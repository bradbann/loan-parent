package org.songbai.loan.admin.push.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.push.model.vo.PushSenderVO;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.sms.PushSenderModel;

import java.util.List;

public interface PushSenderDao extends BaseMapper<PushSenderModel> {
    int selectSenderCount(@Param("agencyId") Integer agencyId);

    List<PushSenderVO> selectSenderList(@Param("page") PageRow page, @Param("agencyId") Integer agencyId);

    List<PushSenderModel> findPushSenderSelected(@Param("agencyId") Integer agencyId);

    PushSenderModel findStartPushSenderByIdAndAgencyId(@Param("id") Integer pushSenderId, @Param("agencyId") Integer agencyId);

}