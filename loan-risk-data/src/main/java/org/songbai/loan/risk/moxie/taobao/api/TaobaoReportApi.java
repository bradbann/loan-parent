package org.songbai.loan.risk.moxie.taobao.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface TaobaoReportApi {


    @GET("/gateway/taobao/v4/report/{taskId}")
    Call<String> getReport(@Path("taskId") String taskId);


}
