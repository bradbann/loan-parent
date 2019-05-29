package org.songbai.loan.model.chase;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("loan_a_chase_feed")
public class ChaseFeedModel {
    Integer id;
    Integer userId;
    String chaseId;//催收单号
    Integer feedType;//催收类型
    String feedRemark;//催收备注
    Date createTime;
    Integer actorId;//催收人
    Integer agencyId;
    Integer deptId;
    String orderNumber;//订单编号
}
