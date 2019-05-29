package org.songbai.loan.admin.finance.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.admin.finance.service.BasicPaymentService;
import org.songbai.loan.admin.order.dao.FinanceIODao;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.admin.order.dao.OrderOptDao;
import org.songbai.loan.admin.order.dao.PaymentFlowDao;
import org.songbai.loan.admin.user.dao.UserBankCardDao;
import org.songbai.loan.admin.user.dao.UserDao;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.sms.PushEnum;
import org.songbai.loan.constant.sms.SmsConst;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.loan.OrderOptModel;
import org.songbai.loan.model.loan.PaymentFlowModel;
import org.songbai.loan.model.sms.PushModel;
import org.songbai.loan.model.statistic.dto.PayStatisticDTO;
import org.songbai.loan.model.statistic.dto.UserStatisticDTO;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.songbai.loan.service.sms.service.ComPushTemplateService;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 后台财务打款通用service
 *
 * @author wjl
 * @date 2018年11月14日 19:10:50
 * @description
 */
@Service
public class BasicPaymentServiceImpl implements BasicPaymentService {
	private static final Logger log = LoggerFactory.getLogger(BasicPaymentServiceImpl.class);
	@Autowired
	private OrderOptDao orderOptDao;
	@Autowired
	private PaymentFlowDao paymentFlowDao;
	@Autowired
	private FinanceIODao ioDao;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private UserDao userDao;
	@Autowired
	private ComPushTemplateService pushTemplateService;
	@Autowired
	private JmsTemplate jmsTemplate;
    @Autowired
    private ComUserService comUserService;
	@Autowired
	private ComFinanceService comFinanceService;

	@Override
	@Transactional
	public void dealPaymentSuccess(OrderModel orderModel, Integer actorId ,UserModel userModel,UserBankCardModel bankCardModel,String requestId, String payPlatform) {
		//打款成功
		//先插入操作记录表
		OrderOptModel optModel = new OrderOptModel();
		optModel.setStage(OrderConstant.Stage.LOAN.key);
		optModel.setStageFlag(OrderConstant.Stage.LOAN.name);
		optModel.setStatus(OrderConstant.Status.PROCESSING.key);
		optModel.setType(2);
		optModel.setOrderNumber(orderModel.getOrderNumber());
		optModel.setAgencyId(orderModel.getAgencyId());
		optModel.setActorId(actorId);
		optModel.setUserId(orderModel.getUserId());
		optModel.setGuest(userModel.getGuest());
		optModel.setOrderTime(orderModel.getCreateTime());
		optModel.setRemark("开始放款");
		orderOptDao.insert(optModel);

		//插入io表
		FinanceIOModel ioModel = new FinanceIOModel();
		ioModel.setUserId(orderModel.getUserId());
		ioModel.setAgencyId(orderModel.getAgencyId());
		ioModel.setThirdUserId(userModel.getThirdId());
		ioModel.setOrderId(orderModel.getOrderNumber());
		ioModel.setRequestId(requestId);
		ioModel.setPayPlatform(payPlatform);
		ioModel.setStatus(FinanceConstant.IoStatus.PROCESSING.key);
		ioModel.setType(FinanceConstant.PayType.PAY.type);
		ioModel.setTypeDetail(FinanceConstant.PayType.PAY.typeDetail);
		ioModel.setBankCardNum(bankCardModel.getBankCardNum());
		ioModel.setMoney(orderModel.getObtain());
		ioModel.setPayType(OrderConstant.RepayType.BANKCARD.key);//2
		ioModel.setOperatorId(actorId);
		ioModel.setOperatorTime(new Date());
		ioDao.insert(ioModel);
	}

	@Override
	public void dealPaymentFailed(Integer orderId, String msg) {
		OrderModel updateOrderModel = new OrderModel();
		updateOrderModel.setId(orderId);
		updateOrderModel.setStatus(OrderConstant.Status.EXCEPTION.key);
		updateOrderModel.setRemark("放款失败：" + msg);
		orderDao.updateById(updateOrderModel);
	}

	@Override
	@Transactional
	public void paymentFailed(OrderModel orderModel, FinanceIOModel ioModel, String msg) {
		//打款失败 更新order表和io表
		OrderModel updateOrderModel = new OrderModel();
		updateOrderModel.setId(orderModel.getId());
		updateOrderModel.setStage(OrderConstant.Stage.LOAN.key);
		updateOrderModel.setStatus(OrderConstant.Status.EXCEPTION.key);
		updateOrderModel.setRemark("放款失败：" + msg);
		orderDao.updateById(updateOrderModel);

		FinanceIOModel updateIoModel = new FinanceIOModel();
		updateIoModel.setId(ioModel.getId());
		updateIoModel.setStatus(FinanceConstant.IoStatus.FAILED.key);
		updateIoModel.setThirdOrderId(ioModel.getThirdOrderId());
		updateIoModel.setRemark("放款失败：" + msg);
		ioDao.updateById(updateIoModel);

		OrderOptModel lastUpdateOpt = orderOptDao.getLastUpdateOpt(orderModel.getOrderNumber(), orderModel.getUserId());
		OrderOptModel updateOrderOptModel = new OrderOptModel();
		updateOrderOptModel.setId(lastUpdateOpt.getId());
		updateOrderOptModel.setStatus(OrderConstant.Status.EXCEPTION.key);
		updateOrderOptModel.setRemark("放款失败：" + msg);
		orderOptDao.updateById(updateOrderOptModel);
	}

	/**
	 * 交易成功处理逻辑
	 */
	@Override
	@Transactional
	public void paymentSuccess(OrderModel orderModel, FinanceIOModel ioModel, String name) {
		//更新order表和io表和插入流水表
		// 更新订单状态为等待还款
		OrderModel updateOrderModel = new OrderModel();
		updateOrderModel.setId(orderModel.getId());
		Date transferTime = new Date();
		updateOrderModel.setRemark(" ");
		updateOrderModel.setStage(OrderConstant.Stage.REPAYMENT.key);
		updateOrderModel.setStatus(OrderConstant.Status.WAIT.key);
		// 计算还款日期
		updateOrderModel.setRepaymentDate(Date8Util.LocalDate2Date(LocalDate.now().plusDays(orderModel.getDays() - 1)));
		updateOrderModel.setTransferTime(transferTime);
		orderDao.updateOrderPayment(updateOrderModel);

		FinanceIOModel updateIoModel = new FinanceIOModel();
		updateIoModel.setId(ioModel.getId());
		updateIoModel.setStatus(FinanceConstant.IoStatus.SUCCESS.key);//1
		updateIoModel.setThirdOrderId(ioModel.getThirdOrderId());
		ioDao.updateById(updateIoModel);

		OrderOptModel lastUpdateOpt = orderOptDao.getLastUpdateOpt(orderModel.getOrderNumber(), orderModel.getUserId());
		OrderOptModel updateOrderOptModel = new OrderOptModel();
		updateOrderOptModel.setId(lastUpdateOpt.getId());
		updateOrderOptModel.setStatus(OrderConstant.Status.SUCCESS.key);
		updateOrderOptModel.setRemark("放款成功");
		orderOptDao.updateById(updateOrderOptModel);

		//查询用户银行卡
		UserBankCardModel userBankCardModel = comFinanceService.getUserDefaultBankCard(ioModel.getUserId());

		//成功再插入打款流水表
		PaymentFlowModel flowModel = new PaymentFlowModel();
		flowModel.setOrderNumber(orderModel.getOrderNumber());
		flowModel.setPaymentNumber(ioModel.getRequestId());
		flowModel.setAgencyId(orderModel.getAgencyId());
		flowModel.setUserId(orderModel.getUserId());
		flowModel.setActorId(ioModel.getOperatorId());
		flowModel.setUsername(userBankCardModel.getName());
		flowModel.setPhone(userBankCardModel.getBankPhone());
		flowModel.setMoney(orderModel.getObtain());
		flowModel.setPayChannel(name);
		flowModel.setLoan(orderModel.getLoan());
		flowModel.setStampTax(orderModel.getStampTax());
		flowModel.setGuest(orderModel.getGuest());
		flowModel.setBankName(userBankCardModel.getBankName());
		flowModel.setBranchBank(userBankCardModel.getBranchBankName());
		flowModel.setBankNumber(userBankCardModel.getBankCardNum());
		flowModel.setOrderTime(orderModel.getCreateTime());
		flowModel.setPaymentTime(transferTime);
		paymentFlowDao.insert(flowModel);
		log.info("订单【{}】打款【{}】给用户【{}】成功，插入打款流水表成功", orderModel.getOrderNumber(), orderModel.getObtain(), orderModel.getUserId());
sendJms(orderModel,userBankCardModel,updateOrderModel,transferTime);

	}

    private void sendJms(OrderModel orderModel, UserBankCardModel bankCardModel, OrderModel updateOrderModel, Date transferTime) {
        //推送放款成功的消息
        UserModel userModel = userDao.selectById(orderModel.getUserId());
        log.info("》》》推送【放款成功】消息给用户【{}】《《《", orderModel.getUserId());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("jine", orderModel.getObtain());
        jsonObject.put("bankName", bankCardModel.getBankName());
        String bankCardNum = bankCardModel.getBankCardNum();
        jsonObject.put("number", bankCardNum.substring(bankCardNum.length() - 4));
        PushModel pushModel = pushTemplateService.generateLoanPushTemplateTitleAndMsg(PushEnum.LOAN.PAY_SUCCESS, jsonObject);
        pushModel.setClassify(PushEnum.Classify.SINGLE.value);
        pushModel.setDataId(orderModel.getOrderNumber());
        pushModel.setUserId(orderModel.getUserId());
        pushModel.setVestId(userModel.getVestId());
        pushModel.setDeviceId(userModel.getGexing());
        jmsTemplate.convertAndSend(JmsDest.LOAN_PUSH_MSG, pushModel);

        Map<String, Object> param = new HashMap<>();
        String name = userModel.getName().substring(0, 1);
        UserInfoModel userInfo = comUserService.findUserInfoByUserId(orderModel.getUserId());
        if (userInfo.getSex() == CommonConst.YES) {//男
            name = name + "先生";
        }else {
            name = name + "女士";
        }
        param.put("name", name);
        param.put("money", FormatUtil.formatDouble2(orderModel.getObtain()));
        param.put("card", jsonObject.get("number"));

        Map<String, Object> map = new HashMap<>();
        map.put("phone", userModel.getPhone());
        map.put("param", param);
        map.put("smsType", SmsConst.Type.PAY_SUCC.value);
        map.put("agencyId", userModel.getAgencyId());
        map.put("channelId", userModel.getChannelId());
        map.put("createTime", System.currentTimeMillis());
        String message = JSON.toJSONString(map);
        log.info("发送短信信息：message={}", message);
        jmsTemplate.convertAndSend(JmsDest.SMS_SENT, message);


        UserStatisticDTO dto = new UserStatisticDTO();
        dto.setRegisterDate(Date8Util.date2LocalDate(userModel.getCreateTime()));
        dto.setAgencyId(userModel.getAgencyId());
        dto.setChannelCode(userModel.getChannelCode());
        dto.setActionDate(LocalDate.now());
        dto.setVestId(userModel.getVestId());
        dto.setIsPay(CommonConst.YES);
        jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
        log.info(">>>>发送统计,用户行为(下款)jms ,data={}", dto);


        PayStatisticDTO payDto = new PayStatisticDTO();
        payDto.setAgencyId(userModel.getAgencyId());
        payDto.setPayDate(Date8Util.date2LocalDate(transferTime));
        payDto.setRepayDate(Date8Util.date2LocalDate(updateOrderModel.getRepaymentDate()));
        payDto.setLoan(orderModel.getLoan());
        payDto.setPay(orderModel.getObtain());
        payDto.setStampTax(orderModel.getStampTax());
        payDto.setVestId(userModel.getVestId());

        if (userModel.getGuest() == OrderConstant.Guest.NEW_GUEST.key) {
            payDto.setIsFirstLoan(CommonConst.YES);
        } else {
            payDto.setIsAgainLoan(CommonConst.YES);
        }
        jmsTemplate.convertAndSend(JmsDest.ORDER_LOAN_PAY, payDto);
        log.info(">>>>发送统计,放款统计(放款)jms ,data={}", payDto);
    }

}
