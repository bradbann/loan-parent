package org.songbai.loan.model.agency;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("loan_a_agency_config")
public class AgencyConfigModel {
    Integer id;
    Integer agencyId;
    Integer createId;
    Integer amount;//金额
    Double feeRate;//手续费
    Date createTime;
    Date updateTime;
}
