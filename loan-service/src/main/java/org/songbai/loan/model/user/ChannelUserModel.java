package org.songbai.loan.model.user;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 渠道用户扣量表
 */
@Data
@TableName("loan_u_channel_user")
public class ChannelUserModel {
    Integer id;
    Integer userId;
    String userPhone;
    Integer agencyId;
    Integer channelId;
    Date createTime;
}
