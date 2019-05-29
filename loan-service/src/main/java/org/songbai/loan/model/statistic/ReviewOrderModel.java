package org.songbai.loan.model.statistic;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@TableName("loan_s_review")
public class ReviewOrderModel {
    Integer id;
    Integer agencyId;
    Integer vestId;
    String channelCode;
    Integer productId;
    Integer productGroupId;
    Integer orderCount;//订单量
    Integer orderNewCount;//新客提单量
    Integer orderOldCount;//老客提单量
    Integer orderWaitCount;//待复审订单量
    Integer reviewNewSuccCount;//新客复审通过
    Integer reviewOldSuccCount;//老客复审通过
    Integer reviewNewFailCount;//新客复审拒绝
    Integer reviewOldFailCount;//老客复审拒绝
    Integer expireNewCount;//新客超期订单数
    Integer expireOldCount;//老客超期订单数
    Integer machineNewSuccCount;//新客机审通过,到人审
    Integer machineOldSuccCount;//老客机审通过,到人审
    Integer machineToTransNewCount;//新客机审到财务
    Integer machineToTransOldCount;//老客机审到财务
    Integer machineNewFailCount;//新客机审拒绝
    Integer machineOldFailCount;//老客机审拒绝
    Integer firstOverdueNewCount;//新客首逾订单量
    Integer firstOverdueOldCount;//老客首逾订单量
    Integer inOverdueNewCount;//新客逾期订单量
    Integer inOverdueOldCount;//老客逾期订单量
    LocalDate calcDate;//统计日期
    Date createTime;
}
