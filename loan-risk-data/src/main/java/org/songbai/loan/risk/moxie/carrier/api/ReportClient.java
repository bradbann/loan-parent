package org.songbai.loan.risk.moxie.carrier.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.risk.moxie.api.MoxieReportClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

@Component
public class ReportClient extends MoxieReportClient {
	
	 private ReportApi api;
	 
	 @Autowired
	public ReportClient(@Value("${moxie.api.baseUrl}") String apiBaseUrl,
            @Value("${moxie.api.token}") String apiToken) {
		super(apiBaseUrl, apiToken);  
		api = retrofit.create(ReportApi.class);
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(ReportClient.class);
	
    public String getReportBasic(String mobile, String taskId) throws IOException {
        Call<String> reportBasic = api.getReportBasic(mobile, taskId);
        Response<String> response = reportBasic.execute();
        if (response.code() == 200) {
            return response.body();
        } else {
            LOGGER.info("getReportBasic, status:{}, message:{}", response.code(), response.message());
        }
        return null;
    }

	
}
  
