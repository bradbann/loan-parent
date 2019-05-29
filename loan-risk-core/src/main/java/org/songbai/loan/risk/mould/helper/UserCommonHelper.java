package org.songbai.loan.risk.mould.helper;

import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.risk.vo.RiskUserVO;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class UserCommonHelper {

    @Autowired
    private ComUserService userService;


    public RiskUserVO userRisk(String thirdUserId) {

        UserModel userModel = userService.selectUserModelByThridId(thirdUserId);
        if (userModel == null) {
//            return null;
            throw new RuntimeException("not found usermodel for:" + thirdUserId);
        }
        UserInfoModel userInfoModel = userService.findUserInfoByUserId(userModel.getId());
        if (userInfoModel == null) {
            throw new RuntimeException("not found userinfomodel for:" + thirdUserId);
//            return null;
        }


        return RiskUserVO.builder().userId(thirdUserId).user(userModel).userinfo(userInfoModel).build();
    }
}
