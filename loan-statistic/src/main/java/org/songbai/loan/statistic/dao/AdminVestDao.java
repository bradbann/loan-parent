package org.songbai.loan.statistic.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.songbai.loan.model.version.AppVestModel;

import java.util.Set;

/**
 * Created by mr.czh on 2017/11/20.
 */
public interface AdminVestDao extends BaseMapper<AppVestModel> {

    Set<Integer> findStartVestList();

}
