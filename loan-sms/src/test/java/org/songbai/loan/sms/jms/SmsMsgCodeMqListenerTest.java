package org.songbai.loan.sms.jms;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;
import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.loan.call.jms.CallUserRepayListener;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.model.sms.PushModel;
import org.songbai.loan.model.sms.SmsNotify;
import org.songbai.loan.push.jms.PushMqListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: qmw
 * Date: 2018/10/29 上午11:59
 */
@RunWith(SpringRunner.class)
@SpringBootTest

public class SmsMsgCodeMqListenerTest {
    @Autowired
    private SmsMsgCodeMqListener mqListener;
    @Autowired
    private PushMqListener pushMqListener;
    @Autowired
    private SmsLogListener logListener;
    @Autowired
    private CallUserRepayListener callUserRepayListener;
    @Test
    public void call() {
        callUserRepayListener.callUserRepay();
        //
        //SmsNotify smsNotify = new SmsNotify();
        ////smsNotify.setTeleCode("86");
        //smsNotify.setPhone("18158693676");
        //smsNotify.setSmsType(0);
        //mqListener.onMessage(JSONObject.toJSONString(smsNotify));

    }
    @Test
    public void sendMsg() {
        SmsNotify smsNotify = new SmsNotify();
        //smsNotify.setTeleCode("86");
        smsNotify.setPhone("18158693676");
        smsNotify.setSmsType(0);
        mqListener.onMessage(JSONObject.toJSONString(smsNotify));

    }

    @Test
    public void sendMsgPaoPao() {
        Map<String, Object> map = new HashMap<>();
        Object param = JSON.parse("{\"code\":\"" + 1234 + "\"}");
        map.put("ip", "127.0.0.1");
        map.put("phone", "13615716121");
        map.put("param", param);
        map.put("smsType", 0);
        map.put("agencyId", 18);
        map.put("vestId", 2);
        map.put("createTime", System.currentTimeMillis());
        String message = JSON.toJSONString(map);
        mqListener.onMessage(message);
    }


    public static void main(String[] args) {
        String httpResponse = testSend();
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(httpResponse);
            int error_code = jsonObj.getInt("error");
            String error_msg = jsonObj.getString("msg");
            if (error_code == 0) {
                System.out.println("Send message success.");
            } else {
                System.out.println("Send message failed,code is " + error_code + ",msg is " + error_msg);
            }
        } catch (JSONException ex) {
        }

        httpResponse = testStatus();
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(httpResponse);
            int error_code = jsonObj.getInt("error");
            if (error_code == 0) {
                int deposit = jsonObj.getInt("deposit");
                System.out.println("Fetch deposit success :" + deposit);
            } else {
                String error_msg = jsonObj.getString("msg");
                System.out.println("Fetch deposit failed,code is " + error_code + ",msg is " + error_msg);
            }
        } catch (JSONException ex) {
        }


    }

    private static String testSend() {
        // just replace key here
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(
                "api", "key-d2394830316eab167f71251347e380ec"));
        WebResource webResource = client.resource("http://voice-api.luosimao.com/v1/verify.json");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        formData.add("mobile", "18158693676");
        //formData.add("mobile", "18858279220");
        formData.add("code", "123456");
        ClientResponse response = webResource.type(MediaType.APPLICATION_FORM_URLENCODED).
                post(ClientResponse.class, formData);
        String textEntity = response.getEntity(String.class);
        int status = response.getStatus();
        //System.out.print(textEntity);
        //System.out.print(status);
        return textEntity;
    }

    private static String testStatus() {
        Client client = Client.create();
        client.addFilter(new HTTPBasicAuthFilter(
                "api", "key-d609b769db914a4d959bae3414ed1f7X"));
        WebResource webResource = client.resource("http://voice-api.luosimao.com/v1/status.json");
        MultivaluedMapImpl formData = new MultivaluedMapImpl();
        ClientResponse response = webResource.get(ClientResponse.class);
        String textEntity = response.getEntity(String.class);
        int status = response.getStatus();
        //System.out.print(status);
        //System.out.print(textEntity);
        return textEntity;
    }

    @Test
    public void sendMsgUpMongo() {

        logListener.msgSyncUp();

        System.out.println();

        System.out.println();

    }

    @Test
    public void sendMsgUp() {

        Map<String, String> data = new HashMap<>();
        data.put("action", "mo");
        data.put("account", "922186");
        data.put("password", "77t47n");
        data.put("rt", "json");
        String url = "http://ppyw.paopao106.cn:7862/sms";

        //String result = HttpTools.doPost(url, data);
        String result = "{\"status\":\"0\",\"balance\":4745680,\"list\":[{\"flag\":0,\"mid\":\"0000000005C56A1F\",\"spid\":\"922186\",\"accessCode\":\"10690186\",\"mobile\":\"13658280002\",\"content\":\"123\",\"time\":\"2019-01-22 10:26:09\"},{\"flag\":0,\"mid\":\"0000000005CE330F\",\"spid\":\"922186\",\"accessCode\":\"10690186\",\"mobile\":\"18458215675\",\"content\":\"齐慕伟大本\",\"time\":\"2019-01-22 11:45:25\"},{\"flag\":0,\"mid\":\"1B2E11C0000B78E6\",\"spid\":\"922186\",\"accessCode\":\"10690106906707\",\"mobile\":\"18158693676\",\"content\":\"测试上行\",\"time\":\"2019-01-22 11:33:07\"}]}";
        JSONObject response = JSONObject.parseObject(result);

        System.out.println(result);

    }

    @Test
    public void pushMsg() {
        PushModel pushModel = new PushModel();
        pushModel.setMsg("123123");

        pushModel.setClassify(PushEnum.Classify.SYSTEM.value);
        pushModel.setDataId("1232131");
        //pushModel.setAgencyId(0);
        pushModel.setDeviceId("59073c0a2f2244c8c9bbb72be2c0c82d");
        //pushMqListener.onMessage(pushModel);

    }

}