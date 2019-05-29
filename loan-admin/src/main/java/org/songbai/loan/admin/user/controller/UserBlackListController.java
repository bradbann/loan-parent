package org.songbai.loan.admin.user.controller;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.admin.support.AgencySecurityHelper;
import org.songbai.loan.admin.user.model.UserQueryVo;
import org.songbai.loan.admin.user.service.UserBlackListService;
import org.songbai.loan.config.Accessible;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 后台黑名单列表
 *
 * @author wjl
 * @date 2018年10月30日 16:27:06
 * @description
 */
@RestController
@RequestMapping("/user")
public class UserBlackListController {

    @Autowired
    private UserBlackListService blackListService;
    @Autowired
    private AdminUserHelper adminUserHelper;
    @Autowired
    private AgencySecurityHelper agencySecurityHelper;

    @GetMapping("/blackList")
    public Response getList(UserQueryVo model, HttpServletRequest request) {
        model.setPage(model.getPage() == null ? 0 : model.getPage());
        model.setPageSize(model.getPageSize() == null ? 20 : model.getPageSize());
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            model.setAgencyId(agencyId);
        }
        return Response.success(blackListService.getList(model));
    }

	@Accessible(onlyAgency = true)
	@PostMapping("/addBlack")
	public Response addBlack(String userId, String name, String phone, String idcardNum, Integer status, String limitStart, String limitEnd, String remark, HttpServletRequest request) {
		Assert.notNull(userId, "id不能为空");
		Assert.notNull(name, "姓名不能为空");
		Assert.notNull(phone, "手机号不能为空");
		Assert.notNull(idcardNum, "身份证号不能为空");
		Assert.notNull(status, "状态不能为空");
		Assert.notNull(limitStart, "开始时间不能为空");
		Assert.notNull(limitEnd, "结束时间不能为空");
		AdminUserModel userModel = adminUserHelper.getAdminUser(request);
		if (userModel != null) {
			blackListService.addBlack(userId, name, phone, idcardNum, status, limitStart, limitEnd, remark, userModel);
		}
		return Response.success();
	}

	@Accessible(onlyAgency = true)
	@PostMapping("/removeBlack")
	public Response removeBlock(String userId, String idcardNum, String phone, HttpServletRequest request) {
		Assert.notNull(userId, "userId不能为空");
		Assert.notNull(idcardNum, "身份证号不能为空");
		Assert.notNull(phone, "手机号不能为空");
		AdminUserModel userModel = adminUserHelper.getAdminUser(request);
		if (userModel != null) {
			blackListService.removeBlack(userId, idcardNum, phone, userModel);
		}
		return Response.success();
	}

	@Accessible(onlyAgency = true)
	@PostMapping("/updateBlack")
	public Response updateBlack(String userId, String name, Integer type, String idcardNum, String phone, String limitStart, String limitEnd, String remark, HttpServletRequest request) {
		Assert.notNull(userId, "id不能为空");
		Assert.notNull(name, "姓名不能为空");
		Assert.notNull(phone, "手机号不能为空");
		Assert.notNull(idcardNum, "身份证号不能为空");
		Assert.notNull(type, "状态不能为空");
		Assert.notNull(limitStart, "开始时间不能为空");
		Assert.notNull(limitEnd, "结束时间不能为空");
		AdminUserModel userModel = adminUserHelper.getAdminUser(request);
		if (userModel != null) {
			blackListService.updateBlack(userId, name, type, idcardNum, phone, limitStart, limitEnd, remark, userModel);
		}
		return Response.success();
	}
}
