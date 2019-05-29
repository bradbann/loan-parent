package org.songbai.loan.push.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.version.AppVestModel;

/**
 * Created by mr.czh on 2017/11/20.
 */
public interface AdminVestDao extends BaseMapper<AppVestModel> {


    AppVestModel findVestById(@Param("vestId") Integer vestId);

}
