package org.songbai.loan.user.user.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.utils.http.HttpTools;
import org.songbai.loan.common.util.BankCardUtil;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.statistic.dto.UserStatisticDTO;
import org.songbai.loan.model.user.AuthenticationModel;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.user.dao.AuthenticationDao;
import org.songbai.loan.user.user.dao.OrderDao;
import org.songbai.loan.user.user.dao.UserBankCardDao;
import org.songbai.loan.user.user.model.vo.UserBankCardVo;
import org.songbai.loan.user.user.service.BindService;
import org.songbai.loan.user.user.service.UserBankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;


/**
 * 用户银行卡认证
 *
 * @author wjl
 * @date 2018年10月30日 10:31:01
 * @description
 */
@Service
public class UserBankCardServiceImpl implements UserBankCardService {
	private static final Logger log = LoggerFactory.getLogger(UserBankCardServiceImpl.class);
	@Autowired
	private UserBankCardDao bankCardDao;
	@Autowired
	private ComUserService userService;
	@Autowired
	@Qualifier("changJieBindService")
	private BindService changJieBindService;
	@Autowired
	@Qualifier("yiBaoBindService")
	private BindService yiBaoBindService;
	@Autowired
	@Qualifier("testBindService")
	private BindService testBindService;
	@Autowired
	private OrderDao orderDao;
	@Autowired
	private SpringProperties springProperties;
	@Autowired
	private AuthenticationDao authDao;
	@Autowired
	private JmsTemplate jmsTemplate;
	@Autowired
	private ComUserService comUserService;
	@Autowired
	private ComFinanceService comFinanceService;
	@Autowired
	private ComAgencyService comAgencyService;
	@Override
	public void bind(String phone, UserBankCardModel bankCardModel, Integer userId) {
		UserModel userModel = comUserService.selectUserModelById(userId, 0);
		if (userModel == null) {
			throw new BusinessException(UserRespCode.USER_ACCOUNT_ERROR);
		}
		UserInfoModel infoModel = comUserService.findUserInfoByUserId(userId, 0);
		if (infoModel == null) {
			throw new BusinessException(UserRespCode.DO_NOT_DELETE_TABLE);
		}
		String platformCode = bankCardModel.getBindPlatform();
		if (platformCode.equals(FinanceConstant.PayPlatform.YIBAO.code)) {
			yiBaoBindService.bind(phone, userModel, infoModel, bankCardModel);
		}
		if (platformCode.equals(FinanceConstant.PayPlatform.CHANGJIE.code)) {
			changJieBindService.bind(phone, userModel, infoModel, bankCardModel);
		}
		if (platformCode.equals(FinanceConstant.PayPlatform.TEST.code)) {
			testBindService.bind(phone, userModel, infoModel, bankCardModel);
		}
	}

	@Override
	public void bindConfirm(String code, UserBankCardModel bankCardModel) {
		if (bankCardModel.getBindPlatform().equals(FinanceConstant.PayPlatform.YIBAO.code)) {
			yiBaoBindService.bindConfirm(code, bankCardModel);
		}
		if (bankCardModel.getBindPlatform().equals(FinanceConstant.PayPlatform.CHANGJIE.code)) {
			changJieBindService.bindConfirm(code, bankCardModel);
		}
		if (bankCardModel.getBindPlatform().equals(FinanceConstant.PayPlatform.TEST.code)) {
			testBindService.bindConfirm(code, bankCardModel);
		}
	}

	@Override
	public void unBind(Integer userId, Integer id) {
		UserModel userModel = comUserService.selectUserModelById(userId);
		if (userModel == null) return;
		UserBankCardModel bankCardModel = bankCardDao.selectById(id);
		if (bankCardModel == null) return;
		if (bankCardModel.getStatus() == FinanceConstant.BankCardStatus.INIT.key || bankCardModel.getStatus() == FinanceConstant.BankCardStatus.UNBIND.key)
			return;//如果不是已绑定的就返回
		checkOrder(userId, bankCardModel.getId(), "unBind");
		String platformCode = bankCardModel.getBindPlatform();
		if (StringUtils.isBlank(platformCode)) return ;
		if (platformCode.equals(FinanceConstant.PayPlatform.YIBAO.code)) {
			yiBaoBindService.unBind(bankCardModel, userModel);
		}
		if (platformCode.equals(FinanceConstant.PayPlatform.CHANGJIE.code)) {
			changJieBindService.unBind(bankCardModel, userModel);
		}
		if (platformCode.equals(FinanceConstant.PayPlatform.TEST.code)) {
			testBindService.unBind(bankCardModel, userModel);
		}
	}

	@Override
	public void bindDefault(Integer userId, Integer id) {
		UserModel userModel = comUserService.selectUserModelById(userId);
		if (userModel == null) return;
		UserBankCardModel bankCardModel = bankCardDao.selectById(id);
		if (bankCardModel == null) return;
		//先查默认卡是不是最终状态
		UserBankCardModel defaultCard = bankCardDao.getUserBindCard(userId, FinanceConstant.BankCardType.DEFAULT.key, FinanceConstant.BankCardStatus.BIND.key);
		if (defaultCard == null) {
			UserBankCardModel newUpdate = new UserBankCardModel();
			newUpdate.setId(bankCardModel.getId());
			newUpdate.setType(FinanceConstant.BankCardType.DEFAULT.key);
			bankCardDao.updateById(newUpdate);
			return;
		}
		checkOrder(userId, defaultCard.getId(), "default");
		//先更新老的为非默认
		UserBankCardModel oldUpdate = new UserBankCardModel();
		oldUpdate.setId(defaultCard.getId());
		oldUpdate.setType(FinanceConstant.BankCardType.OTHER.key);
		bankCardDao.updateById(oldUpdate);
		//在更新新的为默认
		UserBankCardModel newUpdate = new UserBankCardModel();
		newUpdate.setId(bankCardModel.getId());
		newUpdate.setType(FinanceConstant.BankCardType.DEFAULT.key);
		bankCardDao.updateById(newUpdate);
	}

	@Override
	public void save(String name, String bankName, String bankCardNum, String bankCode, String bankPhone, Integer userId) {
		UserInfoModel infoModel = userService.findUserInfoByUserId(userId);
		if (infoModel.getDeleted() == CommonConst.YES){
			throw new BusinessException(UserRespCode.ACCOUNT_NOT_EXISTS,"账户异常,请联系客服处理。");
		}
		Integer agencyId = infoModel.getAgencyId();
		if (!name.equals(infoModel.getName())) {
			throw new BusinessException(UserRespCode.NAME_NOT_MATCH);
		}
		List<UserBankCardModel> cardList = bankCardDao.selectUserBankListByUserIdStatus(userId, FinanceConstant.BankCardStatus.BIND.key);
		if (cardList.size() >= springProperties.getInteger("user.bind.limit",5)) {
			throw new BusinessException(UserRespCode.BINDED_ONLY_FIVE,"最多绑定"+cardList.size()+"张卡");
		}
		//根据卡号查询这个卡是否已绑定
		UserBankCardModel checkBankCardModel = bankCardDao.getBankCardByCardNum(bankCardNum, FinanceConstant.BankCardStatus.BIND.key, infoModel.getAgencyId());
		if (checkBankCardModel != null) {
			throw new BusinessException(UserRespCode.THIS_CARD_BINDED);
		}
		String platformCode = comFinanceService.getPayCodeByAgency(agencyId);
		List<UserBankCardModel> list = bankCardDao.selectUserBankListByUserIdStatus(userId, null);
		UserBankCardModel bankCardModel = new UserBankCardModel();
		bankCardModel.setUserId(userId);
		bankCardModel.setAgencyId(agencyId);
		bankCardModel.setName(name);
		bankCardModel.setBankName(bankName);
		bankCardModel.setBankCode(bankCode);
		bankCardModel.setBankCardNum(bankCardNum);
		bankCardModel.setBankPhone(bankPhone);
		bankCardModel.setStatus(FinanceConstant.BankCardStatus.INIT.key);
		bankCardModel.setBindPlatform(platformCode);
		bankCardModel.setIcon(bankCardDao.getIconByBankCode(bankCode));
		if (CollectionUtils.isEmpty(list)) {//第一次绑卡
			bankCardModel.setType(FinanceConstant.BankCardType.DEFAULT.key);
			bankCardDao.insert(bankCardModel);
			return;
		}
		UserBankCardModel latestModel = list.get(0);
		if (latestModel.getStatus() == FinanceConstant.BankCardStatus.INIT.key) {//上次绑卡没成功
			bankCardModel.setRequestId(null);
			bankCardModel.setId(latestModel.getId());
			bankCardDao.updateBankCardModelById(bankCardModel);
		} else {
			//绑过了 新增卡
			bankCardModel.setType(FinanceConstant.BankCardType.OTHER.key);//其他情况统一为非默认
			bankCardDao.insert(bankCardModel);
		}
	}

	@Override
	public String getBankByBankNum(String bankNum) {
		String url = "https://ccdcapi.alipay.com/validateAndCacheCardInfo.json?_input_charset=utf-8&cardNo=" + bankNum + "&cardBinCheck=true";
		String result = HttpTools.doGet(url, null);
		if (StringUtils.isBlank(result)) {
			log.error("调用支付宝识别银行卡接口失败");
			throw new BusinessException(UserRespCode.INTERNET_ERROR);
		}
		if (JSON.parseObject(result).getBoolean("validated")) {
			return JSON.parseObject(result).getString("bank");
		}
		log.error("银行卡号{}：未通过支付宝校验,返回结果为：{}",bankNum,result);
		throw new BusinessException(UserRespCode.BANKCARD_ERROR);
	}

	@Override
	public Map<String, Object> list(Integer userId) {
		List<UserBankCardModel> list = bankCardDao.selectUserBindList(userId, FinanceConstant.Status.ENABLE.key);
		if (CollectionUtils.isEmpty(list)) {
			return new HashMap<>();
		}
		list.forEach(e -> {
			if (StringUtils.isNotBlank(e.getBankCardNum())) {
				e.setBankCardNum(BankCardUtil.hideCardNo(e.getBankCardNum()));
			}
			if (StringUtils.isNotBlank(e.getBankPhone())) {
				e.setBankPhone(e.getBankPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));
			}
		});
		Map<String, Object> map = new HashMap<>();
		if (list.size() >= springProperties.getInteger("user.bind.limit",5)){
			map.put("max",true);
		}else {
			map.put("max",false);
		}
		map.put("list",list);
		return map;
	}

	@Override
	public Map<String, Object> all(Integer userId) {
		UserModel userModel = comUserService.selectUserModelById(userId);
		Integer agencyId = userModel.getAgencyId();
		AgencyModel agencyModel = comAgencyService.findAgencyById(agencyId);
		HashMap<String, Object> map = new HashMap<>();
		if (agencyModel.getH5Status() == FinanceConstant.Status.ENABLE.key) {
			List<UserBankCardVo> list = new ArrayList<>();
			UserBankCardVo bankCardModel = new UserBankCardVo();
			bankCardModel.setUrl(agencyModel.getH5Url());
			list.add(bankCardModel);
			map.put("enable", true);
			map.put("list", list);
		} else {
			List<UserBankCardVo> list = bankCardDao.selectAllList(userId, FinanceConstant.Status.ENABLE.key);
			if (CollectionUtils.isEmpty(list)) {
				return new HashMap<>();
			}
			if (list.size() >= springProperties.getInteger("user.bind.limit",5)){
				map.put("max",true);
			}else {
				map.put("max",false);
			}
			OrderModel orderModel = orderDao.findWaitRepaymentOrder(userId);

			if (agencyModel.getAlipayStatus() == FinanceConstant.Status.ENABLE.key) {
				UserBankCardVo bankCardModel = new UserBankCardVo();
				String url = agencyModel.getAlipayUrl();
				if (StringUtils.isNotBlank(url)) {
					if (!url.contains("?")) {
						url = url + "?payCode=aliPay&userId=" + userId;
					} else {
						url = url + "&payCode=aliPay&userId=" + userId;
					}
					if (orderModel != null) {
						url = url + "&orderNumber=" + orderModel.getOrderNumber();
					}
					bankCardModel.setUrl(url);
				}

				bankCardModel.setTitle("支付宝支付");
				bankCardModel.setIcon("https://lhloanp.oss-cn-shanghai.aliyuncs.com/assert/bank/alipay.png");
				list.add(bankCardModel);
			}
			if (agencyModel.getWepayStatus() == FinanceConstant.Status.ENABLE.key) {
				UserBankCardVo bankCardModel = new UserBankCardVo();
				String url = agencyModel.getWepayUrl();
				if (StringUtils.isNotBlank(url)) {
					if (!url.contains("?")) {
						url = url + "?payCode=wxPay&userId=" + userId;
					} else {
						url = url + "&payCode=wxPay&userId=" + userId;
					}
					if (orderModel != null) {
						url = url + "&orderNumber=" + orderModel.getOrderNumber();
					}
					bankCardModel.setUrl(url);
				}
				bankCardModel.setTitle("微信支付");
				bankCardModel.setIcon("https://lhloanp.oss-cn-shanghai.aliyuncs.com/assert/bank/weixin.png");
				list.add(bankCardModel);
			}
			map.put("enable", false);
			map.put("list", list);
		}
		return map;
	}

	@Override
	@Transactional
	public void dealBindSuccess(UserModel userModel, UserBankCardModel bankCardModel) {
		UserBankCardModel updateBankModel = new UserBankCardModel();
		updateBankModel.setId(bankCardModel.getId());
		updateBankModel.setStatus(FinanceConstant.BankCardStatus.BIND.key);

		// 绑定银行卡后更新auth表中的银行卡认证状态
		AuthenticationModel authModel = authDao.selectById(userModel.getId());
		if (authModel.getStatus() != 1) {//说明第一次绑卡
			AuthenticationModel updateAuthModel = new AuthenticationModel();
			updateAuthModel.setUserId(authModel.getUserId());
			updateAuthModel.setBankStatus(CommonConst.STATUS_VALID);
			updateAuthModel.setStatus(CommonConst.STATUS_VALID);
			updateAuthModel.setRemainDays(Date8Util.LocalDate2Date(LocalDate.now().plusDays(springProperties.getInteger("user.auth.timeout",30))));
			updateAuthModel.setMoney(springProperties.getInteger("user.auth.bank", 20));
			updateAuthModel.setBankTime(new Date());
			authDao.updateAuthStatusAndAtomicMoneyById(updateAuthModel);
			updateBankModel.setType(FinanceConstant.BankCardType.DEFAULT.key);
		}
		bankCardDao.updateById(updateBankModel);
		UserStatisticDTO dto = new UserStatisticDTO();
		dto.setRegisterDate(Date8Util.date2LocalDate(userModel.getCreateTime()));
		dto.setAgencyId(userModel.getAgencyId());
        dto.setChannelCode(userModel.getChannelCode());
        dto.setActionDate(LocalDate.now());
		dto.setIsBank(CommonConst.YES);
        dto.setVestId(userModel.getVestId());
		jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
		log.info(">>>>发送统计,用户行为(绑卡成功)jms ,data={}", dto);
	}

	@Override
	@Transactional
	public void dealUnBindSuccess(UserBankCardModel bankCardModel) {
		UserBankCardModel updateBankCardModel = new UserBankCardModel();
		updateBankCardModel.setId(bankCardModel.getId());
		updateBankCardModel.setRequestId(bankCardModel.getRequestId());
		updateBankCardModel.setStatus(FinanceConstant.BankCardStatus.UNBIND.key);
		updateBankCardModel.setType(FinanceConstant.BankCardType.OTHER.key);
		bankCardDao.updateById(updateBankCardModel);
		//查询如果只有一张卡 那就该auth  否则不改
		List<UserBankCardModel> list = bankCardDao.selectUserBankListByUserIdStatus(bankCardModel.getUserId(), FinanceConstant.BankCardStatus.BIND.key);
		if (CollectionUtils.isEmpty(list)) {
			AuthenticationModel authenticationModel = authDao.selectById(bankCardModel.getUserId());
			AuthenticationModel updateAuthModel = new AuthenticationModel();
			updateAuthModel.setUserId(authenticationModel.getUserId());
			updateAuthModel.setStatus(CommonConst.STATUS_INVALID);
			updateAuthModel.setBankStatus(CommonConst.STATUS_INVALID);
			updateAuthModel.setMoney(0 - springProperties.getInteger("user.auth.bank", 20));
			authDao.updateAuthStatusAndAtomicMoneyById(updateAuthModel);
		}
	}

	private void checkOrder(Integer userId, Integer bankId, String type) {
		OrderModel orderModel = orderDao.finRecentOrderByUserId(userId);
		if (orderModel != null && bankId.equals(orderModel.getBankId())) {
			Integer stage = orderModel.getStage();
			Integer status = orderModel.getStatus();
			if (stage == OrderConstant.Stage.ARTIFICIAL_AUTH.key) {
				if (status == OrderConstant.Status.FAIL.key) return;
				if (type.equals("unBind")) {
					throw new BusinessException(UserRespCode.HAS_UNFINISHED_ORDER);
				} else {
					throw new BusinessException(UserRespCode.DEFAULT_HAS_UNFINISHED_ORDER);
				}
			}
			if (stage == OrderConstant.Stage.LOAN.key) {
				if (status == OrderConstant.Status.FAIL.key) return;
				if (type.equals("unBind")) {
					throw new BusinessException(UserRespCode.HAS_UNFINISHED_ORDER);
				} else {
					throw new BusinessException(UserRespCode.DEFAULT_HAS_UNFINISHED_ORDER);
				}
			}
			if (stage == OrderConstant.Stage.REPAYMENT.key) {
				if (status == OrderConstant.Status.SUCCESS.key
						|| status == OrderConstant.Status.OVERDUE_LOAN.key
						|| status == OrderConstant.Status.ADVANCE_LOAN.key
						|| status == OrderConstant.Status.CHASE_LOAN.key) return;
				if (type.equals("unBind")) {
					throw new BusinessException(UserRespCode.HAS_UNFINISHED_ORDER);
				} else {
					throw new BusinessException(UserRespCode.DEFAULT_HAS_UNFINISHED_ORDER);
				}
			}
		}
	}

}
