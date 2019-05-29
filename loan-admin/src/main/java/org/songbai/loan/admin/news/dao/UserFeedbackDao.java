package org.songbai.loan.admin.news.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.news.model.po.UserFeedPo;
import org.songbai.loan.model.news.UserFeedbackModel;
import org.songbai.loan.model.news.UserFeedbackVo;

import java.util.List;

public interface UserFeedbackDao extends BaseMapper<UserFeedbackModel> {

    void updateUserFeedback(Integer id);

    UserFeedbackModel queryUserFeedbackById(Integer id);

    List<UserFeedbackVo> qureyList(@Param("po") UserFeedPo po);

    Integer qureyCount(@Param("po") UserFeedPo po);

}
