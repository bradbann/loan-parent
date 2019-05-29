package org.songbai.loan.model.agency;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("dream_u_agency_host")
public class AgencyHostModel {
    Integer id;
    Integer agencyId;
    String host;//域名
    Date createTime;
}
