package org.songbai.loan.common.finance;

import org.apache.commons.httpclient.NameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.model.finance.PlatformConfig;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author: wjl
 * @date: 2018/11/23 15:51
 * Description:
 */
public class ChangJieUtil {
	private static final Logger log = LoggerFactory.getLogger(ChangJieUtil.class);

	private static final String signType = "RSA";
	private static final String charset = "UTF-8";

	public static String request(PlatformConfig config, Map<String, String> map) {
		String str = PaySignUtil.getFinalMap(map);
		String sign = PaySignUtil.generateSign(str, config.getPrivateKey(), charset);
		map.put("Sign", sign);
		map.put("SignType", signType);
		try {
			return ChangJieUtil.buildRequest(map, signType, config.getPrivateKey(), charset, config.getUrl());
		} catch (Exception e) {
			log.error("请求畅捷时网络错误",e);
			throw new BusinessException(UserRespCode.INTERNET_ERROR);
		}
	}

	public static String buildRequest(Map<String, String> sParaTemp, String signType, String key, String inputCharset,
	                                  String gatewayUrl) throws Exception {
		// 待请求参数数组
		Map<String, String> sPara = buildRequestPara(sParaTemp, signType, key, inputCharset);
		HttpProtocolHandler httpProtocolHandler = HttpProtocolHandler.getInstance();
		HttpRequest request = new HttpRequest(HttpResultType.BYTES);
		// 设置编码集
		request.setCharset(inputCharset);
		request.setMethod(HttpRequest.METHOD_POST);
		request.setParameters(generatNameValuePair(createLinkRequestParas(sPara), inputCharset));
		request.setUrl(gatewayUrl);
		if (sParaTemp.get("Service").equalsIgnoreCase("nmg_quick_onekeypay") || sParaTemp.get("Service").equalsIgnoreCase("nmg_nquick_onekeypay")) {
			throw new BusinessException(UserRespCode.INTERNET_ERROR);
		}
		// 返回结果处理
		HttpResponse response = httpProtocolHandler.execute(request, null, null);
		if (response == null) {
			throw new BusinessException(UserRespCode.INTERNET_ERROR);
		}
		return response.getStringResult();
	}

	public static Map<String, String> buildRequestPara(Map<String, String> sParaTemp, String signType, String key,
	                                                   String inputCharset) throws Exception {
		// 除去数组中的空值和签名参数
		Map<String, String> sPara = paraFilter(sParaTemp);
		// 生成签名结果
		String mysign = "";
		mysign = buildRequestByRSA(sPara, key, inputCharset);
		// 签名结果与签名方式加入请求提交参数组中
		System.out.println("Sign:" + mysign);
		sPara.put("Sign", mysign);
		sPara.put("SignType", signType);

		return sPara;
	}

	/**
	 * 除去数组中的空值和签名参数
	 *
	 * @param sArray 签名参数组
	 * @return 去掉空值与签名参数后的新签名参数组
	 */
	public static Map<String, String> paraFilter(Map<String, String> sArray) {

		Map<String, String> result = new HashMap<String, String>();

		if (sArray == null || sArray.size() <= 0) {
			return result;
		}

		for (String key : sArray.keySet()) {
			String value = sArray.get(key);
			if (value == null || value.equals("") || key.equalsIgnoreCase("Sign") || key.equalsIgnoreCase("SignType") || key.equalsIgnoreCase("sign_type")) {
				continue;
			}
			result.put(key, value);
		}

		return result;
	}

	/**
	 * 生成RSA签名结果
	 *
	 * @param sPara 要签名的数组
	 * @return 签名结果字符串
	 */
	public static String buildRequestByRSA(Map<String, String> sPara, String privateKey, String inputCharset)
			throws Exception {
		String prestr = createLinkString(sPara, false); // 把数组所有元素，按照“参数=参数值”的模式用“&”字符拼接成字符串
		String mysign = "";
		mysign = RSA.sign(prestr, privateKey, inputCharset);
		return mysign;
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 *
	 * @param params 需要排序并参与字符拼接的参数组
	 * @param encode 是否需要urlEncode
	 * @return 拼接后字符串
	 */
	public static String createLinkString(Map<String, String> params, boolean encode) {

		params = paraFilter(params);

		List<String> keys = new ArrayList<String>(params.keySet());
		Collections.sort(keys);

		String prestr = "";

		String charset = params.get("InputCharset");
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value = params.get(key);
			if (encode) {
				try {
					value = URLEncoder.encode(value, charset);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			if (i == keys.size() - 1) {// 拼接时，不包括最后一个&字符
				prestr = prestr + key + "=" + value;
			} else {
				prestr = prestr + key + "=" + value + "&";
			}
		}

		return prestr;
	}

	/**
	 * MAP类型数组转换成NameValuePair类型
	 *
	 * @param properties MAP类型数组
	 * @return NameValuePair类型数组
	 */
	private static NameValuePair[] generatNameValuePair(Map<String, String> properties, String charset)
			throws Exception {
		NameValuePair[] nameValuePair = new NameValuePair[properties.size()];
		int i = 0;
		for (Map.Entry<String, String> entry : properties.entrySet()) {
			// nameValuePair[i++] = new NameValuePair(entry.getKey(),
			// URLEncoder.encode(entry.getValue(),charset));
			nameValuePair[i++] = new NameValuePair(entry.getKey(), entry.getValue());
		}
		return nameValuePair;
	}

	/**
	 * 把数组所有元素排序，并按照“参数=参数值”的模式用“&”字符拼接成字符串
	 *
	 * @param params 需要排序并参与字符拼接的参数组
	 *               是否需要urlEncode
	 * @return 拼接后字符串
	 */
	public static Map<String, String> createLinkRequestParas(Map<String, String> params) {
		Map<String, String> encodeParamsValueMap = new HashMap<String, String>();
		List<String> keys = new ArrayList<String>(params.keySet());
		String charset = params.get("InputCharset");
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			String value;
			try {
				value = URLEncoder.encode(params.get(key), charset);
				encodeParamsValueMap.put(key, value);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}

		return encodeParamsValueMap;
	}

	/**
	 * 畅捷回调验签
	 */
	public static Boolean verifySign(Map<String, String> map, String sign, String publicKey, String charset) {
		String text = createLinkString(map, false);
		try {
			if (!RSA.verify(text, sign, publicKey, charset)) {
				log.error("畅捷验签失败，参数为：{},Sign：{},map：{},public：{}", text, sign, map, publicKey);
				return false;
			}
			return true;
		} catch (Exception e) {
			log.error("畅捷验签异常！！！，参数为：{},Sign：{},map：{},public：{}", text, sign, map, publicKey);
		}
		return false;
	}
}
