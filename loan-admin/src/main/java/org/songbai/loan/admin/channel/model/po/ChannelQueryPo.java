package org.songbai.loan.admin.channel.model.po;

import lombok.Data;
import org.songbai.loan.common.util.PageRow;

@Data
public class ChannelQueryPo extends PageRow {
    Integer channelId;//渠道id
    String startDate;
    String endDate;
    Integer agencyId;
}
