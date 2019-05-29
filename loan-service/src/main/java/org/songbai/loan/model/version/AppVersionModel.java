package org.songbai.loan.model.version;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;


@Data
@TableName("dream_v_version")
public class AppVersionModel {
    Integer id;
    Integer agencyId;//与版本绑定一起的代理ID
    Integer vestId;//马甲id
    String channelCode;//渠道code
    String downloadUrl; //下载链接
    Integer forceUpdateAllPreVersions;//是否强制更新之前版本(0否,1是)
    Integer updateAllPreVersions;//更新所有先前的版本(0否,1是)
    String forceUpdatePreVersions;//需强制更新的版本
    String lastVersion;//最新版本
    String updateLog;//更新日志
    String remark; //备注
    Integer platform;//平台2:ios 1:android
    Integer modifyUser;
    Date modifyTime;
    Date createTime;

}
