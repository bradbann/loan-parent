package org.songbai.loan.user.finance.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.utils.math.Arith;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.user.finance.dao.FinanceIODao;
import org.songbai.loan.user.finance.model.vo.PayBankCardVO;
import org.songbai.loan.user.finance.model.vo.PayOrderVO;
import org.songbai.loan.user.finance.model.vo.PayResultVO;
import org.songbai.loan.user.finance.service.BasicOrderService;
import org.songbai.loan.user.finance.service.RepaymentService;
import org.songbai.loan.user.user.dao.OrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author: wjl
 * @date: 2018/12/19 14:40
 * Description: 测试通道支付
 */
@Service("testPayService")
public class TestPayServiceImpl implements RepaymentService {
	private static final Logger log = LoggerFactory.getLogger(TestPayServiceImpl.class);

	@Autowired
	private RedisTemplate<String, Object> redis;
	@Autowired
	private DistributeLockFactory lockFactory;
	@Autowired
	private BasicOrderService basicOrderService;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private FinanceIODao ioDao;

	@Override
	public void pay(OrderModel orderModel, UserModel userModel, UserInfoModel userInfoModel, UserBankCardModel userBankCardModel) {
		Integer userId = userModel.getId();
		Integer agencyId = userModel.getAgencyId();
		String oldOrderId = (String) redis.opsForHash().get(UserRedisKey.USER_REPAYMENT, userId);
		if (StringUtils.isNotBlank(oldOrderId)) {
			payCodeResend(agencyId, oldOrderId);
			return;
		}
		String bankCardNum = userBankCardModel.getBankCardNum();
		String orderNum = orderModel.getOrderNumber();
		String requestId = OrderIdUtil.getRepaymentId();
		DistributeLock lock = null;
		try {
			lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderNum);
			lock.lock();
			basicOrderService.initOrder(userModel, orderModel, requestId, bankCardNum, FinanceConstant.PayPlatform.TEST.code,OrderConstant.RepayType.BANKCARD.key);
			log.info("----用户：{}测试还款请求业务受理成功,订单号为:{}----", userId, orderNum);
			redis.opsForHash().put(UserRedisKey.USER_REPAYMENT, userId, requestId);
			throw new BusinessException(UserRespCode.TEST_ACCOUNT);
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}

	@Override
	public void payConfirm(String code, Integer userId) {
		String requestId = (String) redis.opsForHash().get(UserRedisKey.USER_REPAYMENT, userId);
		if (StringUtils.isBlank(requestId)) {
			throw new BusinessException(UserRespCode.REGET_MSG);
		}
		FinanceIOModel ioModel = getAndCheckIoModel(requestId);
		OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), ioModel.getUserId());

        Double realPayMoney = Arith.subtract(2, orderModel.getPayment(), orderModel.getAlreadyMoney());
		if (Double.compare(ioModel.getMoney(), realPayMoney) != 0.0) {//说明订单金额不一样
			log.info("----用户：{}的测试还款订单：{}金额ioModel:{}与orderModel:{}不一致导致订单失效，请查明原因", userId, orderModel.getOrderNumber(), ioModel.getMoney(), realPayMoney);
			basicOrderService.dealOrderFailed(ioModel, "订单金额ioModel:" + ioModel.getMoney() + "与orderModel:" + realPayMoney + "不一致导致订单失效，请查明原因", false);
			throw new BusinessException(UserRespCode.ORDER_HAS_FAILED);
		}
		log.info("----用户:{}的测试订单:{}确认业务受理成功，更改为还款中状态----", ioModel.getUserId(), orderModel.getOrderNumber());
		basicOrderService.dealOrderSuccess(ioModel);
		try {
			log.info("----订单号【{}】测试还款休息1s----", orderModel.getOrderNumber());
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("----订单号【{}】测试还款交易成功----", orderModel.getOrderNumber());
		ioModel.setThirdOrderId("test"+System.currentTimeMillis());
		basicOrderService.repaymentSuccess(ioModel);
	}

	@Override
	public void payCodeResend(Integer agencyId, String oldOrderId) {
		throw new BusinessException(UserRespCode.TEST_ACCOUNT);
	}

	@Override
	public PayResultVO deductPay(PayOrderVO orderVO, PayBankCardVO bankCardVO) {
		throw new BusinessException("测试通道不支持");
	}

	@Override
	public String getCode() {
		return FinanceConstant.PayPlatform.TEST.code;
	}

	private FinanceIOModel getAndCheckIoModel(String requestId) {
		FinanceIOModel ioModel = ioDao.getModelByUserIdOrderIdRequestId(null, null, requestId);
		if (ioModel == null) {
			throw new BusinessException(UserRespCode.ORDER_NOT_EXIST);
		}
		return ioModel;
	}
}
