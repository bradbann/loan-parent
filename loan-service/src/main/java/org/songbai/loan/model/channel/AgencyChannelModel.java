package org.songbai.loan.model.channel;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("loan_a_agency_channel")
public class AgencyChannelModel {
    Integer id;
    Integer agencyId;
    String channelCode;
    String channelName;
    Integer channelStatus;//是否启用，0-否，1-是
    Double showPercent;//展现百分比
    Integer channelType;
    Date createTime;
    Date updateTime;
    String landCode;//渠道随机code
    String landHtml;//落地页展示页面
    String landUrl;//落地页地址
    Integer vestId;//对应的马甲id
//    Integer isVest;//是否是马甲，0-否，1-是
//    Integer aboutOwner;//关于我们, dream_v_app_manager.id

}
