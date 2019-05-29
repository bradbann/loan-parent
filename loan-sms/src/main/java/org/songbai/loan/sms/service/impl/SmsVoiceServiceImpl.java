package org.songbai.loan.sms.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.model.sms.SmsNotify;
import org.songbai.loan.model.sms.SmsVoiceModel;
import org.songbai.loan.sms.service.SmsVoiceService;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.MediaType;

/**
 * Author: qmw
 * Date: 2019/2/15 2:39 PM
 */
@Component
public class SmsVoiceServiceImpl implements SmsVoiceService {

    private static final Logger logger = LoggerFactory.getLogger(SmsVoiceServiceImpl.class);

    @Override
    public void sendVoiceMsg(SmsVoiceModel voiceModel, SmsNotify notify) {

        JSONObject jsonObject = JSONObject.parseObject(voiceModel.getData());

        String key = jsonObject.getString("key");
        String sendUrl = jsonObject.getString("sendUrl");
        String statusUrl = jsonObject.getString("statusUrl");

        if (StringUtil.isEmpty(key) || StringUtil.isEmpty(sendUrl) || StringUtil.isEmpty(statusUrl)) {
            logger.info("luosimao>>>解析数据配置不全,{}", voiceModel);
            return;
        }

        String httpResponse = send(key, sendUrl, notify);
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(httpResponse);
            int error_code = jsonObj.getInt("error");
            String error_msg = jsonObj.getString("msg");
            if (error_code == 0) {
                logger.info("Send void message success.");
            } else {
                logger.info("Send message failed,code is={} msg is ={}", error_code, error_msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

        httpResponse = getStatus(key, statusUrl);
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(httpResponse);
            int error_code = jsonObj.getInt("error");
            if (error_code == 0) {
                int deposit = jsonObj.getInt("deposit");
                logger.info("Fetch deposit success :deposit={}", deposit);
            } else {
                String error_msg = jsonObj.getString("msg");
                logger.info("Fetch deposit failed,code is={} msg is ={}", error_code, error_msg);
            }
        } catch (JSONException ex) {
            ex.printStackTrace();
        }

    }

    private String send(String key, String sendUrl, SmsNotify notify) {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", "key-" + key));
        WebResource webResource = client.resource(sendUrl);

        MultivaluedMapImpl formData = new MultivaluedMapImpl();

        formData.add("mobile", notify.getPhone());
        formData.add("code", notify.getVoiceCode());

        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).post(ClientResponse.class, formData);
        return response.getEntity(String.class);
    }

    private String getStatus(String key, String statusUrl) {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter("api", "key-" + key));
        WebResource webResource = client.resource(statusUrl);
        ClientResponse response = webResource.get(ClientResponse.class);
        return response.getEntity(String.class);
    }


}
