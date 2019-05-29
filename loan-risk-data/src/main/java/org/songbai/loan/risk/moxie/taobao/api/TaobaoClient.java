package org.songbai.loan.risk.moxie.taobao.api;

import lombok.extern.slf4j.Slf4j;
import org.songbai.loan.risk.moxie.api.MoxieClient;
import org.songbai.loan.risk.moxie.taobao.model.dto.TaobaoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

@Component
@Slf4j
public class TaobaoClient extends MoxieClient {


    private TaobaoApi api;


    @Autowired
    public TaobaoClient(@Value("${moxie.api.baseUrl}") String apiBaseUrl,
                        @Value("${moxie.api.token}") String apiToken) {
        super(apiBaseUrl, apiToken);
        api = retrofit.create(TaobaoApi.class);
    }

    public TaobaoData getData(String taskId) throws IOException {


        Call<TaobaoData> call = api.getData(taskId);
        Response<TaobaoData> response = call.execute();
        if (response.code() == 200) {
            return response.body();
        } else {
            log.info("getData, status:{}, message:{}", response.code(), response.message());
        }
        return null;
    }

}
  
