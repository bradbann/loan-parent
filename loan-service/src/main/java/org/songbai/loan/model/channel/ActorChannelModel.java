package org.songbai.loan.model.channel;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("loan_a_actor_channel")
public class ActorChannelModel {
    Integer id;
    Integer agencyId;
    Integer actorId;
    Integer channelId;
    Date createTime;
}
