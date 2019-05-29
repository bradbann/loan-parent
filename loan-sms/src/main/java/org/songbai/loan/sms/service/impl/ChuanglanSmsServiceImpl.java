package org.songbai.loan.sms.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.loan.model.sms.SmsNotify;
import org.songbai.loan.model.sms.SmsSender;
import org.songbai.loan.model.sms.SmsTemplate;
import org.songbai.loan.sms.dao.SmsLogDao;
import org.songbai.loan.sms.model.SmsLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("chuanglanSmsService")
public class ChuanglanSmsServiceImpl extends AbstractSmsSenderServiceImpl {

    private static final Logger logger = LoggerFactory.getLogger(ChuanglanSmsServiceImpl.class);
    @Autowired
    SpringProperties springProperties;

    @Autowired
    SmsLogDao smsLogDao;

    /**
     * 创蓝通用的短信验证码统一用子账户来发送
     *
     * @return
     */
    @Override
    protected SmsLog send0(SmsNotify notify, SmsTemplate senderTemplate, SmsSender senderMessage) {
        //Map<String, Object> param = notify.getParam();
        //
        //String message = messageWrapper(senderTemplate, param);
        //String result = "";
        //try {
        //    Map<String, String> data = new HashMap<>();
        //    data.put("account", senderMessage.getAccount());
        //    data.put("password", senderMessage.getPassword());
        //    data.put("phone", notify.getPhone());
        //    data.put("report", "true");
        //    data.put("msg", message);
        //    String url = springProperties.getString("sms.chuanglan.url", "http://smssh1.253.com/msg/send/json");
        //    result = sendSmsByPost(url, JSONObject.toJSONString(data));
        //    JSONObject response = JSONObject.parseObject(result);
        //    if (response.getString("code").equals("0")) {
        //        logger.info("创蓝253发送短信成功,result={}", result);
        //        return null;
        //    } else {
        //        try {
        //            SmsLog smsLog = new SmsLog();
        //            smsLog.setAgencyId(notify.getAgencyId());
        //            smsLog.setVestId(notify.getVestId());
        //            smsLog.setPhone(notify.getPhone());
        //            JSONObject ret = (JSONObject) response.getJSONArray("list").get(0);
        //            smsLog.setMid(ret.getString("mid"));
        //            smsLog.setCreateTime(new Date());
        //            return smsLog;
        //
        //        } catch (Exception e) {
        //            return null;
        //        }
        //    }
        //} catch (Exception e) {
        //    logger.info("创蓝253发送短信异常，msg={}", result);
        //    e.printStackTrace();
        //}
        return null;
    }

    public static String sendSmsByPost(String path, String postContent) {
        URL url = null;
        try {
            url = new URL(path);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");// 提交模式
            httpURLConnection.setConnectTimeout(10000);//连接超时 单位毫秒
            httpURLConnection.setReadTimeout(10000);//读取超时 单位毫秒
            // 发送POST请求必须设置如下两行
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type", "application/json");

            httpURLConnection.connect();
            OutputStream os = httpURLConnection.getOutputStream();
            os.write(postContent.getBytes(StandardCharsets.UTF_8));
            os.flush();

            StringBuilder sb = new StringBuilder();
            int httpRspCode = httpURLConnection.getResponseCode();
            if (httpRspCode == HttpURLConnection.HTTP_OK) {
                // 开始获取数据
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8));
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                return sb.toString();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected String messageWrapper(SmsTemplate senderTemplate, Map<String, Object> param) {
        String message = "【" + senderTemplate.getSign() + "】" + senderTemplate.getTemplate();
        if (param != null) {
            for (String key : param.keySet()) {
                message = message.replace("{s" + key + "}", "" + param.get(key));
            }
        }
        return message;
    }

    @Override
    protected SmsLogDao getSmsLogDao() {
        return smsLogDao;
    }

}
