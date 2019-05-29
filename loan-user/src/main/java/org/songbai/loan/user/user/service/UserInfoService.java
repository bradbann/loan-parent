package org.songbai.loan.user.user.service;

import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.loan.model.user.UserContactModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserInfoService {
    void save(UserInfoModel model, String type, boolean flag);

    UserInfoModel selectByUserId(Integer userId);

    UserInfoModel getUserInfoByIdCardNum(String idCardNum, Integer agencyId, Integer vestId);

    /**
     * 保存用户通讯录
     */
    void saveUserContact(Integer userId, List<UserContactModel> list);

    void doubtfulContactRelation(Integer userId);

    /**
     * 身份证识别
     */
    Ret idcardAuth(MultipartFile multipartFile, Integer userId, String idcardSide);
}
