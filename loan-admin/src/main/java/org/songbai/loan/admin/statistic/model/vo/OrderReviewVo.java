package org.songbai.loan.admin.statistic.model.vo;

import lombok.Data;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.statistic.ReviewOrderModel;

@Data
public class OrderReviewVo extends ReviewOrderModel {

    //以下字段为页面显示字段
    String statisDate;
    String agencyName;
    String vestName;
    String productName;
    String productGroupName;
    Integer machineToTransCount = 0;//风控通过量
    String machineToTransRate = "0.00";//风控通过率
    Integer machineFailCount = 0;//风控拒绝量
    String machineFailRate = "0.00"; //风控拒绝率
    Integer machineSuccCount = 0;//复审量
    String machineSuccRate = "0.00";//复审率
    Integer reviewSuccCount = 0;//复审通过量
    String reviewSuccRate = "0.00";//复审通过率
    Integer reviewFailCount = 0;//复审拒绝量
    String reviewFailRate = "0.00";//复审拒绝率
    Integer expireCount;//超期订单数
    String expireRate = "0.00";//超期订单率
    Integer firstOverdueCount;//首逾量
    String firstOverdueRate = "0.00";//首逾率
    Integer inOverdueCount;//在逾量
    String inOverdueRate = "0.00";//在逾率
    Integer succCount = 0;//总通过订单量
    String succRate = "0.00";//总通过率

    public void calcRate(OrderReviewVo vo, Integer guest) {

        if (guest != null && guest == OrderConstant.Guest.NEW_GUEST.key) {
            vo.setMachineToTransOldCount(0);
            vo.setMachineOldFailCount(0);
            vo.setMachineOldSuccCount(0);
            vo.setReviewOldFailCount(0);
            vo.setReviewOldSuccCount(0);
            vo.setFirstOverdueOldCount(0);
            vo.setInOverdueOldCount(0);
            vo.setOrderOldCount(0);
            vo.setExpireOldCount(0);
        } else if (guest != null && guest == OrderConstant.Guest.OLD_GUEST.key) {
            vo.setMachineToTransNewCount(0);
            vo.setMachineNewFailCount(0);
            vo.setMachineNewSuccCount(0);
            vo.setReviewNewFailCount(0);
            vo.setReviewNewSuccCount(0);
            vo.setFirstOverdueNewCount(0);
            vo.setInOverdueNewCount(0);
            vo.setOrderNewCount(0);
            vo.setExpireNewCount(0);
        }

        vo.setOrderCount(vo.getOrderNewCount() + vo.getOrderOldCount());
        vo.setMachineToTransCount(vo.getMachineToTransNewCount() + vo.getMachineToTransOldCount());
        vo.setMachineFailCount(vo.getMachineNewFailCount() + vo.getMachineOldFailCount());
        vo.setMachineSuccCount(vo.getMachineNewSuccCount() + vo.getMachineOldSuccCount());
        vo.setExpireCount(vo.getExpireNewCount() + vo.getExpireOldCount());
        vo.setReviewSuccCount(vo.getReviewNewSuccCount() + vo.getReviewOldSuccCount());
        vo.setReviewFailCount(vo.getReviewNewFailCount() + vo.getReviewOldFailCount());
        vo.setFirstOverdueCount(vo.getFirstOverdueNewCount() + vo.getFirstOverdueOldCount());
        vo.setInOverdueCount(vo.getInOverdueNewCount() + vo.getInOverdueOldCount());
        vo.setSuccCount(vo.getMachineToTransCount() + vo.getReviewSuccCount());


        if (vo.getOrderCount() > 0) {
            Double orderCount = vo.getOrderCount() / 100D;

            vo.setMachineToTransRate(FormatUtil.formatDouble2(vo.getMachineToTransCount() / orderCount));
            vo.setMachineFailRate(FormatUtil.formatDouble2(vo.getMachineFailCount() / orderCount));
            vo.setMachineSuccRate(FormatUtil.formatDouble2((vo.getMachineSuccCount() / orderCount)));
            vo.setSuccRate(FormatUtil.formatDouble2((vo.getReviewSuccCount() + vo.getMachineToTransCount()) / orderCount));
        }

        if (vo.getMachineSuccCount() > 0) {
            Double machineSuccCount = vo.getMachineSuccCount() / 100D;
            vo.setReviewSuccRate(FormatUtil.formatDouble2(vo.getReviewSuccCount() / machineSuccCount));
            vo.setReviewFailRate(FormatUtil.formatDouble2(vo.getReviewFailCount() / machineSuccCount));
        }

        if (vo.getExpireCount() > 0) {
            Double expireCount = vo.getExpireCount() / 100D;
            vo.setFirstOverdueRate(FormatUtil.formatDouble2(vo.getFirstOverdueCount() / expireCount));
            vo.setInOverdueRate(FormatUtil.formatDouble2(vo.getInOverdueCount() / expireCount));
        }
    }

}
