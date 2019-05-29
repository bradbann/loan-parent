package org.songbai.loan.admin.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.RespCode;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.utils.regular.Regular;
import org.songbai.loan.admin.admin.model.AdminActorModel;
import org.songbai.loan.admin.admin.model.AdminRoleModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminRoleService;
import org.songbai.loan.admin.admin.service.AdminUserService;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.admin.support.AgencySecurityHelper;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.resp.UserRespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequestMapping("/adminUser")
public class AdminUserController {
    @Autowired
    AdminUserService adminUserService;

    @Autowired
    AdminRoleService adminRoleService;

    @Autowired
    AgencySecurityHelper agencySecurityHelper;

    @Autowired
    AdminUserHelper adminUserHelper;

    Logger logger = LoggerFactory.getLogger(AdminUserController.class);

    /**
     * 多条件分页查询User列表
     *
     * @return
     */
    @RequestMapping(value = "/pagingQuery")
    @ResponseBody
    public Response pagingQuery(Integer page, Integer pageSize, String name, String userAccount, String description,
                                Boolean disabled, String phone, String email, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        pageSize = pageSize == null ? Page.DEFAULE_PAGESIZE : pageSize;
        page = page == null ? 0 : page;
//        if (userModel.getDataId().equals(0)) {
//            userModel.setDeptId(null);
//            userModel.setDataId(null);
//        }
        Page<AdminActorModel> resultPage = adminUserService.getUserList(page, pageSize, userAccount, name, email,
                disabled, phone, userModel);
        return Response.success(resultPage);

    }

    /**
     * 添加User
     *
     * @return
     */
    @RequestMapping(value = "/add")
    @ResponseBody
    public Response add(String name, String userAccount, String description, String userPortrait,
                        String phone, String email,
                        HttpServletRequest request, Integer dataId, Integer deptId, Integer isManager, Integer isValidate) {
        Assert.notNull(name, "用户名称不能为空");
        Assert.notNull(userAccount, "用户登录账号不能为空");
        Assert.notNull(phone, "用户手机号不能为空");

        if (!Regular.checkPhone(phone)) {
            throw new BusinessException(UserRespCode.PHONE_WRONG);
        }
        AdminUserModel currentUser = adminUserHelper.getAdminUser(request);
        // 验证用户账户是否已经存在
        AdminUserModel userModel = new AdminUserModel();
        if (dataId == null) {
            dataId = adminUserHelper.getAgencyId(request);
        }
        userModel.setDataId(dataId);
        if (adminUserService.hasUserAccount(userAccount, dataId)) {
            return Response.response(RespCode.SERVER_ERROR, "添加用户失败，已存在的用户账户");
        }
        if (adminUserService.hasUserPhone(phone, dataId)) {
            return Response.response(RespCode.SERVER_ERROR, "添加用户失败，已存在的用户手机号");
        }
        userModel.setPhone(phone);
        userModel.setEmail(email);
        userModel.setName(name);
        userModel.setUserAccount(userAccount);
        userModel.setDescription(description);
        userModel.setUserPortrait(userPortrait);
        userModel.setRoleType(0);
        userModel.setDeptId(deptId);
        userModel.setIsManager(isManager);
        if (isValidate != null) userModel.setIsValidate(isValidate);
        if (currentUser != null) {
            userModel.setResourceType(currentUser.getResourceType());
            userModel.setCreateOwnerId(currentUser.getId() != null ? currentUser.getId() : 0);
            userModel.setCreateOwner(currentUser.getName() != null ? currentUser.getName() : "admin");
        }

        if (isManager.equals(1)) {
            Integer count = adminUserService.getDeptManagerCountByDeptId(deptId);
            if (count > 0) {
                throw new BusinessException(AdminRespCode.DEPT_HAVA_MANAGER);
            }

        }
        adminUserService.createAdminUser(userModel, null);
        return Response.success();
    }

    /**
     * 修改用户信息
     *
     * @return
     */
    @RequestMapping(value = "/update")
    @ResponseBody
    public Response update(Integer id, String name, String userAccount, String phone, String email, String description, String userPortrait,
                           HttpServletRequest request, Integer deptId, Integer isManager, Integer isValidate) {
        Assert.notNull(name, "用户名称不能为空");
        Assert.notNull(userAccount, "用户登录账号不能为空");

        Integer agencyId = adminUserHelper.getAgencyId(request);
        AdminUserModel oldUser = adminUserService.getUser(id);
        if (oldUser != null && agencyId != 0 && !agencyId.equals(oldUser.getDataId())) {
            throw new ResolveMsgException(AdminRespCode.ACCESS_ADMIN);
        }

        AdminUserModel userModel = new AdminUserModel();
        userModel.setId(id);
        userModel.setName(name);
        userModel.setPhone(phone);
        userModel.setEmail(email);
        userModel.setUserAccount(userAccount);
        userModel.setDescription(description);
        userModel.setUserPortrait(userPortrait);
        userModel.setDeptId(deptId);
        userModel.setIsManager(isManager);
        if (isValidate != null) userModel.setIsValidate(isValidate);
        adminUserService.updateAdminUserExceptPassword(userModel);
        return Response.success();
    }

    /**
     * 删除多条用户
     *
     * @return
     */
    @RequestMapping(value = "/deleteUsers")
    @ResponseBody
    public Response deleteUsers(String ids, HttpServletRequest request) {
        Integer dataId = adminUserHelper.getAgencyId(request);
        Assert.notNull(ids, "要删除的用户的id不能为空");

        String[] id = ids.split(",");
        List<Integer> idList = new ArrayList<Integer>();
        for (String anId : id) {
            // idList.add(Integer.valueOf(id[i]));
            // }pagingQuery
            // List<Integer> list = new ArrayList<Integer>();
            // for(Integer i : idList ){
            AdminUserModel model = adminUserService.getUser(Integer.valueOf(anId));
            if (model.getRoleType().equals(1)) {
                return Response.response(701, "admin管理员不允许删除");
            } else {
                idList.add(Integer.valueOf(anId));
            }
        }
        adminUserService.deleteAdminUsers(idList, dataId);

        return Response.success();

    }

    /**
     * 分页获得未授权给User的角色
     *
     * @return
     */
    @RequestMapping(value = "/pagingQueryNotGrantRoles")
    @ResponseBody
    public Response pagingQueryNotGrantRoles(Integer page, Integer pageSize, Integer userId, String name,
                                             HttpServletRequest request) {
        Assert.notNull(userId, "被授权id不能为空!");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
//        Integer agencyId = 0;
        Page<AdminRoleModel> pageResult = adminRoleService.pagingQueryNotGrantRoles(page, pageSize, userId, name, userModel);
        return Response.success(pageResult);

    }

    /**
     * 分页获得已授权给User的角色
     *
     * @return
     */
    @RequestMapping(value = "/pagingQueryGrantRoleByUserId")
    @ResponseBody
    public Response pagingQueryGrantRoleByUserId(Integer page, Integer pageSize, Integer userId, String name,
                                                 HttpServletRequest request) {
        Assert.notNull(userId, "被授权id不能为空!");
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
//        Integer agencyId = 0;
        Page<AdminRoleModel> pageResult = adminRoleService.pagingQueryGrantRoles(page, pageSize, userId, name,
                userModel);
        return Response.success(pageResult);
    }

    /**
     * 为参与者授权
     *
     * @return
     */
    @RequestMapping(value = "/grantAuthorityToActor")
    @ResponseBody
    public Response grantAuthorityToActor(String authorityIds, Integer actorId, HttpServletRequest request) {

        // adminUserService.grantAuthoritysToActor(actorId, authorityIdList);
        Assert.notNull(actorId, "参与者id不能为空");
        Assert.notNull(authorityIds, "授权id不能为空");
        List<Integer> authorityIdList = new ArrayList<Integer>();
        String temp[] = authorityIds.split(",");
        for (String aTemp : temp) {
            authorityIdList.add(Integer.valueOf(aTemp));
        }
        adminUserService.grantAuthoritysToActor(actorId, authorityIdList, adminUserHelper.getAgencyId(request));
        return Response.success();
    }

    /**
     * 批量为用户撤销角色 暂时是只撤销角色，后期可能会也撤销权限
     *
     * @return
     */
    @RequestMapping(value = "/terminateAuthorizationByUserInRoles")
    @ResponseBody
    public Response terminateAuthorizationByUserInRoles(String roleIds, Integer userId, HttpServletRequest request) {

        Assert.notNull(userId, "参与者id不能为空");
        Assert.notNull(roleIds, "授权id不能为空");
        List<Integer> authorityIdList = new ArrayList<Integer>();
        String temp[] = roleIds.split(",");
        for (String aTemp : temp) {
            authorityIdList.add(Integer.valueOf(aTemp));
        }
        adminRoleService.terminateAuthorizationByUserIdAuthorithIds(userId, authorityIdList, adminUserHelper.getAgencyId(request));

        return Response.success();

    }

    /**
     * 用户修改密码 注意前端密码是经过MD5加密后的密码，否则将不能正确匹配用户的密码
     *
     * @return
     */
    @RequestMapping(value = "/safe_updatePassword")
    @ResponseBody
    public Response updatePassword(String oldUserPassword, String userPassword, HttpServletRequest request,
                                   HttpServletResponse resp) {

        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        if (userModel == null || userModel.getId() == null) {
            return Response.response(RespCode.SERVER_ERROR, "修改密码失败，无法获得正确的登录用户信息");
        }
        String oldUserPassword_salt = userModel.handlePassword(oldUserPassword);
        // userModel
        // =adminUserService.getUserByUserAccountPassword(userModel.getUserAccount(),oldUserPassword,
        // userId);
        if (!oldUserPassword_salt.equals(userModel.getPassword())) {
            return Response.response(RespCode.SERVER_ERROR, "修改密码失败，原密码不正确");
        }
        logger.info("用户更改密码:用户{}更改密码为{}", userModel.getName(), userPassword);
        adminUserService.changePassword(userModel.getId(), new AdminUserModel().handlePassword(userPassword));
        return Response.success();

    }

    /**
     * 重置密码(重置为初始密码)
     */
    @PostMapping(value = "/safe_resetPassword")
    @ResponseBody
    public Response resetPassword(HttpServletRequest request, HttpServletResponse resp) {
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null) {
            return Response.response(RespCode.SERVER_ERROR, "重置密码失败，无法获得正确的登录用户信息");
        }
        AdminUserModel userModel = adminUserService.getUser(userId);
        if (userModel == null) {
            return Response.response(RespCode.SERVER_ERROR, "重置密码失败，无法获得正确的登录用户信息");
        }
        adminUserService.changePassword(userId, new AdminUserModel().createPassWord(userModel.getPhone()));
        String phone = userModel.getPhone();
        phone = phone.substring(phone.length() - 6, phone.length());
        return Response.success(phone);

    }

    /**
     * 重置密码(重置为初始密码)
     */
    @PostMapping(value = {"/resetPasswordById", "/resetChannelPassword"})
    @ResponseBody
    public Response resetPasswordById(Integer userId) {
        if (userId == null) {
            return Response.response(RespCode.SERVER_ERROR, "重置密码失败，无法获得用户信息");
        }
        AdminUserModel userModel = adminUserService.getUser(userId);
        if (userModel == null) {
            return Response.response(RespCode.SERVER_ERROR, "重置密码失败，无法获得正确的登录用户信息");
        }
        adminUserService.changePassword(userId, new AdminUserModel().createPassWord(userModel.getPhone()));
        String phone = userModel.getPhone();
        phone = phone.substring(phone.length() - 6, phone.length());
        return Response.success(phone);

    }

    /**
     * 禁用用户
     *
     * @return
     */
    @RequestMapping(value = "/suspend")
    @ResponseBody
    public Response suspend(String userIds) {
        Assert.notNull(userIds, "用户Id不能为空");
        String[] userStrs = userIds.split(",");
        for (String userStr : userStrs) {
            Integer userId = Integer.parseInt(userStr);
            AdminUserModel model = adminUserService.getUser(userId);
            if ("admin".equals(model.getUserAccount())) {
                return Response.response(701, "admin管理员不允许被禁用");
            }
            model.setId(userId);
            model.setDisable(true);
            adminUserService.updateAdminUserExceptPassword(model);
        }
        return Response.success();
    }

    /**
     * 激活用户
     */
    @RequestMapping(value = "/activate")
    @ResponseBody
    public Response activate(String userIds) {
        Assert.notNull(userIds, "用户Id不能为空");
        String[] userStrs = userIds.split(",");
        for (String userStr : userStrs) {
            Integer userId = Integer.parseInt(userStr);
            AdminUserModel model = adminUserService.getUser(userId);
            if ("admin".equals(model.getUserAccount())) {
                return Response.response(701, "admin管理员不允许被操作");
            }
            model.setId(userId);
            model.setDisable(false);
            adminUserService.updateAdminUserExceptPassword(model);
        }
        return Response.success();
    }

    /**
     * 分页查询角色下用户*
     *
     * @return
     */
    @RequestMapping(value = "/pageQueryActorByRoleId")
    @ResponseBody
    public Response pageQueryActorByRoleId(Integer roleId, HttpServletRequest request, Integer page, Integer pageSize) {
        Assert.notNull(roleId, "角色id不能为空");
        Page<AdminUserModel> resultPage = adminUserService.pageQueryActorByRoleId(roleId, adminUserHelper.getAgencyId(request),
                page, pageSize);
        return Response.success(resultPage);
    }

    /**
     * 移除角色下人员
     *
     * @return
     */
    @RequestMapping(value = "/deleteUserInRole")
    @ResponseBody
    public Response deleteUserInRole(Integer roleId, Integer actorId, HttpServletRequest request) {
        adminUserService.deleteUserInRole(roleId, actorId, adminUserHelper.getAgencyId(request));
        return Response.success();
    }

}