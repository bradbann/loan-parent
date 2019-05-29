package org.songbai.loan.admin.news.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.news.model.po.UserFeedPo;
import org.songbai.loan.model.news.UserFeedbackModel;
import org.songbai.loan.model.news.UserFeedbackVo;

public interface UserFeedbackService {

    public void updateUserFeedback(Integer id);

    public UserFeedbackModel findUserFeedbackById(Integer id);

    public Page<UserFeedbackVo> qureyPage(UserFeedPo po);

}
