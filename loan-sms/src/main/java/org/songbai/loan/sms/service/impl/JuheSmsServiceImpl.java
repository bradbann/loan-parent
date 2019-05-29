//package org.songbai.loan.sms.service.impl;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.songbai.cloud.basics.boot.properties.SpringProperties;
//import org.songbai.loan.model.sms.SmsNotify;
//import org.songbai.loan.model.sms.SmsSender;
//import org.songbai.loan.sms.dao.SmsLogDao;
//import org.songbai.loan.sms.model.SmsLog;
//import org.songbai.loan.vo.sms.SenderTemplateVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.io.*;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
///**
// * 聚合平台
// *
// * @author czh
// */
//@Service("juheSmsService")
//public class JuheSmsServiceImpl extends AbstractSmsSenderServiceImpl {
//    private static final Logger logger = LoggerFactory.getLogger(JuheSmsServiceImpl.class);
//
//    @Autowired
//    SpringProperties springProperties;
//
//    @Autowired
//    SmsLogDao smsLogDao;
//
//
//    private static final String DEF_CHATSET = "UTF-8";
//    private static final int DEF_CONN_TIMEOUT = 30000;
//    private static final int DEF_READ_TIMEOUT = 30000;
//    private static String userAgent = "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
//
//    @Override
//    protected SmsLog send0(SmsNotify notify, SenderTemplateVO senderTemplate,
//                                SmsSender senderMessage) {
//        String result = null;
//        Map<String, Object> params = new HashMap<>();// 请求参数
//        String mobile = notify.getPhone();
//        String extraParam = senderTemplate.getExtraParam();
//        String tpl_id = JSON.parseObject(extraParam).getString("templateId");
//        String appkey = senderMessage.getAccount();
//        Map<String, Object> param = notify.getParam();
//        for (Iterator iterator = param.keySet().iterator(); iterator.hasNext(); ) {
//            String key = (String) iterator.next();
//            param.put("#" + key + "#", param.get(key));
//        }
//        params.put("mobile", mobile);// 接收短信的手机号码
//        params.put("tpl_id", tpl_id);// 短信模板ID，请参考个人中心短信模板设置
//        params.put("tpl_value", urlencode(param));// 变量名和变量值对。如果你的变量名或者变量值中带有#&=中的任意一个特殊符号，请先分别进行urlencode编码后再传递，<a
//        // href="http://www.juhe.cn/news/index/id/50"
//        // target="_blank">详细说明></a>
//        params.put("key", appkey);// 应用APPKEY(应用详细页查询)
//        params.put("dtype", "json");// 返回数据的格式,xml或json，默认json
//
//        String url = springProperties.getProperty("sms.juhe.url");
//        try {
//            result = net(url, params, "GET");
//        } catch (Exception e) {
//            logger.error("聚合数据发送短信出错\r\n{}", e);
//        }
//        JSONObject object = JSONObject.parseObject(result);
//        if (object.getInteger("error_code") == 0) {
//            logger.info("result", object.get("result"));
//            return null;
//        } else {
//            logger.info("error_code:{} reason:{}", object.get("error_code"), object.get("reason"));
//            SmsLog errorLog = new SmsLog();
//            errorLog.setSender("聚合数据");
//            errorLog.setError(result);
//            errorLog.setCreateTime(new Date());
//            //errorLog.setChannel(senderMessage.getChannelId());
//            errorLog.setMessage(senderTemplate.getTemplate());
//            errorLog.setAccount(appkey);
//            return errorLog;
//        }
//
//    }
//
//    /**
//     * @param strUrl 请求地址
//     * @param params 请求参数
//     * @param method 请求方法
//     * @return 网络请求字符串
//     * @throws Exception
//     */
//    public static String net(String strUrl, Map params, String method) throws Exception {
//        HttpURLConnection conn = null;
//        BufferedReader reader = null;
//        String rs = null;
//        try {
//            StringBuffer sb = new StringBuffer();
//            if (method == null || method.equals("GET")) {
//                strUrl = strUrl + "?" + urlencode(params);
//            }
//            URL url = new URL(strUrl);
//            conn = (HttpURLConnection) url.openConnection();
//            if (method == null || method.equals("GET")) {
//                conn.setRequestMethod("GET");
//            } else {
//                conn.setRequestMethod("POST");
//                conn.setDoOutput(true);
//            }
//            conn.setRequestProperty("User-agent", userAgent);
//            conn.setUseCaches(false);
//            conn.setConnectTimeout(DEF_CONN_TIMEOUT);
//            conn.setReadTimeout(DEF_READ_TIMEOUT);
//            conn.setInstanceFollowRedirects(false);
//            conn.connect();
//            if (params != null && method.equals("POST")) {
//                try {
//                    DataOutputStream out = new DataOutputStream(conn.getOutputStream());
//                    out.writeBytes(urlencode(params));
//                } catch (Exception e) {
//                    //Ignore
//                }
//            }
//            InputStream is = conn.getInputStream();
//            reader = new BufferedReader(new InputStreamReader(is, DEF_CHATSET));
//            String strRead = null;
//            while ((strRead = reader.readLine()) != null) {
//                sb.append(strRead);
//            }
//            rs = sb.toString();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } finally {
//            if (reader != null) {
//                reader.close();
//            }
//            if (conn != null) {
//                conn.disconnect();
//            }
//        }
//        return rs;
//    }
//
//    // 将map型转为请求参数型
//    public static String urlencode(Map<String, Object> data) {
//        StringBuilder sb = new StringBuilder();
//        for (Map.Entry i : data.entrySet()) {
//            try {
//                sb.append(i.getKey()).append("=").append(URLEncoder.encode(i.getValue() + "", "UTF-8")).append("&");
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
//        }
//        return sb.toString();
//    }
//
//    @Override
//    protected SmsLogDao getSmsLogDao() {
//        return smsLogDao;
//    }
//
//}
