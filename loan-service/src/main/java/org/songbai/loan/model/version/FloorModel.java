package org.songbai.loan.model.version;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("dream_v_floor")
public class FloorModel {
    Integer id;
    Integer agencyId;
    String floorName;
    String floorUrl;
    Integer status;
    String remark;

    Date createTime;
    Date updateTime;

}
