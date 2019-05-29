package org.songbai.loan.admin.statistic.model.po;

import lombok.Data;
import org.songbai.loan.common.util.PageRow;

@Data
public class ChannelStatisPo extends PageRow {
    String startCalcDate;
    String endCalcDate;
    Integer agencyId;
    Integer isTotal = 0;//是否汇总
    Integer isVest = 0;//是否马甲统计
    Integer vestId;
    String channelCode;
}
