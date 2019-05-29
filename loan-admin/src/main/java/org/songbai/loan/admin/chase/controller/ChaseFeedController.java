package org.songbai.loan.admin.chase.controller;

import org.apache.commons.lang.StringUtils;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminDeptService;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.chase.po.ChaseFeedPo;
import org.songbai.loan.admin.chase.service.ChaseFeedService;
import org.songbai.loan.config.Accessible;
import org.songbai.loan.model.chase.ChaseFeedModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 催收反馈
 */
@RestController
@RequestMapping("/chaseFeed")
public class ChaseFeedController {
    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    ChaseFeedService chaseFeedService;
    @Autowired
    AdminDeptService adminDeptService;

    /**
     * 催收记录分页
     */
    @GetMapping("/getChaseFeedPage")
    public Response getChaseFeedPage(ChaseFeedPo po, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
//        AdminUserModel userModel = new AdminUserModel();
//        userModel.setDataId(0);
//        userModel.setRoleType(1);
//        userModel.setIsManager(0);
//        userModel.setDeptId(3);
        if (userModel.getDataId() != 0) {
            po.setAgencyId(userModel.getDataId());
        }
        List<Integer> deptIds = adminDeptService.findDeptIdsByType(userModel, null);
        //取消从用户详情跳进来的时候查不到数据问题
        if (StringUtils.isNotEmpty(po.getUserId())) deptIds = null;
        po.initLimit();
        String startTime = po.getStartChaseDate();
        String endTime = po.getEndChaseDate();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(startTime) && org.apache.commons.lang3.StringUtils.isNotBlank(endTime) && startTime.equals(endTime)){
            po.setStartChaseDate(startTime +" 00:00:00");
            po.setEndChaseDate(endTime + " 23:59:59");
        }
        return Response.success(chaseFeedService.getChaseFeedPage(po, deptIds));
    }

    /**
     * 根据催收单号查询催收记录
     */
    @GetMapping("/getChaseListByChaseId")
    public Response getChaseListByChaseId(String chaseId, HttpServletRequest request) {
        Assert.notNull(chaseId, "催收单号不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);

        return Response.success(chaseFeedService.getChaseListByChaseId(chaseId, agencyId == 0 ? null : agencyId));
    }

    /**
     * 新增催收反馈
     */
    @PostMapping(value = "/addChaseFeeBack")
    @Accessible(onlyAgency = true)
    public Response addChaseFeeBack(HttpServletRequest request, ChaseFeedModel chaseFeedModel) {
        Assert.notNull(chaseFeedModel, "参数不能为空");
        Assert.notNull(chaseFeedModel.getFeedType(), "催收类型不能为空");
        Assert.notNull(chaseFeedModel.getChaseId(), "催收单号不能为空");
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        chaseFeedModel.setActorId(userModel.getId());
        chaseFeedModel.setAgencyId(userModel.getDataId());
        chaseFeedService.addChaseFeedBack(chaseFeedModel);
        return Response.success();
    }
}
