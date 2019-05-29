//package org.songbai.loan.user.user.listener;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.songbai.cloud.basics.exception.BusinessException;
//import org.songbai.loan.common.finance.YiBaoUtil;
//import org.songbai.loan.constant.JmsDest;
//import org.songbai.loan.constant.resp.UserRespCode;
//import org.songbai.loan.constant.user.FinanceConstant;
//import org.songbai.loan.model.user.UserBankCardModel;
//import org.songbai.loan.model.user.UserInfoModel;
//import org.songbai.loan.model.user.UserModel;
//import org.songbai.loan.service.user.service.ComUserService;
//import org.songbai.loan.user.finance.service.RepaymentService;
//import org.songbai.loan.user.finance.service.impl.RepaymentFactory;
//import org.songbai.loan.user.user.dao.OrderDao;
//import org.songbai.loan.user.user.dao.UserBankCardDao;
//import org.songbai.loan.user.user.model.vo.OrderVO;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.stereotype.Component;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * @author: wjl
// * @date: 2019/1/3 16:39
// * Description: 代扣Listener
// */
//@Component
//@Slf4j
//public class AutoRepaymentListener {
//
//	@Autowired
//	private OrderDao orderDao;
//	@Autowired
//	private ComUserService comUserService;
//	@Autowired
//	private UserBankCardDao bankCardDao;
//	@Autowired
//	private RepaymentFactory repaymentFactory;
//
//	@JmsListener(destination = JmsDest.AUTO_REPAYMENT)
//	public void autoRepayment(String msg) {
//		Map<String, String> map = YiBaoUtil.parseResponse(msg);
//		Integer agencyId = Integer.valueOf(map.get("agencyId"));
//		Integer actorId = Integer.valueOf(map.get("actorId"));
//		String ids = map.get("ids");
//		//查询订单 status agencyId  ? 代扣 只有逾期   正常的
//		List<OrderVO> orderModels = orderDao.selectWaitRepaymentOrderToday(ids, agencyId);
//		if (CollectionUtils.isEmpty(orderModels)) {
//			log.info("代扣订单集合为空");
//			return;
//		}
//		for (OrderVO vo : orderModels) {
//			try {
//				UserInfoModel infoModel = comUserService.findUserInfoByUserId(vo.getUserId());
//				UserModel userModel = comUserService.selectUserModelById(vo.getUserId());
//				if (infoModel == null || userModel == null) {
//					throw new BusinessException(UserRespCode.ACCOUNT_NOT_EXISTS);
//				}
//				UserBankCardModel bankCardModel = bankCardDao.getUserBindCard(vo.getUserId(), FinanceConstant.BankCardType.DEFAULT.key, FinanceConstant.BankCardStatus.BIND.key);
//				if (bankCardModel == null) {
//					throw new BusinessException(UserRespCode.PLEASE_AUTH);
//				}
//				RepaymentService bean = repaymentFactory.getBeanByCode(vo.getBindPlatform());
//				bean.autoPay(vo,userModel, infoModel, bankCardModel, actorId);
//			} catch (Exception e) {
//				log.info("订单：【{}】扣款失败，绑定的支付平台为：【{}】", vo.getOrderNumber(), vo.getBindPlatform());
//			}
//		}
//	}
//}
