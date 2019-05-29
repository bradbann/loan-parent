package org.songbai.loan.admin.version.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.version.service.AdminVestService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.rediskey.AdminRedisKey;
import org.songbai.loan.model.version.AppVestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 马甲
 */
@RestController
@RequestMapping("/vest")
public class AdminVestController {

    private static Logger logger = LoggerFactory.getLogger(AdminVestController.class);
    @Autowired
    private AdminVestService vestService;
    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    /**
     * 马甲分页查询
     */
    @GetMapping(value = "/findVestPage")
    public Response findVestPage(Integer page, Integer pageSize, HttpServletRequest request, AppVestModel model, Integer vestId) {
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 20 : pageSize;
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            model.setAgencyId(agencyId);
        }
        if (vestId != null) model.setId(vestId);
        return Response.success(vestService.findByPage(model, page, pageSize));
    }

    @GetMapping(value = "/safe_findVestList")
    public Response findVestList(HttpServletRequest request, AppVestModel model) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            model.setAgencyId(agencyId);
        }
        model.setStatus(CommonConst.STATUS_VALID);
        return Response.success(vestService.findVestList(model));
    }

    /**
     * 新增
     */
    @RequestMapping(value = "/saveVest", method = RequestMethod.POST)
    public Response saveVest(AppVestModel model, HttpServletRequest request) {
        checkBaseParam(model);
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            model.setAgencyId(agencyId);
        }
        vestService.saveVest(model);
        return Response.success();
    }

    private void checkBaseParam(AppVestModel model) {
        Assert.notNull(model, "参数不能为空");
        Assert.notNull(model.getName(), "马甲名称不能为空");
        Assert.notNull(model.getPactId(), "用户协议不能为空");
        Assert.notNull(model.getStatus(), "状态不能为空");
        Assert.notNull(model.getRefuseStatus(), "审核拒绝不能为空");
        Assert.notNull(model.getGroupId(), "groupId不能为空");
        Assert.notNull(model.getPushSenderId(), "推送名称不能为空");
        Assert.notNull(model.getPlatform(), "来源不能为空");


    }

    /**
     * 修改
     */
    @RequestMapping(value = "/updateVest", method = RequestMethod.POST)
    public Response updateVest(AppVestModel model, HttpServletRequest request) {
        checkBaseParam(model);
        Assert.notNull(model.getId(), "主键参数不能为空");

        vestService.updateVest(model);
        redisTemplate.opsForHash().delete(AdminRedisKey.AGENCY_VEST, model.getId());
        return Response.success();
    }

    /**
     * 删除
     */
    @RequestMapping(value = "/deleteVest", method = RequestMethod.POST)
    public Response deletes(String ids) {
        Assert.notNull(ids, "请求参数出错");
        String[] idArr = ids.split(",");
        vestService.deleteVest(idArr);
        return Response.success();
    }


}
