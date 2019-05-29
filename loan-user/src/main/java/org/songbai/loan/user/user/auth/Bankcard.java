/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package org.songbai.loan.user.user.auth;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.user.UserUtil;
import org.songbai.cloud.basics.utils.regular.Regular;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.model.finance.FinanceBankModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.songbai.loan.service.user.dao.ComUserDao;
import org.songbai.loan.user.user.service.UserBankCardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 银行卡识别
 */
@Component
public class Bankcard {

	private static final Logger log = LoggerFactory.getLogger(Bankcard.class);

	@Autowired
	private AliyunUtil aliyunUtil;
	@Autowired
	private AuthService authService;
	@Autowired
	private ComFinanceService comFinanceService;
	@Autowired
	private ComUserDao comUserDao;
	@Autowired
	private UserBankCardService bankCardService;

	/**
	 * 重要提示代码中所需工具类
	 * FileUtil,Base64Util,HttpUtil,GsonUtils请从
	 * https://ai.baidu.com/file/658A35ABAB2D404FBF903F64D47C1F72
	 * https://ai.baidu.com/file/C8D81F3301E24D2892968F09AE1AD6E2
	 * https://ai.baidu.com/file/544D677F5D4E4F17B4122FBD60DB82B3
	 * https://ai.baidu.com/file/470B3ACCA3FE43788B5A963BF0B625F3
	 * 下载
	 */
	public Map<String, String> bankCardAuth(String filePath) {
		// 银行卡识别url
		String bankcardIdentificate = "https://aip.baidubce.com/rest/2.0/ocr/v1/bankcard";
		String result;
		try {
			String imgStr = aliyunUtil.getImgBase64ByImgPath(filePath);
			String params = URLEncoder.encode("image", "UTF-8") + "=" + URLEncoder.encode(imgStr, "UTF-8");
			/**
			 * 线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
			 */
			String accessToken = authService.getAuth();
			result = HttpUtil.post(bankcardIdentificate, accessToken, params);
		} catch (Exception e) {
			log.info("调取百度OCR银行卡识别接口失败");
			throw new BusinessException(UserRespCode.INTERNET_ERROR);
		}
		log.info("百度OCR银行卡接口返回结果：{}", result);
		if (!result.contains("result")) {
			throw new BusinessException(UserRespCode.REQUEST_BINDCARD_FAILED);
		}
		JSONObject jsonObject = JSON.parseObject(result).getJSONObject("result");
		Map<String, String> map = new HashMap<>();
		String bankCardNum = jsonObject.getString("bank_card_number").replace(" ", "");
		String bankName = jsonObject.getString("bank_name").replace(" ", "");
		if (StringUtils.isNotBlank(bankCardNum) && Regular.checkBankCardMatch(bankCardNum)
				&& StringUtils.isNotBlank(bankName)) {
			map.put("bank_card_number", bankCardNum);
			map.put("valid_date", jsonObject.getString("valid_date"));
		} else {
			throw new BusinessException(UserRespCode.REQUEST_BINDCARD_FAILED);
		}
		UserModel userModel = comUserDao.selectUserModelById(UserUtil.getUserId());
		if (userModel == null) {
			throw new BusinessException(UserRespCode.ACCOUNT_NOT_EXISTS);
		}
		//拿到平台银行code
		String bankCode = bankCardService.getBankByBankNum(bankCardNum);

		FinanceBankModel bankModel = comFinanceService.getBankModelByBankCodeAndPlatformId(userModel.getAgencyId(), bankCode);
		if (bankModel == null) {
			throw new BusinessException(UserRespCode.NOT_SUPPORT_THIS_BANK);
		}
		map.put("bank_name", bankModel.getBankName());
		map.put("bank_code", bankCode);
		map.put("bank_icon", bankModel.getIcon());
		return map;
	}
}
