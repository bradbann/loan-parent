package org.songbai.loan.admin.version.service.impl;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.version.dao.FloorDao;
import org.songbai.loan.admin.version.model.po.FloorPagePo;
import org.songbai.loan.admin.version.model.vo.FloorPageVo;
import org.songbai.loan.admin.version.service.FloorService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.model.version.FloorModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FloorServiceImpl implements FloorService {
    @Autowired
    FloorDao floorDao;
    @Autowired
    ComAgencyService comAgencyService;

    @Override
    public void saveFloor(FloorModel floorModel) {
        if (floorModel.getStatus() == null) floorModel.setStatus(CommonConst.STATUS_VALID);
        floorDao.insert(floorModel);
    }

    @Override
    public void updateFloor(FloorModel model) {
        floorDao.updateById(model);
    }

    @Override
    public void deleteFloor(String[] idArr) {
        floorDao.deleteFloorByIds(idArr);
    }

    @Override
    public Page<FloorPageVo> findFloorPage(FloorPagePo po) {
        Integer count = floorDao.queryPageCount(po);
        if (count == 0) return new Page<>(po.getPage(), po.getPageSize(), count, new ArrayList<>());
        List<FloorPageVo> list = floorDao.findPageList(po);
        return new Page<>(po.getPage(), po.getPageSize(), count, list);
    }
}
