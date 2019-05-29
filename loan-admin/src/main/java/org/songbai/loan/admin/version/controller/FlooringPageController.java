package org.songbai.loan.admin.version.controller;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.version.model.po.FloorPagePo;
import org.songbai.loan.admin.version.service.FloorService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.model.version.FloorModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 落地页管理
 */
@RestController
@RequestMapping("/flooringPage")
public class FlooringPageController {

    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    FloorService floorService;

    /**
     * 落地页分页查询
     */
    @RequestMapping(value = "/findFloorPage", method = RequestMethod.GET)
    public Response findFloorPage(FloorPagePo po, HttpServletRequest request) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        po.initLimit();
        return Response.success(floorService.findFloorPage(po));
    }

    @GetMapping(value = "/safe_findFloorList")
    public Response findVestList(HttpServletRequest request,FloorPagePo po) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        po.setStatus(CommonConst.STATUS_VALID);
        po.setPageSize(1000);
        po.initLimit();
        return Response.success(floorService.findFloorPage(po));
    }

    /**
     * 新增
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public Response save(FloorModel floorModel, HttpServletRequest request) {
        checkBaseParam(floorModel);
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (floorModel.getAgencyId() == null) {
            floorModel.setAgencyId(agencyId);
        }
        floorService.saveFloor(floorModel);
        return Response.success();
    }

    private void checkBaseParam(FloorModel model) {
        Assert.notNull(model, "参数不能为空");
        Assert.notNull(model.getFloorName(), "落地页名称不能为空");
        Assert.notNull(model.getFloorUrl(), "落地页不能为空");
        Assert.notNull(model.getStatus(), "状态不能为空");
    }

    /**
     * 修改
     */
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public Response update(FloorModel model, HttpServletRequest request) {
        checkBaseParam(model);
        Assert.notNull(model.getId(), "主键参数不能为空");

        floorService.updateFloor(model);
        return Response.success();
    }

    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public Response updateStatus(FloorModel model, HttpServletRequest request) {
        Assert.notNull(model.getId(), "主键参数不能为空");
        Assert.notNull(model.getStatus(), "状态不能为空");

        floorService.updateFloor(model);
        return Response.success();
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public Response deletes(String ids) {
        Assert.notNull(ids, "请求参数出错");
        String[] idArr = ids.split(",");
        floorService.deleteFloor(idArr);
        return Response.success();
    }


}
