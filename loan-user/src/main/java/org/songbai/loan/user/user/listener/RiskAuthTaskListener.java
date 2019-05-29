package org.songbai.loan.user.user.listener;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.risk.RiskConst;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.model.statistic.dto.UserStatisticDTO;
import org.songbai.loan.model.user.AuthenticationModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.user.dao.AuthenticationDao;
import org.songbai.loan.vo.risk.TaskNotifyVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

/**
 * 魔蝎认真状态更新
 */
@Component
@Slf4j
public class RiskAuthTaskListener {
	private static final Logger logger = LoggerFactory.getLogger(RiskAuthTaskListener.class);

	@Autowired
	private AuthenticationDao authenticationDao;
	@Autowired
	private SpringProperties springProperties;

	@Autowired
	ComUserService userService;
	@Autowired
	private JmsTemplate jmsTemplate;

	/**
	 * loan-risk-service.TaskNotifyVO
	 */
	@JmsListener(destination = JmsDest.RISK_DATA_NOTIFY)
	@Transactional
	public void authTask(String msg) {

		log.info("receive user task and update auth status :{}", msg);


		TaskNotifyVO notifyVO = JSONObject.parseObject(msg, TaskNotifyVO.class);

		UserModel userModel = userService.selectUserModelByThridId(notifyVO.getUserId());

		if (userModel == null) {
			log.info("not found user info and not update : {}", notifyVO);
			return;
		}

		if (notifyVO.getStatus() == RiskConst.Task.SUBMIT_SUCCESS.code) {
			if (notifyVO.getSources().equals(VariableConst.VAR_SOURCE_MOXIE_TAOBAO)
					|| notifyVO.getSources().equals(VariableConst.VAR_SOURCE_MOXIE_TAOBAO_REPORT)) {
				updateTaobaoStatus(userModel.getId(), 3);
			} else if (notifyVO.getSources().equals(VariableConst.VAR_SOURCE_MOXIE_CARRIER)
					|| notifyVO.getSources().equals(VariableConst.VAR_SOURCE_MOXIE_CARRIER_REPORT)) {
				// 提交
				updateMobileStatus(userModel.getId(), 3);
			}
		}

		if (notifyVO.getStatus() == RiskConst.Task.SUBMIT_FAIL.code
				|| notifyVO.getStatus() == RiskConst.Task.AUTH_FAIL.code
				|| notifyVO.getStatus() == RiskConst.Task.DATA_FAIL.code) {
			if (notifyVO.getSources().equals(VariableConst.VAR_SOURCE_MOXIE_TAOBAO)
					|| notifyVO.getSources().equals(VariableConst.VAR_SOURCE_MOXIE_TAOBAO_REPORT)) {
				// 淘宝失败
				updateTaobaoStatus(userModel.getId(), 0);
			} else if (notifyVO.getSources().equals(VariableConst.VAR_SOURCE_MOXIE_CARRIER)
					|| notifyVO.getSources().equals(VariableConst.VAR_SOURCE_MOXIE_CARRIER_REPORT)) {
				//  运营商认证失败
				updateMobileStatus(userModel.getId(), 0);
			}
		}

		if (notifyVO.getStatus() == RiskConst.Task.DATA_SUCCESS.code) {
			AuthenticationModel model = authenticationDao.findUserAuthenticationByUserId(userModel.getId());
			if (model == null) {
				logger.error(">>>>用户认证表不存在 ,userId={}", userModel.getId());
				return;
			}

			if (notifyVO.getSources().equals(VariableConst.VAR_SOURCE_MOXIE_TAOBAO_REPORT)) {

				updateTaobaoStatus(userModel.getId(), 1);

				if (model.getBankTime() == null) {  // 按照目前逻辑+需求来看,首次绑定淘宝的时候,银行卡绑定时间肯定为空
					// 淘宝 认证成功
					UserStatisticDTO dto = new UserStatisticDTO();
					dto.setRegisterDate(Date8Util.date2LocalDate(userModel.getCreateTime()));
					dto.setAgencyId(userModel.getAgencyId());
                    dto.setChannelCode(userModel.getChannelCode());
                    dto.setActionDate(LocalDate.now());
                    dto.setVestId(userModel.getVestId());
					dto.setIsAli(CommonConst.YES);
					jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
					logger.info(">>>>发送统计,用户注册+行为(淘宝认证)jms ,data={}", dto);
				}else {
                    // 淘宝 认证成功
                    UserStatisticDTO dto = new UserStatisticDTO();
                    dto.setRegisterDate(Date8Util.date2LocalDate(userModel.getCreateTime()));
                    dto.setAgencyId(userModel.getAgencyId());
                    dto.setChannelCode(userModel.getChannelCode());
                    dto.setActionDate(LocalDate.now());
                    dto.setVestId(userModel.getVestId());
                    dto.setIsAli(CommonConst.YES);
                    dto.setIsActionLogin(CommonConst.YES);
                    jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
                    logger.info(">>>>发送统计,用户行为(淘宝认证)jms ,data={}", dto);
                }

			} else if (notifyVO.getSources().equals(VariableConst.VAR_SOURCE_MOXIE_CARRIER_REPORT)) {
				//  运营商认证成功
				updateMobileStatus(userModel.getId(), 1);

				if (model.getBankTime() == null) {
					UserStatisticDTO dto = new UserStatisticDTO();
					dto.setRegisterDate(Date8Util.date2LocalDate(userModel.getCreateTime()));
					dto.setAgencyId(userModel.getAgencyId());
                    dto.setChannelCode(userModel.getChannelCode());
                    dto.setActionDate(LocalDate.now());
					dto.setIsPhone(CommonConst.YES);
                    dto.setVestId(userModel.getVestId());
					jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
					logger.info(">>>>发送统计,用户注册+行为(运营商认证)jms ,data={}", dto);
				}else {
                    UserStatisticDTO dto = new UserStatisticDTO();
                    dto.setRegisterDate(Date8Util.date2LocalDate(userModel.getCreateTime()));
                    dto.setAgencyId(userModel.getAgencyId());
                    dto.setChannelCode(userModel.getChannelCode());
                    dto.setActionDate(LocalDate.now());
                    dto.setIsPhone(CommonConst.YES);
                    dto.setIsActionLogin(CommonConst.YES);
                    dto.setVestId(userModel.getVestId());
                    jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
                    logger.info(">>>>发送统计,用户行为(运营商认证)jms ,data={}", dto);
                }
			}
		}
	}


	private void updateTaobaoStatus(Integer userId, Integer status) {
		AuthenticationModel authenticationModel = authenticationDao.findUserAuthenticationByUserId(userId);
		if (authenticationModel.getAlipayStatus() == 1){
			throw new BusinessException(UserRespCode.NOT_REPEAT_AUTH);
		}
		AuthenticationModel update = new AuthenticationModel();

		update.setUserId(userId);
		update.setAlipayStatus(status);
		if (status == 1) {
			update.setMoney(springProperties.getInteger("user.auth.alipay", 80));
			if (authenticationModel.getBankTime() != null && authenticationModel.getRemainDays() != null) {//肯定是认证过
				update.setStatus(1);
				update.setRemainDays(Date8Util.LocalDate2Date(LocalDate.now().plusDays(springProperties.getInteger("user.auth.timeout",30))));
			}
		}

		authenticationDao.updateAuthStatusAndAtomicMoneyById(update);
	}

	private void updateMobileStatus(Integer userId, Integer status) {
		AuthenticationModel authenticationModel = authenticationDao.findUserAuthenticationByUserId(userId);
		if (authenticationModel.getPhoneStatus() == 1){
			throw new BusinessException(UserRespCode.NOT_REPEAT_AUTH);
		}
		AuthenticationModel update = new AuthenticationModel();

		update.setUserId(userId);
		update.setPhoneStatus(status);
		if (status == 1) {
			update.setMoney(springProperties.getInteger("user.auth.phone", 100));
		}

		authenticationDao.updateAuthStatusAndAtomicMoneyById(update);

	}

}
