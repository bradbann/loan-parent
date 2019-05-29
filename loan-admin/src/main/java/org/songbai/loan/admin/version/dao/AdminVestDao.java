package org.songbai.loan.admin.version.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.version.model.vo.AppVestVO;
import org.songbai.loan.model.version.AppVestModel;

import java.util.List;

/**
 * Created by mr.czh on 2017/11/20.
 */
public interface AdminVestDao extends BaseMapper<AppVestModel> {

    Integer queryRows(@Param("model") AppVestModel vestModel);

    List<AppVestVO> findByPage(@Param("model") AppVestModel vestModel, @Param("offset") Integer page, @Param("pageSize") Integer pageSize);

    void deleteByIds(@Param("ids") String[] ids);

    AppVestModel findVestByVestCode(@Param("vestCode") String vestCode, @Param("agencyId") Integer agencyId);

    List<AppVestModel> findVestList(@Param("model") AppVestModel model);

    int findVestStartPush(@Param("pushSenderId") Integer pushSenderId);
}
