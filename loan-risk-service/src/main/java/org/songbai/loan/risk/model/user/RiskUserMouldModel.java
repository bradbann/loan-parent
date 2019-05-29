package org.songbai.loan.risk.model.user;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "risk_user_mould")
public class RiskUserMouldModel {

    private Integer id;
    private String userId;
    private Integer mouldId;// 模型ID

    private Integer riskScore; // 风险级别得分

    private Integer scoring; // 计分项得分

    private Integer stampman; //是否标记转人工
    private Integer stampreject; //是否标记拒绝


    private Integer finalScore; // 最终得分 。加权后的得分

    private Integer finalResult; // 0默认， 1通过， 2拒绝。3:人工


    private Date createTime;
    private Date updateTime;

}
