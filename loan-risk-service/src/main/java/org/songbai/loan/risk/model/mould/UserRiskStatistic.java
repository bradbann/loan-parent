package org.songbai.loan.risk.model.mould;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Author: qmw
 * Date: 2018/11/20 7:29 PM
 * 用户风控模型参数统计
 */
@Data
@Document(collection = "user_statistic")
public class UserRiskStatistic {

    @Id
    private String id;
    @Indexed
    private String phone; // 手机号
    @Indexed
    private String userId; // 用户id

    private String data; // 统计的json数据
}
