package org.songbai.loan.common.finance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yeepay.g3.sdk.yop.client.YopClient3;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;
import com.yeepay.g3.sdk.yop.encrypt.CertTypeEnum;
import com.yeepay.g3.sdk.yop.encrypt.DigitalEnvelopeDTO;
import com.yeepay.g3.sdk.yop.utils.DigitalEnvelopeUtils;
import com.yeepay.g3.sdk.yop.utils.InternalConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.constant.resp.UserRespCode;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Map;
import java.util.TreeMap;

/**
 * json工具类，由于易宝很多地方需要转 所以就建了它
 * @author wjl
 * @date 2018年11月12日 17:00:20
 * @description
 */
public class YiBaoUtil {
	private static final Logger log = LoggerFactory.getLogger(YiBaoUtil.class);
	/**
	 * json -> map
	 * @param response
	 * @return
	 */
	public static Map<String, String> parseResponse(String response) {

		Map<String, String> jsonMap;
		jsonMap = JSON.parseObject(response, new TypeReference<TreeMap<String, String>>() {
		});

		return jsonMap;
	}
	
	/**
	 * 易宝解密
	 * @param result
	 * @return
	 */
	public static Map<String, String> Decrypt(String result,String appKey) {
		// 开始解密
		DigitalEnvelopeDTO dto = new DigitalEnvelopeDTO();
		dto.setCipherText(result);
		PrivateKey privateKey = InternalConfig.getISVPrivateKey(appKey,CertTypeEnum.RSA2048);
		PublicKey publicKey = InternalConfig.getYopPublicKey(CertTypeEnum.RSA2048);
		dto = DigitalEnvelopeUtils.decrypt(dto, privateKey, publicKey);
		return parseResponse(dto.getPlainText());
	}

	/**
	 * 易宝解密
	 * @param result
	 * @return
	 */
	public static Map<String, String> DecryptBackup(String result,PrivateKey privateKey,PublicKey publicKey) {
		// 开始解密
		Map<String, String> map;
		try {
			DigitalEnvelopeDTO dto = new DigitalEnvelopeDTO();
			dto.setCipherText(result);
			dto = DigitalEnvelopeUtils.decrypt(dto, privateKey, publicKey);
			map = parseResponse(dto.getPlainText());
		} catch (Exception e) {
			return null;
		}
		return map;
	}

	public static Map<String, String> request(String url, YopRequest yopRequest){
		try {
			YopResponse yopResponse = YopClient3.postRsa(url, yopRequest);
			if (StringUtils.isBlank(yopResponse.getStringResult())) {
				throw new BusinessException("请求易宝失败");
			}
			return parseResponse(yopResponse.getStringResult());
		} catch (Exception e) {
			log.error("请求易宝时网络错误", e);
			throw new BusinessException(UserRespCode.INTERNET_ERROR,"请求易宝失败");
		}
	}
}
