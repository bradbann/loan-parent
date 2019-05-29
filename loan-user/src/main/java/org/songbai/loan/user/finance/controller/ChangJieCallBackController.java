package org.songbai.loan.user.finance.controller;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.loan.common.finance.ChangJieUtil;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.finance.dao.FinanceIODao;
import org.songbai.loan.user.finance.service.BasicOrderService;
import org.songbai.loan.user.finance.service.PayNotifyService;
import org.songbai.loan.user.user.dao.OrderDao;
import org.songbai.loan.user.user.dao.UserBankCardDao;
import org.songbai.loan.user.user.helper.ChangJieHelper;
import org.songbai.loan.user.user.service.UserBankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: wjl
 * @date: 2018/11/22 16:01
 * Description: 畅捷支付回调
 */
@RestController
@RequestMapping("/repayment")
@LimitLess
public class ChangJieCallBackController {
	private static final Logger log = LoggerFactory.getLogger(ChangJieCallBackController.class);

	@Autowired
	private FinanceIODao ioDao;
	@Autowired
	private BasicOrderService basicOrderService;
	@Autowired
	private ChangJieHelper changJieHelper;
	@Autowired
	private ComUserService comUserService;
	@Autowired
	private DistributeLockFactory lockFactory;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private UserBankCardService userBankCardService;
	@Autowired
	private UserBankCardDao bankCardDao;

	@Autowired
	private PayNotifyService payNotifyService;

	public String CHARSET = "UTF-8";

	@RequestMapping("/changJieBindCardNotify")
	public void changJieBindCardNotify(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			Map<String, String> map = bindCardNotifyParam(request, response);
			log.info("畅捷绑卡回调待签名的参数为：{}", JSON.toJSONString(map));
			UserModel userModel = validatePreDealBind(request, writer, map);
			if (userModel == null) return;
			Integer userId = userModel.getId();
			if (map.get("auth_status").equals("F")) {
				log.info("用户：{}畅捷绑卡鉴权失败", userId);
				writer.write("success");
			} else {
				log.info("用户：{}畅捷绑卡鉴权成功", userId);
				List<UserBankCardModel> list = bankCardDao.selectUserBankListByUserIdStatus(userId, null);
				UserBankCardModel bankCardModel = list.get(0);
				userBankCardService.dealBindSuccess(userModel, bankCardModel);
				writer.write("success");
			}
		} catch (IOException e) {
			writer.write("failed");
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	private UserModel validatePreDealBind(HttpServletRequest request, PrintWriter writer, Map<String, String> map) {
		UserModel userModel = comUserService.selectUserModelByThridId(map.get("mer_user_id"));
		if (userModel == null) {
			log.info("畅捷绑卡回调的第三方userId：{}找不到", map.get("mer_user_id"));
			writer.write("success");
			return null;
		}
		PlatformConfig config = changJieHelper.getAlreadyReqConfig(userModel.getAgencyId());
		if (config == null) {
			writer.write("failed");
			return null;
		}
		String resultSign = request.getParameter("sign");
		if (!ChangJieUtil.verifySign(map, resultSign, config.getPublicKey(), CHARSET)) {
			log.error("畅捷绑卡异步回调验签失败，参数为：{},签名为：{}，公钥为：{}", JSON.toJSONString(map), resultSign, config.getPublicKey());
			writer.write("failed");
		}
		return userModel;
	}

	@RequestMapping("/changJiePayNotify")
	public void changJiePayNotify(HttpServletRequest request, HttpServletResponse response) {
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			Map<String, String> map = payNotifyParam(request, response);
			if(map.isEmpty()){
				writer.write("fail:param is empty");
				return;
			}
			String resultSign = request.getParameter("sign");
			log.info("畅捷支付异步回调返回结果:{}", JSON.toJSONString(map));
			String requestId = map.get("outer_trade_no");
			if (StringUtils.isBlank(requestId)) {
				writer.write("success");
				return;
			}
			//这里需要先查询
			FinanceIOModel financeIOModel = validatePreDealOrder(writer, map, resultSign, requestId);
			if (financeIOModel == null) return;
			DistributeLock lock = null;
			try {
				lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + financeIOModel.getOrderId());
				lock.lock();
				FinanceIOModel ioModel = ioDao.getModelByUserIdOrderIdRequestId(null, null, requestId);
				ioModel.setThirdOrderId(map.get("inner_trade_no"));
				if (checkOrder(writer, map, ioModel)) return;

				String notifyType = map.get("notify_type");

				if(notifyType.equalsIgnoreCase("trade_status_sync")){
					if (map.get("trade_status").equalsIgnoreCase("TRADE_SUCCESS")
							|| map.get("trade_status").equalsIgnoreCase("TRADE_FINISHED")) {
						payNotifyService.paySuccess(ioModel);
					} else {
						payNotifyService.payFail(ioModel,"畅捷支付交易失败");
					}
					writer.write("success");
				}else if(notifyType.equalsIgnoreCase("pay_status_sync")){

					if(map.get("status").equalsIgnoreCase("PAY_FAIL")
						|| map.get("status").equalsIgnoreCase("TRADE_CLOSED")){
						payNotifyService.payFail(ioModel,map.get("pay_msg"));
						writer.write("success");
					}else{
						writer.write("fail:status have "+map.get("status"));
					}

				}else{
					writer.write("fail:notify_type error");
				}

			} finally {
				if (lock != null) {
					lock.unlock();
				}
			}
		} catch (IOException e) {
			writer.write("failed");
		} finally {
			if (writer != null) {
				writer.flush();
				writer.close();
			}
		}
	}

	private FinanceIOModel validatePreDealOrder(PrintWriter writer, Map<String, String> map, String resultSign, String requestId) {
		FinanceIOModel financeIOModel = ioDao.getModelByUserIdOrderIdRequestId(null, null, requestId);
		if (financeIOModel == null) {
			log.info("畅捷支付回调的订单号：{}找不到,不予处理！", requestId);
			writer.write("failed:not found");
			return null;
		}

		if (financeIOModel.getStatus() == FinanceConstant.IoStatus.SUCCESS.key
			|| financeIOModel.getStatus() == FinanceConstant.IoStatus.FAILED.key ) {
			log.info("畅捷支付回调订单{} 已经处理完成，不予处理,订单详细信息：{}", financeIOModel.getOrderId(), JSON.toJSONString(financeIOModel));
			writer.write("success");
			return null;
		}

		PlatformConfig config = changJieHelper.getAlreadyReqConfig(financeIOModel.getAgencyId());
		if (config == null) {
			writer.write("failed:not found config");
			return null;
		}
		if (!ChangJieUtil.verifySign(map, resultSign, config.getPublicKey(), CHARSET)) {
			log.error("畅捷支付异步回调验签失败，参数为：{},签名为：{}，公钥为：{}", JSON.toJSONString(map), resultSign, config.getPublicKey());
			writer.write("failed:sign error");
			return null;
		}
		return financeIOModel;
	}

	private boolean checkOrder(PrintWriter writer, Map<String, String> map, FinanceIOModel ioModel) {
		if (ioModel.getStatus() == FinanceConstant.IoStatus.SUCCESS.key
				|| ioModel.getStatus() == FinanceConstant.IoStatus.FAILED.key ) {
			log.info("畅捷支付回调订单{} 已经处理完成，不予处理,订单详细信息：{}", ioModel.getOrderId(), JSON.toJSONString(ioModel));
			writer.write("success");
			return true;
		}
		OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(ioModel.getOrderId(), ioModel.getUserId());
		List<Integer> list = Arrays.asList(OrderConstant.Status.PROCESSING.key, OrderConstant.Status.WAIT.key, OrderConstant.Status.OVERDUE.key, OrderConstant.Status.FAIL.key);
		if (!(orderModel.getStage() == OrderConstant.Stage.REPAYMENT.key && list.contains(orderModel.getStatus()))) {
			log.info("畅捷支付回调订单{}不是还款处理范围，该订单当前阶段为：{}，状态为：{},订单详细信息：{}", orderModel.getOrderNumber(), orderModel.getStage(), orderModel.getStatus(), JSON.toJSONString(orderModel));
			writer.write("success");
			return true;
		}
		if (Double.compare(ioModel.getMoney(), Double.valueOf(map.get("trade_amount"))) != 0.0) {
			log.info("畅捷支付回调金额{}与订单{}金额{}不符，不予处理！", map.get("trade_amount"), ioModel.getOrderId(), ioModel.getMoney());
			writer.write("success");
			return true;
		}
		return false;
	}

	private Map<String, String> bindCardNotifyParam(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		Map<String, String> map = getCommonMap(request);
		String selfOrderId = request.getParameter("source_voucher_no");
		map.put("source_voucher_no", selfOrderId);
		map.put("auth_status", request.getParameter("auth_status"));
		map.put("bank_code", request.getParameter("bank_code"));
		map.put("bank_name", request.getParameter("bank_name"));
		map.put("mer_user_id", request.getParameter("mer_user_id"));
		map.put("card_begin", request.getParameter("card_begin"));
		map.put("card_end", request.getParameter("card_end"));
		String ext = request.getParameter("ext");
		if (StringUtils.isNotBlank(ext)) {
			map.put("ext", ext);
		}
		return map;
	}

	private Map<String, String> payNotifyParam(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("UTF-8");
		Map<String, String> map = getCommonMap(request);
		map.put("outer_trade_no", request.getParameter("outer_trade_no"));
		map.put("inner_trade_no", request.getParameter("inner_trade_no"));
		map.put("trade_status", request.getParameter("trade_status"));
		map.put("trade_amount", request.getParameter("trade_amount"));
		map.put("gmt_create", request.getParameter("gmt_create"));
		String gmt_payment = request.getParameter("gmt_payment");
		if (StringUtils.isNotBlank(gmt_payment)) {
			map.put("gmt_payment", gmt_payment);
		}
		String gmt_close = request.getParameter("gmt_close");
		if (StringUtils.isNotBlank(gmt_close)) {
			map.put("gmt_close", gmt_close);
		}
		String extension = request.getParameter("extension");
		if (StringUtils.isNotBlank(extension)) {
			map.put("extension", extension);
		}
		return map;
	}

	private Map<String, String> getCommonMap(HttpServletRequest request) {
		Map<String, String> map = new HashMap<>();
		map.put("notify_id", request.getParameter("notify_id"));
		map.put("notify_type", request.getParameter("notify_type"));
		map.put("notify_time", request.getParameter("notify_time"));
		map.put("_input_charset", request.getParameter("_input_charset"));
		map.put("version", request.getParameter("version"));
		return map;
	}
}
