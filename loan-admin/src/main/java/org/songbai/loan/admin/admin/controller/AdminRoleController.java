package org.songbai.loan.admin.admin.controller;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.RespCode;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminMenuResourceModel;
import org.songbai.loan.admin.admin.model.AdminPageElementResourceModel;
import org.songbai.loan.admin.admin.model.AdminRoleModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminMenuResouceService;
import org.songbai.loan.admin.admin.service.AdminPageElementResourceService;
import org.songbai.loan.admin.admin.service.AdminRoleService;
import org.songbai.loan.admin.admin.service.AdminUrlAccessResourceService;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SUNDONG_
 */

@Controller
@RequestMapping("/role")
public class AdminRoleController {
    @Autowired
    AdminRoleService adminRoleService;

    @Autowired
    AdminUrlAccessResourceService adminUrlAccessResourceService;

    @Autowired
    AdminMenuResouceService adminMenuResouceService;

    @Autowired
    AdminPageElementResourceService adminPageElementResourceService;


    @Autowired
    AdminUserHelper adminUserHelper;

    /**
     * 角色保存
     */
    @RequestMapping(value = "/addRole")
    @ResponseBody
    public Response addRole(String name, String description, Integer deptId, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        AdminRoleModel roleModel = new AdminRoleModel();
        if (StringUtils.isBlank(name)) {
            return Response.response(RespCode.SERVER_ERROR, "添加角色名称不能为空");
        }
        roleModel.setName(name);
        roleModel.setDescription(description);
        roleModel.setDataId(userModel.getDataId());
        roleModel.setIsAdmin(0);
        roleModel.setCreateId(userModel.getId());
        if (deptId != null ){
            roleModel.setDeptId(deptId);
        }else {
            roleModel.setDeptId(userModel.getDeptId());
        }

        adminRoleService.createAdminRole(roleModel);
        return Response.success();
    }

    /**
     * 多条件查询角色
     */
    @RequestMapping(value = "/pagingQuery")
    @ResponseBody
    public Response pagingQuery(Integer page, Integer pageSize, String name, String description,
                                HttpServletRequest request) {

        pageSize = pageSize == null ? Page.DEFAULE_PAGESIZE : pageSize;
        page = page == null ? 0 : page;
        Map<String, Object> param = new HashMap<>();
        if (name != null) {
            param.put("name", "%" + name + "%");
        }
        if (description != null) {
            param.put("description", "%" + description + "%");
        }
        param.put("category", AdminRoleModel.CATEGORY);
        Page<AdminRoleModel> data = adminRoleService.pagingQueryPermissions(name, description, AdminRoleModel.CATEGORY,
                adminUserHelper.getAgencyId(request), page, pageSize);
        return Response.success(data);
    }

    /**
     * 角色修改
     */
    @RequestMapping(value = "/updateRole")
    @ResponseBody
    public Response updateRole(Integer id, String name, String description) {
        Assert.notNull(id, "角色id不能为空");
        Assert.notNull(name, "角色名称不能为空");

        AdminRoleModel roleModel = new AdminRoleModel();
        roleModel.setId(id);
        roleModel.setName(name);
        roleModel.setDescription(description);

        adminRoleService.updateAdminRole(roleModel);
        return Response.success();
    }

    /**
     * 删除多条角色和该角色相关的关系数据纪录如：角色权限关系数据、角色URL资源权限数据
     */
    @RequestMapping(value = "/deleteRoles")
    @ResponseBody
    public Response deleteRoles(String ids) {

        Assert.notNull(ids, "要删除的角色Id不能为空");
        List<Integer> roleIds = new ArrayList<Integer>();
        String temp[] = ids.split(",");
        for (String st : temp) {
            Integer roleId = Integer.valueOf(st);
            if (roleId.equals(1)) {
                return Response.response(701, "代理管理员不能删除");
            }
            roleIds.add(roleId);
        }
        List<AdminRoleModel> nodelete = adminRoleService.deleteRoles(roleIds);

        return Response.success(nodelete);
    }

    /**
     * 为角色删除分配的URL访问权限资源
     * @param urlAccessResourceIds
     *            要删除的权限资源Id（不是分配纪录的Id）
     */
    // @RequestMapping(value = "/terminateUrlAccessResourcesFromRole")
    // @ResponseBody
    // public Response terminateUrlAccessResourcesFromRole(Integer roleId,
    // String urlAccessResourceIds,
    // HttpServletRequest request) {
    //
    // Assert.notNull(roleId, "角色Id不能为空");
    // Assert.notNull(urlAccessResourceIds, "要删除的资源权限Id不能为空");
    //
    // String[] urlIds = urlAccessResourceIds.split(",");
    // List<Integer> urlAccessResourceIdList = new ArrayList<Integer>();
    // for (int i = 0; i < urlIds.length; i++) {
    // urlAccessResourceIdList.add(Integer.valueOf(urlIds[i]));
    // }
    //
    // adminUrlAccessResourceService.terminateUrlAccessResourcesFromRole(roleId,
    // urlAccessResourceIdList,
    // adminUserHelper.getAgencyId(request));
    // ;
    // return Response.success();
    // }

    /**
     * 跟据角色Id获得为该角色已授权的菜单资源数据 <strong>菜单数据是包含子菜单的，注意菜单的层级关系数据
     *
     * </strong>
     *
     * @return
     */
    @RequestMapping(value = "/findMenuResourceTreeSelectItemByRoleId")
    @ResponseBody
    public Response findMenuResourceTreeSelectItemByRoleId(Integer roleId, HttpServletRequest request) {
        Integer actorId = adminUserHelper.getAdminUserId(request);
        List<AdminMenuResourceModel> allMenuResources = adminMenuResouceService
                .findMenuResourceTreeSelectItemByRoleId(roleId, adminUserHelper.getAgencyId(request), actorId);
        return Response.success(allMenuResources);
    }

    /**
     * 根据角色ID获得未分配给该角色的页面元素资源
     */
    @RequestMapping(value = "/pagingQueryNotGrantPageElementResourcesByRoleId")
    @ResponseBody
    public Response pagingQueryNotGrantPageElementResourcesByRoleId(String name, String description, String identifier,
                                                                    Integer page, Integer pageSize, Integer roleId, HttpServletRequest request) {

        Assert.notNull(roleId, "角色Id不能为空");
        pageSize = pageSize == null ? Page.DEFAULE_PAGESIZE : pageSize;
        page = page == null ? 0 : page;
        Page<AdminPageElementResourceModel> data = adminPageElementResourceService
                .pagingQueryNotGrantPageElementsByRoleId(name, description, identifier, roleId,
                        adminUserHelper.getAgencyId(request), page, pageSize);
        return Response.success(data);
    }

    /**
     * 根据角色id 和数据类型获取各自所有的菜单 页面元素与url访问地址(平台或者各个渠道 0表示平台 1表示一级渠道 2表示二级渠道)
     */
    @RequestMapping(value = "/getAllMenuPageUrl")
    @ResponseBody
    public Response getAllMenuPageUrl(Integer roleId, HttpServletRequest request) {
        Integer type = 0;
        AdminUserModel actor = adminUserHelper.getAdminUser(request);

        return Response.success(adminMenuResouceService.getAllMenuPageUrl(roleId, type, actor));
    }

    /**
     * 根据角色id、权限资源id、数据类型获取角色下的用户列表<strong>因为权限资源来自于同一张表其id是唯一的</strong>
     */
    @RequestMapping(value = "/grantResourcesToRole")
    @ResponseBody
    public Response grantResourcesToRole(Integer roleId, String securityResourceIds, HttpServletRequest request) {
        Assert.notNull(roleId, "角色id不能为空");
        Integer dataId = adminUserHelper.getAgencyId(request);
        if (securityResourceIds.equals("")) {
            adminMenuResouceService.deleteAdminResourceAssignmentsByAuthorityId(roleId);
        } else {
            String[] ids = securityResourceIds.split(",");
            List<Integer> securityResourceList = new ArrayList<Integer>();
            for (String id : ids) {
                securityResourceList.add(Integer.valueOf(id));
            }
            adminMenuResouceService.saveMenuPageUrlToRole(roleId, securityResourceList, dataId);
        }
        return Response.success();

    }

    // *******************************************************************************************************

    /**
     * 查询平台渠道下的角色列表
     */
    @RequestMapping(value = "/pagingQueryChannelRoles")
    @ResponseBody
    public Response pagingQueryChannelRoles(Integer page, Integer pageSize, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        return Response.success(
                adminRoleService.pagingQueryChannelRoles(userModel, page, pageSize));

    }

    /**
     * 获取 当前一级渠道的所有角色 ZY需要
     */
    @RequestMapping(value = "/findRoles")
    @ResponseBody
    public Response findRoles(HttpServletRequest request) {
        return Response.success(adminRoleService.findRoles(adminUserHelper.getAgencyId(request)));

    }
}
