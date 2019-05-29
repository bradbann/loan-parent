package org.songbai.loan.admin.finance.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.loan.admin.finance.service.BasicPaymentService;
import org.songbai.loan.admin.finance.service.PaymentService;
import org.songbai.loan.admin.order.dao.FinanceIODao;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.admin.user.dao.UserBankCardDao;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: wjl
 * @date: 2018/12/19 15:24
 * Description: 测试通道打款
 */
@Service("testPaymentService")
public class TestPaymentServiceImpl implements PaymentService {
	private static final Logger log = LoggerFactory.getLogger(TestPaymentServiceImpl.class);

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private DistributeLockFactory lockFactory;
	@Autowired
	private BasicPaymentService basicPaymentService;
	@Autowired
	private ComUserService comUserService;
	@Autowired
	private UserBankCardDao userBankCardDao;
	@Autowired
	private FinanceIODao ioDao;

	@Override
	public List<Integer> validate(List<OrderModel> orderModels, Integer agencyId, Integer actorId) {
		List<Integer> list = new ArrayList<>();
		for (OrderModel orderModel : orderModels) {
			String redisKey = UserRedisKey.PAYMENT_TEST + orderModel.getId();
			if (!redisTemplate.opsForValue().setIfAbsent(redisKey, 1)) {
				throw new BusinessException(AdminRespCode.ORDER_HAS_SUBMIT, "订单：" + orderModel.getOrderNumber() + "已被提交，请勿重复提交");
			}
			list.add(orderModel.getId());
		}
		DistributeLock lock = null;
		for (OrderModel dbModel : orderModels) {
			try {
				lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + dbModel.getOrderNumber());
				lock.lock();
				//先修改order表
				OrderModel update = new OrderModel();
				update.setId(dbModel.getId());
				update.setStatus(OrderConstant.Status.PROCESSING.key);
				orderDao.updateById(update);
			} finally {
				if (lock != null) {
					lock.unlock();
				}
			}
		}
		return list;
	}

	@Override
	public void transfer(List<Integer> list, Integer agencyId, Integer actorId) {
		List<OrderModel> orderModels = orderDao.selectBatchIds(list);
		try {
			DistributeLock lock = null;
			for (OrderModel orderModel : orderModels) {
				try {
					String orderNumber = orderModel.getOrderNumber();
					String requestId = OrderIdUtil.getPaymentId();
					lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderNumber);
					lock.lock();
					//查询用户信息
					UserModel userModel = comUserService.selectUserModelById(orderModel.getUserId(), 0);
					// 查询银行卡
					UserBankCardModel bankCardModel = new UserBankCardModel();
					bankCardModel.setUserId(orderModel.getUserId());
					bankCardModel.setStatus(FinanceConstant.BankCardStatus.BIND.key);
					bankCardModel.setType(FinanceConstant.BankCardType.DEFAULT.key);
					bankCardModel = userBankCardDao.selectOne(bankCardModel);
					if (bankCardModel == null) {
						basicPaymentService.dealPaymentFailed(orderModel.getId(), "用户没有默认收款银行卡");
						continue;
					}
					log.info("----订单号【{}】测试打款业务受理成功----", orderNumber);
					basicPaymentService.dealPaymentSuccess(orderModel, actorId, userModel, bankCardModel, requestId, FinanceConstant.PayPlatform.TEST.code);
					try {
						log.info("----订单号【{}】测试打款休息1s----", orderNumber);
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					FinanceIOModel ioModel = new FinanceIOModel();
					ioModel.setRequestId(requestId);
					ioModel = ioDao.selectOne(ioModel);
					ioModel.setThirdOrderId("测试测试测试测试");
					log.info("----订单号【{}】测试打款{}交易成功----", orderNumber, orderModel.getObtain());
					basicPaymentService.paymentSuccess(orderModel, ioModel, "测试测试测试测试");
				} finally {
					if (lock != null) {
						lock.unlock();
					}
				}
			}
		} finally {
			for (OrderModel orderModel : orderModels) {
				redisTemplate.delete(UserRedisKey.PAYMENT_TEST + orderModel.getId());
			}
		}
	}

	/**
	 * 获取支付配置
	 *
	 * @return
	 */
	@Override
	public PlatformConfig getConfig(Integer agencyId) {
		return null;
	}

	@Override
	public String getCode() {
		return FinanceConstant.PayPlatform.TEST.code;
	}
}
