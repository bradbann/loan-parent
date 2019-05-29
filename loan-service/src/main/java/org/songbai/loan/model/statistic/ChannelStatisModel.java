package org.songbai.loan.model.statistic;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
@TableName("loan_s_channel_statis")
public class ChannelStatisModel {
    Integer id;
    Integer agencyId;
    Integer channelId;
    Integer registerCount;
    Integer loginCount;
    Integer orderCount;
    Integer transferCount;
    LocalDate calcDate;
    Date createTime;
}
