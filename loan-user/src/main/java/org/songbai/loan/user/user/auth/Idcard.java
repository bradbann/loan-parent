package org.songbai.loan.user.user.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.utils.regular.Regular;
import org.songbai.loan.constant.resp.UserRespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * 身份证识别
 */
@Component
public class Idcard {

	private static final Logger log = LoggerFactory.getLogger(Idcard.class);

	@Autowired
	private AliyunUtil aliyunUtil;
	@Autowired
	private AuthService authService;

	/**
	 * 重要提示代码中所需工具类
	 * FileUtil,Base64Util,HttpUtil,GsonUtils请从
	 * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
	 * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
	 * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
	 * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
	 * 下载
	 */
	public Map<String, String> IdcardAuth(String filePath, String type) {
		// 身份证识别url
		String idcardIdentificate = "https://aip.baidubce.com/rest/2.0/ocr/v1/idcard";
		String result = "";
		try {
			String imgStr = aliyunUtil.getImgBase64ByImgPath(filePath);
			// 识别身份证正面id_card_side=front;识别身份证背面id_card_side=back;
			String params = "id_card_side=" + type + "&" + URLEncoder.encode("image", "UTF-8") + "="
					+ URLEncoder.encode(imgStr, "UTF-8") + "&" + "detect_risk=true";
			/**
			 * 线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
			 */
			String accessToken = authService.getAuth();
			result = HttpUtil.post(idcardIdentificate, accessToken, params);
		} catch (Exception e) {
			log.info("调取百度OCR身份证识别接口失败");
			throw new BusinessException(UserRespCode.INTERNET_ERROR);
		}
		log.info("百度OCR身份证接口识别结果：{}", result);
		if (!result.contains("result")) {
			throw new BusinessException(UserRespCode.AUTH_FAILED);
		}
		//处理返回结果
		JSONObject jsonObject = JSON.parseObject(result).getJSONObject("words_result");
		Map<String, String> map = new HashMap<>();
		if (type.equalsIgnoreCase("front")) {
			map.put("address", jsonObject.getJSONObject("住址").getString("words"));
			String idcard = jsonObject.getJSONObject("公民身份号码").getString("words");
			String name = jsonObject.getJSONObject("姓名").getString("words");
			String root = jsonObject.getJSONObject("民族").getString("words");
			String sex = jsonObject.getJSONObject("性别").getString("words");
			if (StringUtils.isNotBlank(idcard) && Regular.checkIdCardMatch(idcard)
					&& StringUtils.isNotBlank(name) && StringUtils.isNotBlank(root)
					&& StringUtils.isNotBlank(sex) && sex.length() == 1) {
				map.put("idcard", idcard);
				map.put("name", name);
				map.put("root", root);
				map.put("sex", sex);
			} else {
				throw new BusinessException(UserRespCode.AUTH_FAILED);
			}
		} else {
			String where = jsonObject.getJSONObject("签发机关").getString("words");
			String start = jsonObject.getJSONObject("签发日期").getString("words");
			String end = jsonObject.getJSONObject("失效日期").getString("words");
			if (StringUtils.isNotBlank(where) && StringUtils.isNotBlank(start) && StringUtils.isNotBlank(end)) {
				map.put("where", where);
				map.put("validate", start + "-" + end);
			} else {
				throw new BusinessException(UserRespCode.AUTH_FAILED);
			}
		}
		return map;
	}
}
