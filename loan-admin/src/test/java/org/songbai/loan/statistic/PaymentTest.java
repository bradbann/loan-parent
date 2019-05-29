package org.songbai.loan.statistic;

import com.yeepay.g3.sdk.yop.client.YopClient3;
import com.yeepay.g3.sdk.yop.client.YopRequest;
import com.yeepay.g3.sdk.yop.client.YopResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.songbai.loan.common.finance.YiBaoUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Map;

/**
 * @author: wjl
 * @date: 2019/1/2 16:40
 * Description:
 */

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
public class PaymentTest {

	@Test
	public void teste() throws IOException {
		/*YopRequest yopRequest = new YopRequest("OPR:10025662404");
		log.info("yio：{}",yopRequest);
		yopRequest.addParam("customerNumber", "10025662404");
		yopRequest.addParam("groupNumber", "10025662404");
		yopRequest.addParam("batchNo", "12345678910QWER");
		yopRequest.addParam("orderId", "12345678910QWER");
		yopRequest.addParam("amount", "0.01");
		yopRequest.addParam("urgency", "1");
		yopRequest.addParam("accountName", "吴佳乐");
		yopRequest.addParam("accountNumber", "6217003320063726916");
		yopRequest.addParam("bankCode", "CCB");
		yopRequest.addParam("feeType", "SOURCE");
		log.error("{}", JSON.toJSONString(yopRequest));
		YopResponse yopResponse = YopClient3.postRsa("/rest/v1.0/balance/transfer_send", yopRequest);
		log.info("{}",yopResponse);
		Map<String, String> result = YiBaoUtil.parseResponse(yopResponse.getStringResult());
		log.info("{}",result);*/
//		--------请求查询余额--------
		YopRequest yopRequest = new YopRequest("OPR:10025662404");
		log.info("yio：{}",yopRequest);
		yopRequest.addParam("customerNumber", "10025662404");
		YopResponse yopResponse = YopClient3.postRsa("/rest/v1.0/balance/query_customer_amount", yopRequest);
		log.info("{}",yopResponse);
		Map<String, String> result = YiBaoUtil.parseResponse(yopResponse.getStringResult());
		log.info("{}",result);
	}
}
