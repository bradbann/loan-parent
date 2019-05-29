package org.songbai.loan.admin.chase.vo;

import lombok.Data;
import org.songbai.loan.model.chase.ChaseFeedModel;

@Data
public class ChaseFeedVo extends ChaseFeedModel {
    String orderNumber;//订单号
    String deptName;//催收组
    String userName;//用户姓名
    String userPhone;//手机号
    String chaseActorName;//催收人
    String feedTypeName;//催收类型名称
    String agencyName;


}
