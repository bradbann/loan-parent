package org.songbai.loan.admin.order.vo;

import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.songbai.cloud.basics.utils.base.BeanUtil;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.constant.user.OrderConstant.Stage;
import org.songbai.loan.constant.user.OrderConstant.Status;
import org.songbai.loan.model.loan.OrderModel;

@Data
public class OrderPageVo extends OrderModel {

    String userName;
    String userPhone;
    String orderStatusName;
    String chaseStatus;//是否已分配催收，0-否，1-是
    String deptName;
    String guestName;
    String chaseActorStatus;//是否已分配到人员
    String idcardNum;//身份证号
    String channelName;
    String agencyName;
    String actorName;//取单人名称
    String thirdId;
    String chaseDeptName;//催收部门名称
    String chaseActorName;//催收人名称
    String lastFeedName;//最后一次催收类型
    String returnRemark;//财务退回备注
    String vestName;//马甲名称
    String channelCode;
    Integer vestId;
    Integer scoring;//机审评分


    public OrderPageVo change(OrderPageVo model) {
        OrderPageVo vo = new OrderPageVo();
        BeanUtil.copyNotNullProperties(model, vo);
        Integer stage = vo.getStage();
        Integer status = vo.getStatus();
        if (stage.equals(Stage.ARTIFICIAL_AUTH.key)) {//人工复审
            if (status.equals(Status.WAIT.key)) {
                vo.setOrderStatusName("等待复审");
            } else if (status.equals(Status.FAIL.key)) {
                vo.setOrderStatusName("复审失败");
            }
        } else if (stage.equals(Stage.LOAN.key)) {//
            if (status.equals(Status.WAIT.key)) {
                vo.setOrderStatusName("复审成功");
            }
        } else if (stage.equals(Stage.REPAYMENT.key)) {
            if (status.equals(Status.PROCESSING.key)) {
                vo.setOrderStatusName("还款中");
            }else if (status.equals(Status.FAIL.key)) {
                vo.setOrderStatusName("坏账");
            } else if (status.equals(Status.OVERDUE.key)) {
                vo.setOrderStatusName("逾期");
            } else if (status.equals(Status.OVERDUE_LOAN.key) || status.equals(Status.CHASE_LOAN.key)) {
                vo.setOrderStatusName("已还款");
            }
        }

        if (StringUtils.isEmpty(vo.getChaseId())) {
            vo.setChaseStatus("未分单");
        } else {
            vo.setChaseStatus("已分单");
        }

        if (vo.getChaseActorId() != null) {
            vo.setChaseActorStatus("已分单");
        } else {
            vo.setChaseActorStatus("未分单");
        }
        if (vo.getGuest() != null) {
            vo.setGuestName(OrderConstant.Guest.parse(vo.getGuest()).name);
        }

        return vo;
    }

}
