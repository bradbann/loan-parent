package org.songbai.loan.user.user.auth;

import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.ocr.AipOcr;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.constant.resp.UserRespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;

@Component
public class BaiduOcrUtil {
	private static final Logger logger = LoggerFactory.getLogger(BaiduOcrUtil.class);
	@Autowired
	SpringProperties properties;
	AipOcr client;

	@PostConstruct
	public void init() {

		String appid = properties.getString("baidu.aip.appid", "14690667");
		String appkey = properties.getString("baidu.aip.appkey", "OwNUPu1cLAPexx0UHspWK7IG");
		String secretkey = properties.getString("baidu.aip.secretkey", "GrHx4eg7iCnb0vQ4X6E54dGg9j0aGfqD");


		client = new AipOcr(appid, appkey, secretkey);

		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);
	}


	public JSONObject authIdcard(MultipartFile multipartFile, String idCardSide) {
		// 传入可选参数调用接口
		HashMap<String, String> options = new HashMap<>();
		options.put("detect_direction", "true");//朝向
		options.put("detect_risk", "true");//是否开启身份证风险类型

		// 参数为二进制数组
		try {
			byte[] file = multipartFile.getBytes();
			org.json.JSONObject res = client.idcard(file, idCardSide, options);
			JSONObject obj = JSONObject.parseObject(res.toString());
			checkResult(obj);
			return obj;
		} catch (IOException e) {
			throw new BusinessException(UserRespCode.SYSTEM_EXCEPTION);
		}

	}

	private void checkResult(JSONObject obj) {
		if (obj.get("words_result") == null) {
			logger.info("baiduOcr is error,backResult={}", obj);
			throw new BusinessException(UserRespCode.AUTH_FAILED);
		}

		if (obj.get("edit_tool") != null) {
			//图片不允许编辑
			logger.info("baiduOcr idcard editTool is  error,backResult={}", obj);
//            throw new BusinessException(UserRespCode.IMG_NOT_EDIT);
		}
		if (obj.get("image_status") == null || !obj.get("image_status").equals("normal")) {
			checkImageStatus(obj.getString("image_status"), obj);
		}
//        if (obj.get("risk_type") != null && !obj.get("risk_type").equals("normal")) {
//            checkImageType(obj.getString("risk_type"), obj);
//        }

		logger.info(">>>>idcard auth succ, result={}", obj);
	}

	private void checkImageType(String riskType, JSONObject obj) {
		logger.info("baiduOcr idcard image riskType is error,result={}", obj);
		switch (riskType) {
			case "copy":
				throw new BusinessException(UserRespCode.AUTH_FAILED, "身份证为复印件,请重新拍摄");
			case "temporary":
				throw new BusinessException(UserRespCode.AUTH_FAILED, "身份证为临时身份证,请重新拍摄");
			case "screen":
				throw new BusinessException(UserRespCode.AUTH_FAILED, "身份证为翻拍,请重新拍摄");
			case "unknow":
				throw new BusinessException(UserRespCode.AUTH_FAILED, "识别异常,请重新拍摄");
		}
	}

	private void checkImageStatus(String imageStatus, JSONObject object) {
		logger.info("baiduOct idcard image status is error,result={}", object);
		switch (imageStatus) {
			case "reversed_side":
				throw new BusinessException(UserRespCode.AUTH_FAILED, "请摆正身份证");
			case "non_idcard":
				throw new BusinessException(UserRespCode.AUTH_FAILED, "上传的图片中不包含身份证");
			case "blurred":
				throw new BusinessException(UserRespCode.AUTH_FAILED, "身份证模糊");
			case "over_exposure":
				throw new BusinessException(UserRespCode.AUTH_FAILED, "身份证关键字段反光或过曝");
			case "unknown":
				throw new BusinessException(UserRespCode.AUTH_FAILED, "识别异常,请重新拍摄");
		}
	}

}
