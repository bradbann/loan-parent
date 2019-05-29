package org.songbai.loan.common.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.utils.math.Arith;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.common.util.MD5Util;
import org.songbai.loan.common.util.SslUtils;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.finance.JhPayModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 聚合支付帮助类
 */
@Component
public class JhPayHelper {
    private static Logger logger = LoggerFactory.getLogger(JhPayHelper.class);
    private final static String noncestr = "songbai2018";// 密言,随机参数
    //    final static String merid = "027007S190124004";// 分配的商户号
//    final static String key = "9yVlglsl0ZkcV3XaiCxyS8GEEdffoKJy";// 商户号对应的密钥
    private final static String queryUrl = "http://jh.chinambpc.com/api/queryOrder";// 订单查询地址
    private final static String aliPayUrl = "https://alipay.3c-buy.com/api/createOrder";//支付宝请求地址
    private final static String aliScanPayUrl = "https://alipay.3c-buy.com/api/createPcOrder";//支付宝扫码地址
    private final static String wxScanPayUrl = "https://alipay.3c-buy.com/api/createWxOrder";//微信扫码请求地址


    @Autowired
    SpringProperties properties;
    @Autowired
    ComAgencyService comAgencyService;


    public String createPayUrl(OrderModel orderModel, String payCode, String requestId) {
        String orderTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());// 下单时间
        String orderMoney = FormatUtil.formatDouble2(Arith.subtract(2, orderModel.getPayment(), orderModel.getAlreadyMoney()));// 订单金额

        String id = orderModel.getOrderNumber();//不参与验参

        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("merchantOutOrderNo", requestId);
        AgencyModel agencyModel = comAgencyService.findAgencyById(orderModel.getAgencyId());
        if (agencyModel == null || StringUtils.isEmpty(agencyModel.getJhpayMerid())) {
            throw new BusinessException(UserRespCode.PARAM_ERROR);
        }
        paraMap.put("merid", agencyModel.getJhpayMerid());
        paraMap.put("noncestr", noncestr);
        String notifyUrl = properties.getString("user.jh.pay.nofity", "http://223.93.144.36:3002/user/jhPayCallBack/jhPayNotify.do");
        paraMap.put("notifyUrl", notifyUrl);
        paraMap.put("orderMoney", orderMoney);
        paraMap.put("orderTime", orderTime);

        String sign = getMd5Sign(paraMap, agencyModel.getJhpayKey());
        String param = formatUrlMap(paraMap, true, false);
        String url = aliPayUrl;
        if (StringUtils.isNotBlank(payCode) && payCode.equals(FinanceConstant.PayPlatform.WXPAY.code))
            url = wxScanPayUrl;

        //将此URL送至APP前端页面或手机浏览器打开，即可自动调起支付宝(需要安装)发起支付
        return url + "?" + param + "&sign=" + sign + "&id=" + id;
    }

    public String createScanPayUrl(OrderModel orderModel, String payCode, String requestId) {
        String orderTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());// 下单时间
        String orderMoney = FormatUtil.formatDouble2(Arith.subtract(2, orderModel.getPayment(), orderModel.getAlreadyMoney()));// 订单金额

        String id = orderModel.getOrderNumber();//不参与验参

        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("merchantOutOrderNo", requestId);
        AgencyModel agencyModel = comAgencyService.findAgencyById(orderModel.getAgencyId());
        if (agencyModel == null || StringUtils.isEmpty(agencyModel.getJhpayMerid())) {
            throw new BusinessException(UserRespCode.PARAM_ERROR);
        }
        paraMap.put("merid", agencyModel.getJhpayMerid());
        paraMap.put("noncestr", noncestr);
        String notifyUrl = properties.getString("user.jh.pay.nofity", "http://223.93.144.36:3002/user/jhPayCallBack/jhPayNotify.do");
        paraMap.put("notifyUrl", notifyUrl);
        paraMap.put("orderMoney", orderMoney);
        paraMap.put("orderTime", orderTime);

        String sign = getMd5Sign(paraMap, agencyModel.getJhpayKey());
        String param = formatUrlMap(paraMap, true, false);
        String url = aliScanPayUrl;
        if (StringUtils.isNotBlank(payCode) && payCode.equals(FinanceConstant.PayPlatform.WXPAY.code))
            url = wxScanPayUrl;

        //将此URL送至APP前端页面或手机浏览器打开，即可自动调起支付宝(需要安装)发起支付
        return url + "?" + param + "&sign=" + sign + "&id=" + id;
    }

    private static String getMd5Sign(Map<String, String> paraMap, String key) {
        String stringA = formatUrlMap(paraMap, false, false);
        String stringsignTemp = stringA + "&key=" + key;
        return MD5Util.getMD5String(stringsignTemp).toLowerCase();
    }

    public boolean checkSign(JhPayModel model, Integer agencyId) {
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("merchantOutOrderNo", model.getMerchantOutOrderNo());
        paraMap.put("merid", model.getMerid());
        paraMap.put("msg", model.getMsg());
        paraMap.put("noncestr", model.getNoncestr());
        paraMap.put("orderNo", model.getOrderNo());
        paraMap.put("payResult", model.getPayResult().toString());
        AgencyModel agencyModel = comAgencyService.findAgencyById(agencyId);
        String sign = getMd5Sign(paraMap, agencyModel.getJhpayKey());
        return StringUtils.equals(sign, model.getSign());
    }

    public JhPayModel queryOrderStatus(String merchantOutOrderNo, Integer agencyId) {
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("merchantOutOrderNo", merchantOutOrderNo);
        AgencyModel agencyModel = comAgencyService.findAgencyById(agencyId);
        paraMap.put("merid", agencyModel.getJhpayMerid());
        paraMap.put("noncestr", noncestr);
        String sign = getMd5Sign(paraMap, agencyModel.getJhpayKey());

        String paramStr = formatUrlMap(paraMap, false, false);
//        paraMap.put("sign", sign);
        paramStr = paramStr + "&sign=" + sign;
        String queryResult = sendPostByParamStr(queryUrl, paramStr, "UTF-8");
        JSONObject resultJson = JSON.parseObject(queryResult);
        if (resultJson.get("code") != null) {
            if (logger.isInfoEnabled())
                logger.info(">>>>jhPay queryOrderStatus result={},params={}", resultJson, paramStr);
        }

        return JSONObject.parseObject(queryResult, JhPayModel.class);
    }

    public static String sendPost(String url, Map<String, String> paraMap) {
        String result = "";
        URL postUrl = null;
        try {
            postUrl = new URL(url);
        } catch (MalformedURLException e) {
            logger.error("jhPayPost new url is fail");
            throw new BusinessException(UserRespCode.SYSTEM_EXCEPTION);
        }
        HttpURLConnection connection = null;
        long start = System.currentTimeMillis();
        try {
            connection = (HttpURLConnection) postUrl.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);
            connection.setInstanceFollowRedirects(true);

            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.connect();
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            String content = "";

            if (paraMap != null) {
                for (String key : paraMap.keySet()) {
                    content += key + "=" + URLEncoder.encode(paraMap.get(key), "UTF-8") + "&";
                }
            }

            if (StringUtils.isNotBlank(content))
                content = content.substring(0, content.lastIndexOf('&'));

            out.writeBytes(content);
            out.flush();
            out.close();

            InputStream httpIns = connection.getInputStream();
            result = IOUtils.toString(httpIns, Charset.forName("UTF-8"));
            if (logger.isDebugEnabled())
                logger.debug(">>>>jhPayPost result={}", JSONObject.parse(result));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        if (logger.isDebugEnabled())
            logger.debug(">>>>jhPayPost 所耗时间time={}", System.currentTimeMillis() - start);
        return result;
    }

    public static String sendPostByParamStr(String url, String param, String charset) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        String line;
        StringBuffer sb = new StringBuffer();
        try {
            URL realUrl = new URL(url);
            //如果是https请求,忽略SSL证书
            if ("https".equalsIgnoreCase(realUrl.getProtocol())) {
                SslUtils.ignoreSsl();
            }
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性 设置请求格式
            conn.setRequestProperty("contentType", charset);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            //设置超时时间
            conn.setConnectTimeout(60000);
            conn.setReadTimeout(60000);
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应    设置接收格式
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
            while ((line = in.readLine()) != null) {
                sb.append(line);
            }
            result = sb.toString();
        } catch (Exception e) {
            System.out.println("发送 POST请求出现异常!" + e);
            e.printStackTrace();
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }


    private static String formatUrlMap(Map<String, String> paraMap, boolean urlEncode, boolean keyToLower) {
        String buff = "";
        try {
            List<Map.Entry<String, String>> infoIds = new ArrayList<>(paraMap.entrySet());
            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            infoIds.sort(Comparator.comparing(o -> (o.getKey())));
            // 构造URL 键值对的格式
            StringBuilder buf = new StringBuilder();
            for (Map.Entry<String, String> item : infoIds) {
                if (StringUtils.isNotEmpty(item.getKey())) {
                    String key = item.getKey();
                    String val = item.getValue();
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower) {
                        buf.append(key.toLowerCase()).append("=").append(val);
                    } else {
                        buf.append(key).append("=").append(val);
                    }
                    buf.append("&");
                }

            }
            buff = buf.toString();
            if (!buff.isEmpty()) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            return null;
        }
        return buff;
    }


}
