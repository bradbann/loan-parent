package org.songbai.loan.risk.model.user;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@Data
@Document(collection = "risk_user_var_temp")
@CompoundIndex(def = "{\"userId\":1,\"catalog\":1}", name = "idx_var_temp_userId")
public class RiskUserVarTempModel {

    @Id
    private String id;

    @Indexed
    private String userId;

    @Indexed
    private String sources;

    @Indexed
    private Integer catalog;


    private String variableKey;
    private String variableValue;

    private Date createTime;


}
