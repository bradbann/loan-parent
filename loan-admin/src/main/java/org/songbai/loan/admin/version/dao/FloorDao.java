package org.songbai.loan.admin.version.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.version.model.po.FloorPagePo;
import org.songbai.loan.admin.version.model.vo.FloorPageVo;
import org.songbai.loan.model.version.FloorModel;

import java.util.List;

public interface FloorDao extends BaseMapper<FloorModel> {
    void deleteFloorByIds(@Param("ids") String[] ids);

    Integer queryPageCount(@Param("po") FloorPagePo po);

    List<FloorPageVo> findPageList(@Param("po") FloorPagePo po);

    FloorModel selectFloorByUrl(@Param("landUrl") String landUrl, @Param("agencyId") Integer agencyId);

}
