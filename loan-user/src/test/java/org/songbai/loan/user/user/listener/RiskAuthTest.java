package org.songbai.loan.user.user.listener;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RiskAuthTest {
	@Autowired
	private RiskAuthTaskListener listener;

	@Test
	public void test() {
		List<String> list = Arrays.asList("{\"sources\":\"mx_taobao\",\"status\":1,\"taskId\":\"0cd2498a-fc3e-11e8-b99f-00163e102a72\",\"userId\":\"9a65f4bffb164e8593cf933494ea5707\"}", "{\"sources\":\"mx_taobao\",\"status\":1,\"taskId\":\"0cd2498a-fc3e-11e8-b99f-00163e102a72\",\"userId\":\"9a65f4bffb164e8593cf933494ea5707\"}", "{\"sources\":\"mx_taobao\",\"status\":5,\"taskId\":\"0cd2498a-fc3e-11e8-b99f-00163e102a72\",\"userId\":\"9a65f4bffb164e8593cf933494ea5707\"}", "{\"sources\":\"mx_taobao_report\",\"status\":5,\"taskId\":\"0cd2498a-fc3e-11e8-b99f-00163e102a72\",\"userId\":\"9a65f4bffb164e8593cf933494ea5707\"}");

		for (String msg : list) {
			listener.authTask(msg);

		}
	}

}
