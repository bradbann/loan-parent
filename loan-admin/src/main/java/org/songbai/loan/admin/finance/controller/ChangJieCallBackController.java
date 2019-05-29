package org.songbai.loan.admin.finance.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.loan.admin.finance.service.BasicPaymentService;
import org.songbai.loan.admin.finance.service.impl.ChangJiePaymentServiceImpl;
import org.songbai.loan.admin.order.dao.FinanceIODao;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.common.finance.ChangJieUtil;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.model.loan.OrderModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wjl
 * @date: 2018/11/22 17:43
 * Description: 畅捷支付放款回调
 */
@RestController
@RequestMapping("/finance")
@LimitLess
public class ChangJieCallBackController {

	private static final Logger log = LoggerFactory.getLogger(ChangJieCallBackController.class);

	@Autowired
	private BasicPaymentService basicPaymentService;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private FinanceIODao ioDao;
	@Autowired
	private ChangJiePaymentServiceImpl changJiePaymentService;
	@Autowired
	private DistributeLockFactory lockFactory;

	@RequestMapping("/changJieNotify")
	public void changJieNotify(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			Map<String, String> map = payNotifyParam(request, response);
			log.info("【畅捷支付打款】回调返回的参数为：【{}】", JSON.toJSONString(map));
			String resultSign = request.getParameter("sign");
			String status = map.get("withdrawal_status");
			String requestId = map.get("outer_trade_no");
			if (StringUtils.isBlank(requestId)) {
				writer.write("success");
				return;
			}
			FinanceIOModel ioModel = new FinanceIOModel();
			ioModel.setRequestId(requestId);
			ioModel = ioDao.selectOne(ioModel);
			OrderModel orderModel = getOrderModelAndCheckIoModel(writer, map, resultSign, requestId, ioModel);
			if (orderModel == null) return;
			DistributeLock lock = null;
			try {
				lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderModel.getOrderNumber());
				lock.lock();
				ioModel.setThirdOrderId(map.get("inner_trade_no"));
				if (status.equals("WITHDRAWAL_SUCCESS")) {
					basicPaymentService.paymentSuccess(orderModel, ioModel, FinanceConstant.PayPlatform.CHANGJIE.name);
					log.info("【畅捷支付打款】回调的订单号：【{}】放款成功", ioModel.getOrderId());
					writer.write("success");
					return;
				}
				if (status.equals("WITHDRAWAL_FAIL") || status.equals("RETURN_TICKET")) {
					log.info("【畅捷支付打款】回调的订单号：【{}】放款失败", ioModel.getOrderId());
					basicPaymentService.paymentFailed(orderModel, ioModel, "银行出款失败");
					writer.write("success");
				}
			} finally {
				if (lock != null) {
					lock.unlock();
				}
			}
		} catch (IOException e) {
			log.info("【畅捷支付打款】回调失败");
			writer.write("failed");
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	private OrderModel getOrderModelAndCheckIoModel(PrintWriter writer, Map<String, String> map, String resultSign, String requestId, FinanceIOModel ioModel) {
		if (ioModel == null) {
			log.info("畅捷支付回调的请求第三方订单号：【{}】找不到,不予处理！", requestId);
			writer.write("failed");
			return null;
		}
		PlatformConfig config = getConfig(ioModel.getAgencyId());
		if (config == null) {
			writer.write("failed");
			return null;
		}
		if (!ChangJieUtil.verifySign(map, resultSign, config.getPublicKey(), "UTF-8")) {
			log.info("【畅捷支付打款】回调验签失败");
			writer.write("failed");
			return null;
		}
		if (ioModel.getStatus() == CommonConst.YES) {
			log.info("【畅捷支付打款】回调的订单号：【{}】已经成功，不予处理", ioModel.getOrderId());
			writer.write("success");
			return null;
		}
		//查询order表
		OrderModel orderModel = orderDao.selectInfoByOrderNumb(ioModel.getOrderId());
		if (orderModel.getStage() == OrderConstant.Stage.LOAN.key && orderModel.getStatus() == OrderConstant.Status.SUCCESS.key) {
			log.info("【畅捷支付打款】回调的订单号：【{}】已经成功，不予处理", ioModel.getOrderId());
			writer.write("success");
			return null;
		}

		if (Double.compare(ioModel.getMoney(), Double.valueOf(map.get("withdrawal_amount"))) != 0.0) {
			log.info("【畅捷支付打款】回调金额【{}】与订单号【{}】金额【{}】不符，不予处理！", map.get("withdrawal_amount"), ioModel.getOrderId(), ioModel.getMoney());
			writer.write("failed");
			return null;
		}
		return orderModel;
	}

	private Map<String, String> payNotifyParam(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		Map<String, String> map = new HashMap<>();
		map.put("notify_id", request.getParameter("notify_id"));
		map.put("notify_type", request.getParameter("notify_type"));
		map.put("notify_time", request.getParameter("notify_time"));
		map.put("_input_charset", request.getParameter("_input_charset"));
		map.put("version", request.getParameter("version"));
		map.put("outer_trade_no", request.getParameter("outer_trade_no"));
		map.put("inner_trade_no", request.getParameter("inner_trade_no"));
		map.put("withdrawal_amount", request.getParameter("withdrawal_amount"));
		map.put("withdrawal_status", request.getParameter("withdrawal_status"));
		map.put("uid", request.getParameter("uid"));
		map.put("return_code", request.getParameter("return_code"));
		map.put("fail_reason", request.getParameter("fail_reason"));
		map.put("gmt_withdrawal", request.getParameter("gmt_withdrawal"));
		return map;
	}


	private PlatformConfig getConfig(Integer agencyId) {
		PlatformConfig config;
		try {
			config = changJiePaymentService.getConfig(agencyId);
		} catch (Exception e) {
			return null;
		}
		return config;
	}
}
