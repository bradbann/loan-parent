package org.songbai.loan.admin.channel.model.vo;

import lombok.Data;

import java.util.Date;

@Data
public class ChannelUserVo {
    Integer id;
    Integer userId;
    String userPhone;
    Integer agencyId;
    Integer channelId;
    Date createTime;
    String channelName;
}
