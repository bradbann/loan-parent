package org.songbai.loan.risk.model.user;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.songbai.loan.risk.model.mould.RiskMouldVariableModel;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Document(collection = "risk_user_mould_catalog")
public class RiskUserMouldCatalogModel {

    @Id
    private String id;
    @Indexed
    private String userId;
    private String orderNumber;
    private Integer mouldId;// 模型ID
    private Integer catalog;

    private Integer riskScore; // 风险级别得分

    private Integer scoring; // 计分项得分

    private Integer stampman; //是否标记转人工
    private Integer stampreject; //是否标记拒绝


    private Integer finalScore; // 最终得分 。加权后的得分

    private Integer finalResult; // 0默认， 1通过， 2拒绝,  3:人工

    private List<RiskMouldVariableModel> hitVariable;
    private JSONObject variableParam;

    private Date createTime;
    private Date updateTime;

}
