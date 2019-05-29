package org.songbai.loan.risk.moxie.ctrl;


import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.PropertyNamingStrategy;
import com.alibaba.fastjson.parser.ParserConfig;
import com.google.common.base.Strings;
import org.apache.commons.codec.binary.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.loan.constant.risk.RiskConst;
import org.songbai.loan.risk.moxie.carrier.billitem.CarrierBillTask;
import org.songbai.loan.risk.moxie.carrier.billitem.CarrierReportTask;
import org.songbai.loan.risk.moxie.carrier.service.CarrierBillService;
import org.songbai.loan.risk.moxie.carrier.service.CarrierReportService;
import org.songbai.loan.risk.moxie.taobao.model.vo.TaobaoReportTask;
import org.songbai.loan.risk.moxie.taobao.model.vo.TaobaoTask;
import org.songbai.loan.risk.moxie.taobao.service.TaobaoReportService;
import org.songbai.loan.risk.moxie.taobao.service.TaobaoService;
import org.songbai.loan.risk.platform.service.UserDataTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

@RestController
@RequestMapping(value = "/moxie/v1/")
@LimitLess
public class WebHookController {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebHookController.class);

    private static final String HEADER_MOXIE_EVENT = "X-Moxie-Event";
    private static final String HEADER_MOXIE_TYPE = "X-Moxie-Type";
    private static final String HEADER_MOXIE_SIGNATURE = "X-Moxie-Signature";

    @Value("${moxie.signature.secret}")
    private String secret;


    @Autowired
    TaobaoService taobaoService;
    @Autowired
    CarrierBillService carrierBillService;
    @Autowired
    CarrierReportService carrierReportService;
    @Autowired
    TaobaoReportService taobaoReportService;


    @Autowired
    UserDataTaskService taskService;

    private static ParserConfig config = new ParserConfig();

    static {
        config.propertyNamingStrategy = PropertyNamingStrategy.SnakeCase;
    }

    /**
     * 回调接口, moxie通过此endpoint通知账单更新和任务状态更新
     */
    @RequestMapping(value = "/notifications", method = RequestMethod.POST)
    public void notifyUpdateBill(@RequestBody String body, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String eventName = request.getHeader(HEADER_MOXIE_EVENT); //事件类型：task or bill
        String eventType = request.getHeader(HEADER_MOXIE_TYPE); // //业务类型：email、bank、carrier 等
        String signature = request.getHeader(HEADER_MOXIE_SIGNATURE); // //body签名

        LOGGER.info("event name:{} , eventType: {}", eventName, eventType);
        LOGGER.info("request body:" + body);

        if (Strings.isNullOrEmpty(eventName)) {
            writeMessage(response, HttpServletResponse.SC_BAD_REQUEST, "header not found:" + HEADER_MOXIE_EVENT);
            return;
        }

        if (Strings.isNullOrEmpty(eventType)) {
            writeMessage(response, HttpServletResponse.SC_BAD_REQUEST, "header not found:" + HEADER_MOXIE_TYPE);
            return;
        }

        if (Strings.isNullOrEmpty(signature)) {
            writeMessage(response, HttpServletResponse.SC_BAD_REQUEST, "header not found:" + HEADER_MOXIE_SIGNATURE);
            return;
        }

        if (Strings.isNullOrEmpty(body)) {
            writeMessage(response, HttpServletResponse.SC_BAD_REQUEST, "request body is empty");
            return;
        }

        //验签，判断body是否被篡改
//        if (!SignatureUtils.base64Hmac256(secret, body).equals(signature)) {
//            writeMessage(httpServletResponse, HttpServletResponse.SC_BAD_REQUEST, "signature mismatch");
//            return;
//        }

        try {
            //任务提交
            if (StringUtils.equals(eventName.toLowerCase(), "task.submit")) {
                //通知状态变更为 '认证中'
                handleEventForTaskSubmit(body, eventName, eventType);
            }

            if (StringUtils.equals(eventName.toLowerCase(), "task")) {
                handleEventForTaskStart(body, eventName, eventType);
            }

            if (StringUtils.equals(eventName.toLowerCase(), "task.fail")) {
                handleEventForTaskFail(body, eventName, eventType);
            }

            //任务完成的通知处理，其中qq联系人的通知为sns，其它的都为bill
            if (Arrays.asList("bill", "allbill", "sns").contains(eventName.toLowerCase())) {
                //通知状态变更为 '认证完成'
                handleEventForBill(body, eventName, eventType);
            }

            if (StringUtils.equals(eventName.toLowerCase(), "report")) {
                handleEventForReport(body, eventName, eventType);
            }
        } catch (Exception e) {
            LOGGER.error("body convert to object error", e);
        }


        writeMessage(response, HttpServletResponse.SC_CREATED, "default eventtype");
    }


    /**
     * 任务提交
     * 通知状态变更为 '认证中'
     *
     * @param body
     * @param eventType
     */
    private void handleEventForTaskSubmit(String body, String eventName, String eventType) {

        taskService.saveDataTaskForMoxie(body, eventName, eventType, RiskConst.Task.SUBMIT_SUCCESS);
    }

    /**
     * 登录结果
     * {"mobile":"15368098198","timestamp":1476084445670,"result":false,"message":"[CALO-22001-10]-服务密码错误，请确认正确后输入。","user_id":"374791","task_id":"fdda6b30-8eba-11e6-b7e9-00163e10b2cd"}
     *
     * @param body
     * @param eventType
     */
    private void handleEventForTaskStart(String body, String eventName, String eventType) {
        JSONObject map = JSONObject.parseObject(body);
        if (map.containsKey("result")) {
            String result = map.get("result").toString();
            if (StringUtils.equals(result, "false")) {
                if (map.containsKey("message")) {
                    String message = map.get("message") == null ? "未知异常" : map.get("message").toString();
                    //通知状态变更为 '认证失败'
                    //noticeHttp..
                    LOGGER.info("task event. result={}, message={}", result, message);
                }
                taskService.saveDataTaskForMoxie(body, eventName, eventType, RiskConst.Task.AUTH_FAIL);
            } else {
                taskService.saveDataTaskForMoxie(body, eventName, eventType, RiskConst.Task.AUTH_SUCCESS);
            }
        }
    }

    /**
     * 任务过程中的失败
     * 运营商的格式{"mobile":"13429801680","timestamp":1474641874728,"result":false,"message":"系统繁忙，请稍后再试","user_id":"1111","task_id":"3e9ff350-819c-11e6-b7fe-00163e004a23"}
     *
     * @param body
     * @param eventType
     */
    private void handleEventForTaskFail(String body, String eventName, String eventType) {
        JSONObject map = JSONObject.parseObject(body);
        if (map.containsKey("result") && map.containsKey("message")) {
            String result = map.get("result").toString();
            String message = map.get("message") == null ? "未知异常" : map.get("message").toString();
            if (StringUtils.equals(result, "false")) {

                //通知状态变更为 '认证失败'
                //noticeHttp..
                LOGGER.info("task fail event. result={}, message={}", result, message);

                taskService.saveDataTaskForMoxie(body, eventName, eventType, RiskConst.Task.DATA_FAIL);
            } else {
                taskService.saveDataTaskForMoxie(body, eventName, eventType, RiskConst.Task.DATA_SUCCESS);
            }
        }
    }


    /**
     * 订单详细数据
     *
     * @param body
     * @param eventType
     */
    private void handleEventForBill(String body, String eventName, String eventType) {
        switch (eventType.toLowerCase()) {
            case "taobao":
                taobaoService.fetchBill(JSONObject.parseObject(body, TaobaoTask.class));
                break;
            case "carrier":
                carrierBillService.fetchBill(JSONObject.parseObject(body, CarrierBillTask.class));
                break;
        }
        taskService.saveDataTaskForMoxie(body, eventName, eventType, RiskConst.Task.DATA_SUCCESS);
    }

    /**
     * 报表 数据
     *
     * @param body
     * @param eventType
     */
    private void handleEventForReport(String body, String eventName, String eventType) {

        switch (eventType.toLowerCase()) {
            case "carrier":
                carrierReportService.fetchReport(JSONObject.parseObject(body, CarrierReportTask.class, config));
                break;
            case "taobao":
                taobaoReportService.fetchReport(JSONObject.parseObject(body, TaobaoReportTask.class, config));
                break;
        }
        taskService.saveDataTaskForMoxie(body, eventName, eventType, RiskConst.Task.DATA_SUCCESS);
    }


    private void writeMessage(HttpServletResponse response, int status, String content) {
        response.setStatus(status);
        try {
            PrintWriter printWriter = response.getWriter();
            printWriter.write(content);
        } catch (IOException ignored) {
        }
    }
}

