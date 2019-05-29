package org.songbai.loan.call.jms;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.call.dao.CallSenderDao;
import org.songbai.loan.common.finance.HttpTools;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.model.sms.CallSenderModel;
import org.songbai.loan.push.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 外呼用户今日还款
 */
@Component
public class CallUserRepayListener {
    private static final Logger logger = LoggerFactory.getLogger(CallUserRepayListener.class);

    @Autowired
    private CallSenderDao callSenderDao;
    @Autowired
    private OrderDao orderDao;


    @JmsListener(destination = JmsDest.MSG_CALL_REPAY)
    public void callUserRepay() {

        logger.info("智能外呼....开始");

        List<CallSenderModel> callSenderList = callSenderDao.selectStartCallSender();
        if (callSenderList.isEmpty()) {
            logger.info("智能外呼....没有启用的外呼马甲");
            return;
        }

        LocalDate today = LocalDate.now();
        String batchDate =  today.toString().replace("-","");

        for (CallSenderModel senderModel : callSenderList) {

            JSONObject jsonObject = JSONObject.parseObject(senderModel.getData());

            String templateCode = jsonObject.getString("templateCode");
            if (StringUtil.isEmpty(templateCode)) {
                logger.info("智能外呼....,没有配置模板,model={}",senderModel);
                continue;
            }

            String loanChannel = jsonObject.getString("loanChannel");
            if (StringUtil.isEmpty(loanChannel)) {
                logger.info("智能外呼....,没有配置渠道,model={}",senderModel);
                continue;
            }

            int size = 500;
            PageRow page = new PageRow();
            page.setPageSize(size);

            page.initLimit();
            Map<String, String> jobData = new HashMap<>();
            jobData.put("loanChannel", loanChannel);

            while (true) {

                Set<String> phones = orderDao.findCallTodayRepayOrder(today, senderModel.getVestId(), page);
                if (phones.isEmpty()){
                    break;
                }
                sendCall(batchDate, senderModel, templateCode, jobData, phones);

                if (phones.size() < size) {
                    break;
                }
                page.setPage(page.getPage() + 1);
                page.initLimit();

            }
        }
    }

    private void sendCall(String batchDate, CallSenderModel senderModel, String templateCode, Map<String, String> jobData, Set<String> phones) {
        Map<String, String> map = new HashMap<>();
        map.put("corpCode", senderModel.getAppId());
        map.put("accessToken", senderModel.getAppKey());
        map.put("templateCode", templateCode);
        map.put("batchDate", batchDate);
        JSONArray jobList = new JSONArray();
        for (String phone : phones) {
            JSONObject data = new JSONObject();
            data.put("jobId", OrderIdUtil.generateShortUuid());
            data.put("phone", phone);
            data.put("jobData", JSONObject.toJSONString(jobData));
            jobList.add(data);
        }
        map.put("jobList", JSONObject.toJSONString(jobList));

        String result = HttpTools.doPost(senderModel.getUrl(), map);

        JSONObject response = JSONObject.parseObject(result);

        if (response.getString("status").equals("1")) {
            logger.info("智能外呼发送成功,发送手机号为={}", phones);
            logger.info("智能外呼发送成功,result={}", result);

        }else {
            logger.info("智能外呼发送失败,result={},senderModel={},phones={}", result, senderModel, phones);

        }
    }

}

