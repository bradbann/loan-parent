package org.songbai.loan.user.user.model.po;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.constant.resp.UserRespCode;

import java.util.Date;

@Data
public class UserInfoPo {
    private Integer userId;
    private String name;//真实姓名
    private String idcardNum;//身份证号
    private String idcardAddress;//身份证住址
    private String validation;//身份证有效期
    private String education;//学历
    private String address;//现居地址
    private String addressTime;//居住时间
    private String marry;//是否结婚
    private String job;//工作
    private String jobName;//工作单位
    private String companyAddress;//公司地址
    private String firstContact;//紧急联系人
    private String firstPhone;//紧急联系人电话
    private String otherContact;//备用联系人
    private String otherPhone;//备用联系人电话
    private Integer agencyId;
}
