package org.songbai.loan.admin.finance.controller;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.loan.admin.finance.service.BasicPaymentService;
import org.songbai.loan.admin.order.dao.FinanceIODao;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.common.finance.YiBaoUtil;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author wjl
 * @date 2018年11月15日 17:15:39
 * @description 易宝支付放款回调
 */
@RestController
@RequestMapping("/finance")
@LimitLess
public class YiBaoCallBackController {

	private static final Logger log = LoggerFactory.getLogger(YiBaoCallBackController.class);

	@Autowired
	private BasicPaymentService basicPaymentService;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private FinanceIODao ioDao;
	@Autowired
	private DistributeLockFactory lockFactory;
	@Autowired
	private ComFinanceService comFinanceService;

	@RequestMapping("/yiBaoNotify/{agencyIdMD5}")
	public void yiBaoNotify(@PathVariable("agencyIdMD5") String agencyIdMD5, HttpServletRequest request, HttpServletResponse response) throws IOException {
		PrintWriter writer = response.getWriter();
		try {
			String resultResponse = request.getParameter("response");
			if (StringUtils.isBlank(resultResponse)) {
				log.info("【易宝打款】回调结果为NULL");
				writer.write("FAILED");
				return;
			}

			Integer agencyId = comFinanceService.getAgencyIdByMD5(agencyIdMD5);
			String appKey = comFinanceService.getYiBaoSellIdByAgencyId(agencyId);
			Map<String, String> result = YiBaoUtil.Decrypt(resultResponse, appKey);
			if (MapUtils.isEmpty(result)) {
				log.error("代理：{}配置的私钥和公钥有问题，不能解密回调：{}", agencyId, resultResponse);
				writer.write("FAILED");
				return;
			}

			log.info("【易宝打款】回调结果为：{}", result);
			String requestId = result.get("batchNo");
			if (StringUtils.isBlank(requestId)) {
				writer.write("SUCCESS");
				return;
			}
			String thirdOrderId = result.get("orderId");
			String transferStatus = result.get("transferStatusCode");
			String bankStatus = result.get("bankTrxStatusCode");
			FinanceIOModel ioModel = new FinanceIOModel();
			ioModel.setRequestId(requestId);
			ioModel = ioDao.selectOne(ioModel);
			OrderModel orderModel = getOrderModelAndCheckIoModel(writer, requestId, ioModel);
			if (orderModel == null) return;
			DistributeLock lock = null;
			try {
				lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderModel.getOrderNumber());
				lock.lock();
				ioModel.setThirdOrderId(thirdOrderId);
				if (transferStatus.equals("0025")) {
					writer.write("FAILED");
					return;
				}
				if (transferStatus.equals("0026")) {
					switch (bankStatus) {
						case "S": //钱到账了
							basicPaymentService.paymentSuccess(orderModel, ioModel, FinanceConstant.PayPlatform.YIBAO.name);
							log.info("【易宝支付打款】回调的订单号：【{}】放款成功", ioModel.getOrderId());
							writer.write("SUCCESS");
							return;
						case "I":
						case "W": //处理中 钱没到账
							log.info("【易宝支付打款】回调的订单号：【{}】处理中，不予处理", ioModel.getOrderId());
							writer.write("FAILED");
							return;
						default: //失败了
							basicPaymentService.paymentFailed(orderModel, ioModel, "银行出款失败");
							log.info("【易宝支付打款】回调的订单号：【{}】放款失败", ioModel.getOrderId());
							writer.write("SUCCESS");
					}
				} else {//失败了
					basicPaymentService.paymentFailed(orderModel, ioModel, "银行出款失败");
					log.info("【易宝支付打款】回调的订单号：【{}】放款失败", ioModel.getOrderId());
					writer.write("SUCCESS");
				}
			} finally {
				if (lock != null) {
					lock.unlock();
				}
			}
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	private OrderModel getOrderModelAndCheckIoModel(PrintWriter writer, String requestId, FinanceIOModel ioModel) {
		if (ioModel == null) {
			log.info("易宝支付回调的请求第三方订单号：【{}】找不到,不予处理！", requestId);
			writer.write("failed");
			return null;
		}
		if (ioModel.getStatus() == CommonConst.YES) {
			log.info("【易宝支付打款】回调的订单号：【{}】已经成功，不予处理", ioModel.getOrderId());
			writer.write("success");
			return null;
		}
		//查询order表
		OrderModel orderModel = orderDao.selectInfoByOrderNumb(ioModel.getOrderId());
		if (orderModel.getStage() == OrderConstant.Stage.LOAN.key && orderModel.getStatus() == OrderConstant.Status.SUCCESS.key) {
			log.info("【易宝支付打款】回调的订单号：【{}】已经成功，不予处理", ioModel.getOrderId());
			writer.write("success");
			return null;
		}
		return orderModel;
	}
}
