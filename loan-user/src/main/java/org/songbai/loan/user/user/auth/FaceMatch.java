package org.songbai.loan.user.user.auth;


import com.baidu.aip.face.AipFace;
import com.baidu.aip.face.MatchRequest;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.model.user.UserInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * 人脸对比
 */
@Component
public class FaceMatch {

	private static final Logger log = LoggerFactory.getLogger(FaceMatch.class);

	@Autowired
	private AliyunUtil aliyunUtil;

	@Autowired
	SpringProperties properties;

	AipFace client;


	@PostConstruct
	public void init() {

		String appid = properties.getString("baidu.aip.appid", "14702222");
		String appkey = properties.getString("baidu.aip.appkey", "u2cvoVyDDVM1eA0DLbAhiIWj");
		String secretkey = properties.getString("baidu.aip.secretkey", "uH3YGYFnytjcDvVuu5l5lxkqbPK0oQMy");


		client = new AipFace(appid, appkey, secretkey);

		// 可选：设置网络连接参数
		client.setConnectionTimeoutInMillis(2000);
		client.setSocketTimeoutInMillis(60000);

	}


	public void searchIdcardFace(MultipartFile imageData) {

		try {
			String images = Base64.encodeBase64String(imageData.getBytes());

			HashMap<String, String> options = new HashMap<>();

			options.put("face_field", "glasses,quality,face_type");
			options.put("face_type", "CERT");

			JSONObject jsonObject = client.detect(images, "BASE64", options);

			if (jsonObject.has("error_code") && jsonObject.getInt("error_code") != 0) {
				throw new BusinessException(UserRespCode.AUTH_FAILED, "请拍摄正确的身份证照片");
			}

			String resultJson = jsonObject.getJSONObject("result").getJSONArray("face_list").get(0).toString();

			checkIdcardFace(resultJson);

		} catch (IOException e) {
			log.error("身份证搜索人像失败", e);
		}
	}


	public double faceAuth2(MultipartFile file, String idcardPath) {

		try {
			String idcard = aliyunUtil.getImgBase64ByImgPath(idcardPath);
			String liveImg = Base64.encodeBase64String(file.getBytes());

			MatchRequest req1 = new MatchRequest(idcard, "BASE64");
			req1.setFaceType("CERT");


			MatchRequest req2 = new MatchRequest(liveImg, "BASE64");
			req2.setFaceType("LIVE");


			ArrayList<MatchRequest> requests = new ArrayList<MatchRequest>();
			requests.add(req1);
			requests.add(req2);


			if (log.isDebugEnabled()) {
				log.debug("百度 idcard ：{}", idcard);
				log.debug("百度 liveImg ：{}", liveImg);
			}

			JSONObject jsonObject = client.match(requests);

			log.info("百度认证返回：{}", jsonObject);

			if (jsonObject.has("error_code") && jsonObject.getInt("error_code") == 0) {
				return jsonObject.getJSONObject("result").getDouble("score");
			}


		} catch (IOException e) {
			log.error("百度认证失败，idcardpath:" + idcardPath, e);
		}

		throw new BusinessException(UserRespCode.AUTH_FAILED);
	}


	public double faceAuth3(MultipartFile file, UserInfoModel infoModel) {


		try {
			String images = Base64.encodeBase64String(file.getBytes());


			JSONObject jsonObject = client.personVerify(images,
					"BASE64", infoModel.getIdcardNum(), infoModel.getName(), null);

			log.info("百度认证返回：{}", jsonObject);

			if (jsonObject.has("error_code") && jsonObject.getInt("error_code") == 0) {
				return jsonObject.getJSONObject("result").getDouble("score");
			}
		} catch (IOException e) {
			log.error("百度认证失败，infomodel :" + infoModel, e);
		}

		throw new BusinessException(UserRespCode.AUTH_FAILED);

	}


	public boolean faceAuth(MultipartFile file, UserInfoModel infoModel) {

		try {
			Double match = faceAuth2(file, infoModel.getIdcardFrontImg());

			if (match > properties.getInteger("user.face.piccompare", 60)) {
				return true;
			}
		} catch (Exception e) {
			log.info("使用人脸对比认证 发生异常，{}", e.getMessage());
		}

//        try {
//            Double match = faceAuth3(file, infoModel);
//
//            if (match > properties.getInteger("user.face.gacompare", 80)) {
//                return true;
//            }
//        } catch (Exception e) {
//            log.info("使用公安认证 发生异常,{}", e.getMessage());
//        }

		return false;
	}

	private void checkIdcardFace(String resultJson) {
		log.info("百度验证省份证头像返回：{}", resultJson);

		com.alibaba.fastjson.JSONObject resultObj = com.alibaba.fastjson.JSONObject.parseObject(resultJson);


		com.alibaba.fastjson.JSONObject occlusion = resultObj.getJSONObject("quality").getJSONObject("occlusion");


		for (String s : occlusion.keySet()) {
			double occ = occlusion.getDouble(s);

			if (s.equalsIgnoreCase("left_eye") || s.equalsIgnoreCase("right_eye")) {
				if (occ > properties.getDouble("user.face.occlusion.eye", 0.6)) {
					throw new BusinessException(UserRespCode.AUTH_FAILED, "请不要遮挡拍摄");
				}
			} else {
				if (occ > properties.getDouble("user.face.occlusion", 0.2)) {
					throw new BusinessException(UserRespCode.AUTH_FAILED, "请不要遮挡拍摄");
				}
			}
		}


		Double blur = resultObj.getJSONObject("quality").getDouble("blur");

		if (blur > properties.getDouble("user.face.blur", 0.3)) {
			throw new BusinessException(UserRespCode.AUTH_FAILED, "请正对身份证进行拍摄");
		}


		Double illumination = resultObj.getJSONObject("quality").getDouble("illumination");

		if (illumination <= properties.getDouble("user.face.illumination", 80D)) {
			throw new BusinessException(UserRespCode.AUTH_FAILED, "请在光线明亮的地方进行拍摄");
		}

		com.alibaba.fastjson.JSONObject angle = resultObj.getJSONObject("angle");

		double angleYz = properties.getDouble("user.face.angle", 20D);
		for (String s : angle.keySet()) {
			double rpy = Math.abs(angle.getDouble(s));

			if ("roll".equalsIgnoreCase(s)) {
				if (rpy > angleYz && Math.abs(rpy - 90) > angleYz && Math.abs(rpy - 180) > angleYz) {
					throw new BusinessException(UserRespCode.AUTH_FAILED, "请正对身份证进行拍摄");
				}
			} else {
				if (rpy > angleYz) {
					throw new BusinessException(UserRespCode.AUTH_FAILED, "请正对身份证进行拍摄");
				}
			}
		}


		Double completeness = resultObj.getJSONObject("quality").getDouble("completeness");

		if (completeness < properties.getDouble("user.face.completeness", 0.95)) {
			throw new BusinessException(UserRespCode.AUTH_FAILED, "请将身份证置于屏幕正中拍摄");
		}


		Double width = resultObj.getJSONObject("location").getDouble("width");
		Double height = resultObj.getJSONObject("location").getDouble("height");

		if (width < properties.getDouble("user.face.width", 100D)
				|| height < properties.getDouble("user.face.height", 100D)) {
			throw new BusinessException(UserRespCode.AUTH_FAILED, "请靠近身份证进行拍摄");
		}
	}

}