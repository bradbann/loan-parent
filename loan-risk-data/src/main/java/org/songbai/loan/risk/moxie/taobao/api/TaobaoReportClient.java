package org.songbai.loan.risk.moxie.taobao.api;

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
public class TaobaoReportClient extends MoxieReportClient {

    private TaobaoReportApi api;

    @Autowired
    public TaobaoReportClient(@Value("${moxie.api.baseUrl}") String apiBaseUrl,
                              @Value("${moxie.api.token}") String apiToken) {
        super(apiBaseUrl, apiToken);
        api = retrofit.create(TaobaoReportApi.class);
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(TaobaoReportClient.class);

    public String getReport(String taskId) throws IOException {
        Call<String> reportBasic = api.getReport(taskId);
        Response<String> response = reportBasic.execute();
        if (response.code() == 200) {
            return response.body();
        } else {
            LOGGER.info("getReportBasic, status:{}, message:{}", response.code(), response.message());
        }
        return null;
    }


}
  
