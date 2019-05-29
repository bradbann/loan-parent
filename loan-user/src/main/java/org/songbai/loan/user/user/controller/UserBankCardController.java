package org.songbai.loan.user.user.controller;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.helper.upload.AliyunOssHelper;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.user.UserUtil;
import org.songbai.cloud.basics.utils.regular.Regular;
import org.songbai.loan.common.util.BankCardUtil;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.model.finance.FinanceBankModel;
import org.songbai.loan.model.user.AuthenticationModel;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.user.auth.AliyunUtil;
import org.songbai.loan.user.user.auth.Bankcard;
import org.songbai.loan.user.user.dao.AuthenticationDao;
import org.songbai.loan.user.user.dao.FinanceBankDao;
import org.songbai.loan.user.user.dao.UserBankCardDao;
import org.songbai.loan.user.user.service.UserBankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户银行卡认证
 *
 * @author wjl
 * @date 2018年10月30日 10:33:03
 * @description
 */
@RestController
@RequestMapping("/userBankCard")
public class UserBankCardController {
	private static final Logger log = LoggerFactory.getLogger(UserBankCardController.class);
	@Autowired
	private UserBankCardService bankCardService;
	@Autowired
	private Bankcard bankcard;
	@Autowired
	private AliyunOssHelper aliyunOssHelper;
	@Autowired
	private AliyunUtil aliyunUtil;
	@Autowired
	private FinanceBankDao bankDao;
	@Autowired
	private AuthenticationDao authenticationDao;
	@Autowired
	private UserBankCardDao bankCardDao;
	@Autowired
	private ComUserService comUserService;
	@Autowired
	private ComFinanceService comFinanceService;

	/**
	 * ocr识别接口
	 */
	@PostMapping("/v1/auth")
	public Response bindAuth(@RequestParam("multipartFile") MultipartFile multipartFile) {
		Integer userId = UserUtil.getUserId();
		UserModel userModel = comUserService.selectUserModelById(userId);
		if (userModel == null || userModel.getDeleted() == CommonConst.YES){
			return Response.response(UserRespCode.ACCOUNT_NOT_EXISTS,"账户异常,请联系客服处理。");
		}
		AuthenticationModel authenticationModel = authenticationDao.selectById(userId);
		if (authenticationModel == null) {
			throw new BusinessException(UserRespCode.DO_NOT_DELETE_TABLE);
		}
		if (!(authenticationModel.getIdcardStatus() == 1 && authenticationModel.getFaceStatus() == 1
				&& authenticationModel.getInfoStatus() == 1 && authenticationModel.getPhoneStatus() == 1 && authenticationModel.getAlipayStatus() == 1)) {
			throw new BusinessException(UserRespCode.PLEASE_DO_OTHER_AUTH);
		}
		if (multipartFile.isEmpty()) {
			throw new BusinessException(UserRespCode.UPLOAD_DATA_NULL);
		}
		String string = multipartFile.getOriginalFilename().toLowerCase();
		log.info("用户:{}进行银行卡识别,上传图片名字{}", userId, string);
		String fileName = aliyunUtil.generateDateKey(userId, "_bankCard" + string.substring(string.lastIndexOf(".")));//生成图片名称
		String filePath = aliyunOssHelper.innerSaveInputStream(fileName, multipartFile);
		return Response.success(bankcard.bankCardAuth(filePath));
	}

	/**
	 * 绑卡请求 获取验证码
	 */
	@PostMapping("/v1/bind")
	public Response bind(String phone) {
		if (StringUtils.isBlank(phone) || !Regular.checkPhone(phone)) {
			throw new BusinessException(UserRespCode.PHONE_ERROR);
		}
		Integer userId = UserUtil.getUserId();
		UserModel userModel = comUserService.selectUserModelById(userId);
		if (userModel == null || userModel.getDeleted() == CommonConst.YES){
			return Response.response(UserRespCode.ACCOUNT_NOT_EXISTS,"账户异常,请联系客服处理。");
		}
		UserBankCardModel bankCardModel = getLastUpdateBankCard(userId);
		bankCardService.bind(phone, bankCardModel, userId);
		return Response.success();
	}

	/**
	 * 绑卡验证码确认
	 */
	@PostMapping("/v1/bindConfirm")
	public Response bindConfirm(String code) {
		Assert.hasText(code, "验证码不能为空");
		if (code.length() == 6) {
			Integer userId = UserUtil.getUserId();
			UserBankCardModel bankCardModel = getLastUpdateBankCard(userId);
			String platformCode = bankCardModel.getBindPlatform();
			if (StringUtils.isBlank(platformCode)) {
				throw new BusinessException(UserRespCode.PLEASE_GET_MSG, "请重新填写银行卡信息");
			}
			bankCardService.bindConfirm(code, bankCardModel);
		} else {
			throw new BusinessException(UserRespCode.MSG_CODE_ERROR);
		}
		return Response.success();
	}

	/**
	 * 银行卡解绑
	 */
	@PostMapping("/unBind")
	public Response unBindCard(Integer id) {
		Integer userId = UserUtil.getUserId();
		UserModel userModel = comUserService.selectUserModelById(userId);
		if (userModel == null || userModel.getDeleted() == CommonConst.YES){
			return Response.response(UserRespCode.ACCOUNT_NOT_EXISTS,"账户异常,请联系客服处理。");
		}
		bankCardService.unBind(userId, id);
		return Response.success();
	}

	/**
	 * 设为默认收款卡
	 */
	@PostMapping("/bindDefault")
	public Response bindDefault(Integer id) {
		Integer userId = UserUtil.getUserId();
		UserModel userModel = comUserService.selectUserModelById(userId);
		if (userModel == null || userModel.getDeleted() == CommonConst.YES){
			return Response.response(UserRespCode.ACCOUNT_NOT_EXISTS,"账户异常,请联系客服处理。");
		}
		bankCardService.bindDefault(userId, id);
		return Response.success();
	}

	/**
	 * 保存银行卡信息
	 */
	@PostMapping("/v1/save")
	public Response save(String name, String bankName, String bankCardNum, String bankCode,String bankPhone) {
		Assert.hasText(name, "姓名不能为空");
		Assert.hasText(bankName, "银行名称不能为空");
		Assert.hasText(bankCode, "银行代码不能为空");
		Assert.hasText(bankPhone, "手机号不能为空");
		if (!(StringUtils.isNotBlank(bankCardNum) && BankCardUtil.matchLuhn(bankCardNum))) {
			throw new BusinessException(UserRespCode.BANKCARD_ERROR);
		}
		bankCardService.save(name, bankName, bankCardNum, bankCode, bankPhone,UserUtil.getUserId());
		return Response.success();
	}

	/**
	 * 根据银行卡识别出银行
	 */
	@GetMapping("/getBankByBankNum")
	public Response getBankByBankNum(String bankNum){
		Integer userId = UserUtil.getUserId();
		UserModel userModel = comUserService.selectUserModelById(userId);
		String bankCode = bankCardService.getBankByBankNum(bankNum);
		FinanceBankModel bankModel = comFinanceService.getBankModelByBankCodeAndPlatformId(userModel.getAgencyId(),bankCode);
		if (bankModel == null){
			throw new BusinessException(UserRespCode.NOT_SUPPORT_THIS_BANK);
		}
		return Response.success(bankModel);
	}

	/**
	 * 我的银行卡
	 */
	@GetMapping("/v1/detail")
	public Response userBankList() {
		Integer userId = UserUtil.getUserId();
		UserModel userModel = comUserService.selectUserModelById(userId);
		if (userModel == null || userModel.getDeleted() == CommonConst.YES){
			return Response.response(UserRespCode.ACCOUNT_NOT_EXISTS,"账户异常,请联系客服处理。");
		}
		return Response.success(bankCardService.list(userId));
	}

	/**
	 * 支付时的银行卡列表
	 */
	@GetMapping("/getUserBankList")
	public Response getUserBankList(){
		Integer userId = UserUtil.getUserId();
		UserModel userModel = comUserService.selectUserModelById(userId);
		if (userModel == null || userModel.getDeleted() == CommonConst.YES){
			return Response.response(UserRespCode.ACCOUNT_NOT_EXISTS,"账户异常,请联系客服处理。");
		}
		return Response.success(bankCardService.all(userId));
	}

	/**
	 * 获取银行列表
	 */
	@GetMapping("/bankList")
	public Response bankList() {
		return Response.success(bankDao.selectAll());
	}

	private UserBankCardModel getLastUpdateBankCard(Integer userId) {
		List<UserBankCardModel> list = bankCardDao.selectUserBankListByUserIdStatus(userId, null);
		if (CollectionUtils.isEmpty(list)) {
			throw new BusinessException(UserRespCode.REQUEST_BINDCARD_FAILED);
		}
		return list.get(0);
	}

	@Deprecated
	@PostMapping("/auth")
	public Response auth(@RequestParam("multipartFile") MultipartFile multipartFile) {
		throw new BusinessException(UserRespCode.PLEASE_UPDATE_APP);
	}

	@Deprecated
	@PostMapping("/bind")
	public Response bind(String phone, String bankCardNum) {
		throw new BusinessException(UserRespCode.PLEASE_UPDATE_APP);
	}

	@Deprecated
	@PostMapping("/bindConfirm")
	public Response bindConfirmOld(String code) {
		throw new BusinessException(UserRespCode.PLEASE_UPDATE_APP);
	}

	@Deprecated
	@PostMapping("/save")
	public Response save(UserBankCardModel model) {
		throw new BusinessException(UserRespCode.PLEASE_UPDATE_APP);
	}

	@Deprecated
	@GetMapping("/detail")
	public Response detail() {
		throw new BusinessException(UserRespCode.PLEASE_UPDATE_APP);
	}

}
