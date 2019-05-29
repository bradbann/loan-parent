package org.songbai.loan.user.user.controller;

import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.helper.upload.AliyunOssHelper;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.user.UserUtil;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.risk.RiskJmsDest;
import org.songbai.loan.model.statistic.dto.UserStatisticDTO;
import org.songbai.loan.model.user.AuthenticationModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.user.auth.AliyunUtil;
import org.songbai.loan.user.user.auth.FaceMatch;
import org.songbai.loan.user.user.dao.AuthenticationDao;
import org.songbai.loan.user.user.dao.UserInfoDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsMessagingTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 人脸识别认证
 *
 * @author wjl
 * @date 2018年11月06日 20:09:37
 * @description
 */
@RestController
@RequestMapping("/userFace")
public class UserFaceController {
	private static final Logger log = LoggerFactory.getLogger(UserFaceController.class);
	@Autowired
	private FaceMatch faceMatch;
	@Autowired
	private ComUserService comUserService;
	@Autowired
	private AuthenticationDao authenticationDao;
	@Autowired
	private JmsMessagingTemplate jmsMessagingTemplate;
	@Autowired
	private SpringProperties springProperties;

	@Autowired
	private AliyunUtil aliyunUtil;

	@Autowired
	private AliyunOssHelper aliyunOssHelper;

	@Autowired
	private UserInfoDao userInfoDao;
	@Autowired
	private JmsTemplate jmsTemplate;

	private static Random random = new Random();

	@PostMapping("/auth")
	public Response auth(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			throw new BusinessException(UserRespCode.UPLOAD_DATA_NULL);
		}
		Integer userId = UserUtil.getUserId();
		AuthenticationModel authenticationModel = authenticationDao.selectById(userId);
		if (authenticationModel.getFaceStatus() == 1) {
			throw new BusinessException(UserRespCode.NOT_REPEAT_AUTH);
		}
		saveUserLivingImg(file, userId);

		UserInfoModel infoModel = comUserService.findUserInfoByUserId(userId);
//        double matchCode = faceMatch.faceAuth2(file, infoModel.getIdcardFrontImg());
		boolean matches = faceMatch.faceAuth(file, infoModel);

		log.info("userId[{}] 身份证对比，百度人会结果:{} ", userId, matches);

		if (matches) {
			AuthenticationModel entity = new AuthenticationModel();
			entity.setUserId(userId);
			entity.setFaceStatus(1);
			entity.setMoney(authenticationModel.getMoney() + springProperties.getInteger("user.auth.face", 200));
			entity.setFaceTime(new Date());
			authenticationDao.updateById(entity);
			//认证成功，发送jms给风控
			UserModel userModel = comUserService.selectUserModelById(userId, 0);
			Map<String, String> map = new HashMap<>();
			map.put("userId", userModel.getThirdId() + "");
			map.put("phone", userModel.getPhone());
			map.put("name", infoModel.getName());
			map.put("idCard", infoModel.getIdcardNum());
			jmsMessagingTemplate.convertAndSend(RiskJmsDest.RISK_USER_MOXIE_REPORT, JSON.parseObject(JSON.toJSONString(map)));

			UserStatisticDTO dto = new UserStatisticDTO();
			dto.setRegisterDate(Date8Util.date2LocalDate(userModel.getCreateTime()));
			dto.setAgencyId(userModel.getAgencyId());
            dto.setChannelCode(userModel.getChannelCode());
            dto.setActionDate(LocalDate.now());
			dto.setIsFace(CommonConst.YES);
            dto.setVestId(userModel.getVestId());
			jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
			log.info(">>>>发送统计,用户行为(活体认证)jms ,data={}", dto);
			return Response.success("认证成功");
		}

		throw new BusinessException(UserRespCode.AUTH_FAILED);
	}


	private void saveUserLivingImg(@RequestParam("file") MultipartFile file, Integer userId) {
		String string = file.getOriginalFilename().toLowerCase();
		String fileName = aliyunUtil.generateDateKey(userId, "_living_" + random.nextInt(9999) + string.substring(string.lastIndexOf(".")));//生成图片名称
		String filePath = aliyunOssHelper.innerSaveInputStream(fileName, file);

		UserInfoModel update = new UserInfoModel();

		update.setUserId(userId);
		update.setLivingImg(filePath);

		userInfoDao.updateById(update);
	}

}
