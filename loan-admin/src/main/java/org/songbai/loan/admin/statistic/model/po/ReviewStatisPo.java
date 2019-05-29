package org.songbai.loan.admin.statistic.model.po;

import lombok.Data;
import org.songbai.loan.common.util.PageRow;

@Data
public class ReviewStatisPo extends PageRow {
    String startCalcDate;
    String endCalcDate;
    Integer guest;//客群
    Integer vestId;//马甲id
    Integer isTotal = 0;//是否汇总，0-否,1-是
    Integer isVest = 0;//是否马甲统计
    Integer agencyId;
    Integer actorId;
    Integer isChannelOrder = 0;//是否为渠道订单统计
    Integer isProduct = 0;//是否为标的统计
    Integer productId;//
    Integer productGroupId;
    String channelCode;

}
