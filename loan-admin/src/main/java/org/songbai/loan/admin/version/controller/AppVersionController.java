package org.songbai.loan.admin.version.controller;


import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.version.model.vo.AppVersionVO;
import org.songbai.loan.admin.version.service.AppVersionService;
import org.songbai.loan.model.version.AppVersionModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by hacfox on 09/10/2017
 */
@RestController
@RequestMapping("/appVersion")
public class AppVersionController {
    @Autowired
    AppVersionService appVersionService;
    @Autowired
    AdminUserHelper adminUserHelper;

    @GetMapping("/queryForceVersion")
    public Response queryForceVersion(AppVersionVO model, Integer page, Integer pageSize, HttpServletRequest request) {

        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 20 : pageSize;
        Integer limit = page * pageSize;
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            model.setAgencyId(agencyId);
        }
        Integer count = appVersionService.getCount(model);
        List<AppVersionVO> list = appVersionService.findVersionPage(model, limit, pageSize);
        Page<AppVersionVO> result = new Page<>(page, pageSize, count);
        result.setData(list);
        return Response.success(result);
    }

    @PostMapping("/updateVersion")
    public Response updateVersion(AppVersionModel model, HttpServletRequest request) {
        Assert.notNull(model.getId(), "id不能为空");
        checkBaseParam(model);

        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        model.setModifyUser(userModel.getId());
        Integer agencyId = userModel.getDataId();
        if (agencyId != 0) {
            model.setAgencyId(agencyId);
        }
        AppVersionModel appVersionModel = appVersionService.findInfoByAgencyIdAndPlatform(model.getAgencyId(), model.getPlatform(), model.getVestId());
        if (appVersionModel != null && !appVersionModel.getId().equals(model.getId())) {
            throw new ResolveMsgException("common.param.repeat", "versionModel");
        }

        Integer tmp = appVersionService.update(model);
        if (tmp > 0) {
            return Response.success();
        }
        return Response.success("更新失败");
    }


    @PostMapping("/addVersion")
    public Response addVersion(AppVersionModel model, HttpServletRequest request) {
        checkBaseParam(model);
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        model.setModifyUser(userModel.getId());
        Integer agencyId = userModel.getDataId();
        if (agencyId != 0) {
            model.setAgencyId(agencyId);
        }
        Integer counts = appVersionService.findVersionByAgencyId(model.getAgencyId(), model.getPlatform(), model.getVestId());
        if (counts > 0) {
            throw new ResolveMsgException("common.param.repeat", "数据已存在，请勿重复提交");
        }
        Integer tmp = appVersionService.addVersion(model);
        if (tmp > 0) {
            return Response.success();
        }
        return Response.success("新增失败");
    }

    private void checkBaseParam(AppVersionModel model) {
        Assert.notNull(model.getPlatform(), "平台字段不能为空");
        Assert.notNull(model.getUpdateAllPreVersions(), "是否更新之前所有版本字段不能为空");
        Assert.notNull(model.getForceUpdateAllPreVersions(), "是否强制更新之前版本字段不能为空");
        Assert.notNull(model.getForceUpdatePreVersions(), "强制更新版本字段不能为空");
        Assert.notNull(model.getDownloadUrl(), "下载链接字段不能为空");
        Assert.notNull(model.getLastVersion(), "最新版本字段不能为空");
        Assert.notNull(model.getUpdateLog(), "更新日志字段不能为空");
        Assert.notNull(model.getRemark(), "备注字段不能为空");
        Assert.notNull(model.getVestId(), "马甲名称不能为空");
    }

}
