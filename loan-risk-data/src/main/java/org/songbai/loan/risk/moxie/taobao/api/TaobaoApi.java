package org.songbai.loan.risk.moxie.taobao.api;

import org.songbai.loan.risk.moxie.taobao.model.dto.TaobaoData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Modified by liyang on 20171019 接口升级
 */
public interface TaobaoApi {
    /**
     * 根据task_id查询淘宝用户数据
     */
    @GET("/gateway/taobao/v6/data/{taskId}")
    Call<TaobaoData> getData(@Path("taskId") String taskId);

}
