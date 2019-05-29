package org.songbai.loan.admin.version.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.version.model.po.FloorPagePo;
import org.songbai.loan.admin.version.model.vo.FloorPageVo;
import org.songbai.loan.model.version.FloorModel;
import org.springframework.stereotype.Component;


@Component
public interface FloorService {


    void saveFloor(FloorModel floorModel);

    void updateFloor(FloorModel model);

    void deleteFloor(String[] idArr);

    Page<FloorPageVo> findFloorPage(FloorPagePo po);
}
