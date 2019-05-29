package org.songbai.loan.user.finance.service.impl;

import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.loan.OrderOptModel;
import org.songbai.loan.user.finance.dao.FinanceIODao;
import org.songbai.loan.user.finance.service.FinanceIOService;
import org.songbai.loan.user.user.dao.OrderDao;
import org.songbai.loan.user.user.dao.OrderOptDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * io表的dao
 *
 * @author wjl
 * @date 2018年11月13日 20:47:26
 * @description
 */
@Service
public class FinanceIOServiceImpl implements FinanceIOService {

	@Autowired
	private FinanceIODao ioDao;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private OrderOptDao optDao;

	@Transactional
	@Override
	public void deleteIoModelOptModel(Integer ioId,Integer optId) {
		ioDao.deleteById(ioId);
		optDao.deleteById(optId);
	}

	@Override
	public FinanceIOModel getLastIoModelByOrderIdUserId(String orderId, Integer userId) {
		return ioDao.getLastModelByUserIdOrderId(orderId,userId);
	}

	@Override
	public OrderOptModel getLastOptModelByOrderIdUserId(String orderId, Integer userId) {
		return optDao.getLastOptModelByOrderIdUserId(orderId,userId);
	}

	@Override
	public OrderModel validateOrder(String orderNum, Integer userId) {
		OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(orderNum, userId);
		if (orderModel == null) {
			throw new BusinessException(UserRespCode.ORDER_NOT_EXIST);
		}
		if (orderModel.getStage() != OrderConstant.Stage.REPAYMENT.key) {
			throw new BusinessException(UserRespCode.ORDER_NOT_REPAY_TIME);
		}
		if (orderModel.getStatus() == OrderConstant.Status.SUCCESS.key
				|| orderModel.getStatus() == OrderConstant.Status.OVERDUE_LOAN.key
				|| orderModel.getStatus() == OrderConstant.Status.ADVANCE_LOAN.key
				|| orderModel.getStatus() == OrderConstant.Status.CHASE_LOAN.key) {
			throw new BusinessException(UserRespCode.ORDER_ALREADY_SUCCESS);
		}
		if (orderModel.getStatus() == OrderConstant.Status.PROCESSING.key) {
			throw new BusinessException(UserRespCode.ORDER_REPAY_PROCESSING);
		}
		return orderModel;
	}

	@Override
	public FinanceIOModel getIoModelByOrderIdAndRequestId(String orderNum, String requestId, Integer userId) {
		return ioDao.getIoModelByOrderIdAndRequestId(orderNum,requestId,userId);
	}


}
