package org.songbai.loan.admin.schdule.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.loan.admin.finance.service.BasicPaymentService;
import org.songbai.loan.admin.finance.service.impl.ChangJiePaymentServiceImpl;
import org.songbai.loan.admin.order.dao.FinanceIODao;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.common.finance.ChangJieUtil;
import org.songbai.loan.common.finance.PaySignUtil;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.model.loan.OrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wjl
 * @date: 2018/12/2 13:50
 * Description: 打款回调主动查询订单状态
 */
@Component
public class PayAutoQueryListener {
	private static final Logger log = LoggerFactory.getLogger(PayAutoQueryListener.class);

	@Autowired
	private FinanceIODao ioDao;
	@Autowired
	private ChangJiePaymentServiceImpl changJiePaymentService;
	@Autowired
	private DistributeLockFactory lockFactory;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private BasicPaymentService basicPaymentService;

	private String charset = "UTF-8";
	private String signType = "RSA";

//	@JmsListener(destination = JmsDest.FINANCE_PAY_QUERY)
	public void dealOrder() {
		log.info("执行》》》》平台打款回调补偿操作《《《《开始");
		long start = System.currentTimeMillis();

		List<Integer> ioIds = ioDao.selectPayProcessingOrder();
		if (CollectionUtils.isEmpty(ioIds)) {
			log.info("没有查询到待回调的订单");
			log.info("执行》》》》平台打款回调补偿操作《《《《结束，共耗时【{}】s", System.currentTimeMillis() - start);
			return;
		}
		List<FinanceIOModel> ioModels = ioDao.selectBatchIds(ioIds);
		log.info("查询到【{}】条待回调的订单", ioModels.size());
		ioModels.forEach(ioModel -> {
			if (StringUtils.isNotBlank(ioModel.getPayPlatform())) {
				if (ioModel.getPayPlatform().equals(FinanceConstant.PayPlatform.CHANGJIE.code)) {
					changJieQuery(ioModel);
				}
				//TODO 新增支付记得在这里添加
			}
		});

		log.info("执行》》》》平台打款回调补偿操作《《《《结束，共耗时【{}】s", System.currentTimeMillis() - start);
	}

	private void changJieQuery(FinanceIOModel ioModel) {
		log.info("请求畅捷查询打款订单【{}】", ioModel.getOrderId());
		//查询结果
		PlatformConfig config = changJiePaymentService.getConfig(ioModel.getAgencyId());
		Map<String, String> map = new HashMap<>();
		map = changJiePaymentService.setCommonMap(map, config.getSellId());
		map.put("TransCode", "C00000");
		map.put("OriOutTradeNo", ioModel.getRequestId());
		map.put("OutTradeNo", OrderIdUtil.getRequestId());
		String str = PaySignUtil.getFinalMap(map);
		String sign = PaySignUtil.generateSign(str, config.getPrivateKey(), charset);
		map.put("Sign", sign);
		map.put("SignType", signType);
		String result = "";
		try {
			result = ChangJieUtil.buildRequest(map, signType, config.getPrivateKey(), charset, config.getUrl());
		} catch (Exception e) {
			log.info("请求畅捷查询打款订单【{}】网络异常", ioModel.getOrderId());
			throw new BusinessException(UserRespCode.INTERNET_ERROR);
		}
		log.info("订单号【{}】请求畅捷查询打款返回结果为：【{}】", ioModel.getOrderId(), result);
		map.clear();
		//返回结果
		JSONObject jsonObject = JSON.parseObject(result);
		map = changJiePaymentService.getCommonMap(jsonObject);
		map.put("CorpAcctNo", jsonObject.getString("CorpAcctNo"));
		map.put("CorpAcctName", jsonObject.getString("CorpAcctName"));
		map.put("AcctNo", jsonObject.getString("AcctNo"));
		map.put("AcctName", jsonObject.getString("AcctName"));
		map.put("TransAmt", jsonObject.getString("TransAmt"));
		map.put("OriginalRetCode", jsonObject.getString("OriginalRetCode"));
		map.put("Fee", jsonObject.getString("Fee"));
		map.put("OriginalErrorMessage", jsonObject.getString("OriginalErrorMessage"));
		map.put("AppRetcode", jsonObject.getString("AppRetcode"));
		map.put("AppRetMsg", jsonObject.getString("AppRetMsg"));
		String resultSign = jsonObject.getString("Sign");
		//验签
		if (!ChangJieUtil.verifySign(map, resultSign, config.getPublicKey(), charset)) {
			log.info("请求畅捷查询订单验签失败");
			throw new BusinessException(AdminRespCode.VERIFY_SIGN_ERROR);
		}
		OrderModel orderModel = orderDao.selectInfoByOrderNumb(ioModel.getOrderId());
		if (orderModel == null) {
			return;
		}
		if (!(orderModel.getStage() == OrderConstant.Stage.LOAN.key && orderModel.getStatus() == OrderConstant.Status.PROCESSING.key)) {
			log.info("【畅捷支付】查询订单【{}】不是放款阶段、放款中状态，不予处理，当前状态为：{}", orderModel.getOrderNumber(), JSON.toJSONString(orderModel));
			return;
		}
		DistributeLock lock = null;
		try {
			lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderModel.getOrderNumber());
			lock.lock();
			if (map.get("AcceptStatus").equals("S")) {
				if (map.get("PlatformRetCode").equals("0000") || map.get("PlatformRetCode").equals("2000")) {
					if (map.get("OriginalRetCode").equals("000000")) {
						log.info("查询得知订单：【{}】打款成功", ioModel.getOrderId());
						basicPaymentService.paymentSuccess(orderModel, ioModel, FinanceConstant.PayPlatform.CHANGJIE.name);
					} else {
						log.info("查询得知订单：【{}】打款失败", ioModel.getOrderId());
						basicPaymentService.paymentFailed(orderModel, ioModel, map.get("PlatformErrorMessage"));
					}
				} else {
					log.info("请求畅捷查询订单【{}】受理失败", ioModel.getOrderId());
				}
			} else {
				log.info("请求畅捷查询订单【{}】受理失败", ioModel.getOrderId());
			}
		} finally {
			if (lock != null) {
				lock.unlock();
			}
		}
	}
}
