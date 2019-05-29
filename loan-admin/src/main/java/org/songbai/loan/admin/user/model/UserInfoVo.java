package org.songbai.loan.admin.user.model;

import lombok.Data;
import org.songbai.loan.model.user.UserInfoModel;

@Data
public class UserInfoVo extends UserInfoModel {
    String firstName;
    String otherName;
}
