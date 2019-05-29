package org.songbai.loan.model.version;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("dream_v_app_manager")
public class AppManagerModel {
    Integer id;
    Integer agencyId;
    Integer vestId;//马甲id
    Integer platform;//1-安卓，2-ios
    String title;//标题
    String logoUrl;
    String name;//名称
    String customerQq;//客服qq
    String customerWechat;//客服微信
    String copyRight;//版权
    Integer status;
    Date createTime;

}
