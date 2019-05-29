package org.songbai.loan.risk.vo;

import lombok.*;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskUserVO {

    private String userId;

    private UserModel user;
    private UserInfoModel userinfo;

}


