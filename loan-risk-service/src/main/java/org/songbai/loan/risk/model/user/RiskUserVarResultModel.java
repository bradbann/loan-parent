package org.songbai.loan.risk.model.user;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document(collection = "risk_user_var_result")
public class RiskUserVarResultModel {

    @Id
    private String id;

    @Indexed
    private String userId;

    private Integer catalog;

    private JSONObject data; //JSON 字段，里面的只是

    private Date createTime;
    private Date updateTime;

}
