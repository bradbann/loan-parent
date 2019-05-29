//package org.songbai.loan.user.finance.controller;
//
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.songbai.loan.constant.user.FinanceConstant;
//import org.songbai.loan.model.finance.FinanceIOModel;
//import org.songbai.loan.user.finance.dao.FinanceIODao;
//import org.songbai.loan.user.finance.service.impl.ChangJiePayServiceImpl;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//@SpringBootTest
//@RunWith(SpringRunner.class)
//public class RepaymentControllerTest {
//
//private static final Logger log = LoggerFactory.getLogger(RepaymentControllerTest.class);
//	@Autowired
//	private RepaymentController repaymentController;
//
//	@Autowired
//	private FinanceIODao ioDao;
//	@Autowired
//	private ChangJiePayServiceImpl changJiePayService;
//
//	@Test
//	public void payConfirm() {
//	}
//
//	@Test
//	public void pay() {
//
//		String orderNum = "L18121313574P1PIA0M";
//
//		String bankCardNum = "6217003320063726916";
//
//		Integer userId = 19  ;
//
//		/*String response = "MOV3q58BlwPBu9T09sgb8PWgUIHR5LJBo46HoChOGvm3MZr_LNS0xyVJh-qyEgtW0RZZKFAidQe8k5EQ9HZLTJSIozi9VSI0j7prwAtFg2-E1M4Ij54vnMmmoHxZL0WdUlZCPDHCXdazQhhYv58BmfKoh3b1efA-wKr4VI5u0E0nI93OZ5qqO9owHDltu5ShMLfb8EXzBBV2dpwFejPwyYPmQCM4CSy8VJgqMhDDKX6DHAdYObMJ0IXmAkq_U85a-ZKCw-XC2yf9y0aJut6-58l_3leDVj9YyN6-7JY9SiIt3X6uE7kPR59aCCy-DTGBD5ugrY0DyppNEZmOno901A$A902IafZzPjuWuVa-vMmPEdpPEhZGwY5n0ju28HlTYuuLx_r2l9FWGR4caUXvTlfMJOQ0AeF3GR9MpByZPmZvCofLKEI57kK0YUiV_ph90okIEW46aIflglrwAVm8GSklNEBSGeHQcehgfhw6i8RB9QXLAY6AVApCQdUOkKAtoiOluds8aPdxS1kYmg4f7C8mbouFdf2ANCdh_Huz4lH5ReXv-cvm4PmesYHNVrqfOIisz4WyNAF5eMwR8SzF2iGRvecu_fUoYekztELGPHstV4-IMXy1jgJ6MKMu2_yjT2OPrvoSn6iizYA9lqLikHfLDfD0KeNSCadQzptMRtRRjtPHjgVQRTutufIK4XQUkwlnz2b0oIFQBr7VlTsmbm2vzle_-nbrRgXeLPCXwt-G-uPyy41DpSsUPzHkWYklo-Ch-MNp_czUaRlFJ1a3Vmt62X_-QWa31kaeDHIkHSPSuSyDLnXu9C9v9rGdLk5OrvXiX42SNYK60SG5GGsB79u3dAWG83DaBmBfCY8G5y1su-zthM49ZBGT3fSnXVeP1ilLmQ7M4BMZY8m_MSsgLCyeZWDqf7EwE8ww39YkItq_4qgdkrnH7lcR8leDil6Kqo_7ltHmRcsWdxzDfpAkpje1v4Zc30LraaNTPRw9t3hB15SyIcp_wvPfZ79yVC5HrgofGkKd1BzHJ_-0X3thhAgEgcfViJ7lzKyU3Mlp21BJvpgCPmpqhQSE28f3Uo-7IvUjo1bl7X-OpkMXHBoZD0Eu1a27lmmc_DrQ67uEvgikUqUPoTsDEeL4VIjSSsqtDzS3AeJwaiKiV-FGX_BWPGL4GYjerm0Df3GmfmGddB3WSWK00Fr6LI09oNOF1SFz0CDxyZiSohjZy1Ab65XBjNc$AES$SHA256";
//		YopRequest yopRequest = new YopRequest("SQKK10025662404");
//		AppSdkConfig appSdkConfig = yopRequest.getAppSdkConfig();
//		PrivateKey privateKey = appSdkConfig.getDefaultIsvPrivateKey();
//		PublicKey publicKey = appSdkConfig.getDefaultYopPublicKey();
//		Map<String, String> map = YiBaoUtil.Decrypt2(response, privateKey,publicKey);
//
//		log.info("{}",map);*/
//		repaymentController.validateAndChooseMethod(orderNum, bankCardNum, userId, FinanceConstant.PAY);
//	}
//
//	@Test
//	public void payQuery(){
//		FinanceIOModel financeIOModel = ioDao.selectById(1899);
//		changJiePayService.payQuery(financeIOModel);
//	}
//
//}