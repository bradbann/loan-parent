package org.songbai.loan.admin.admin.controller;

import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.dao.AdminDeptDao;
import org.songbai.loan.admin.admin.model.AdminDeptModel;
import org.songbai.loan.admin.admin.model.AdminRoleModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminDeptService;
import org.songbai.loan.admin.admin.service.AdminRoleService;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("dept")
public class AdminDeptController {
    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    AdminDeptService adminDeptService;
    @Autowired
    AdminRoleService adminRoleService;
    @Autowired
    AdminDeptDao adminDeptDao;

    @GetMapping(value = "/findDeptPage")
    public Response findDeptPage(HttpServletRequest request, Integer deptId) {

        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
//        Integer agencyId = 0;
//        AdminUserModel userModel = new AdminUserModel();
//        userModel.setDeptId(deptId);
        return Response.success(adminDeptService.findDeptPage(userModel));
    }

    @GetMapping(value = "/findDeptList")
    public Response findDeptList(HttpServletRequest request) {
//        AdminUserModel userModel = new AdminUserModel();
//        userModel.setDataId(0);
//        userModel.setDeptId(0);
//        userModel.setRoleType(1);
//        userModel.setId(0);
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        return Response.success(adminDeptService.findDeptList(userModel));
    }


    @PostMapping(value = "/addDept")
    @ResponseBody
    public Response addDept(String name, Integer parentId, HttpServletRequest request) {
        Assert.notNull(name, "部门名称不能为空");

        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        if (userModel.getIsManager() == CommonConst.NO) {

            throw new BusinessException(AdminRespCode.ACCESS_PINGTAI, "该账号没有操作权限");
        }

        AdminDeptModel deptModel = new AdminDeptModel();

        deptModel.setName(name);
        deptModel.setCreateId(userModel.getId());

        parentId = parentId == null ? userModel.getDeptId() : parentId;
        deptModel.setParentId(parentId);

        if (deptModel.getAgencyId() == null) {
            deptModel.setAgencyId(userModel.getDataId());
        }

        AdminDeptModel parentModel = adminDeptService.findDeptById(deptModel.getParentId());
        if (parentModel.getDeptLevel() == 4) {
            throw new BusinessException(AdminRespCode.SERVER_ERROR, "暂不支持多级部门架构");
        }
        deptModel.setDeptLevel(parentModel.getDeptLevel() + 1);
        deptModel.setDeptType(parentModel.getDeptType());

        adminDeptDao.insert(deptModel);
        deptModel.setDeptCode(parentModel.getDeptCode() + deptModel.getId() + "#");
        adminDeptDao.updateById(deptModel);
        return Response.success();
    }

    @RequestMapping(value = "/updateDept")
    @ResponseBody
    public Response updateDept(AdminDeptModel model) {
        Assert.notNull(model.getId(), "id不能为空");
        Assert.notNull(model.getName(), "部门名称不能为空");
        adminDeptService.updateDept(model);
        return Response.success();
    }

    @RequestMapping(value = "/deleteDept")
    public Response deleteDept(Integer id) {
        Assert.notNull(id, "id不能为空");
        adminDeptService.deleteById(id);
        return Response.success();
    }

    @GetMapping(value = "/findDeptById")
    public Response findDeptById(Integer id) {
        Assert.notNull(id, "id不能为空");
        return Response.success(adminDeptService.findDeptById(id));
    }


    /**
     * 分页获得未授权给Dept的角色
     *
     * @return
     */
//    @RequestMapping(value = "/pagingQueryNotGrantRoles")
//    @ResponseBody
//    public Response pagingQueryNotGrantRoles(Integer page, Integer pageSize, Integer deptId,
//                                             HttpServletRequest request) {
//        AdminUserModel model = adminUserHelper.getAdminUser(request);
//        Page<AdminRoleModel> pageResult = adminRoleService.pagingQueryNotGrantRoles(page, pageSize, null, null,
//                model.getDataId(), deptId, model.getId());
//        return Response.success(pageResult);
//
//    }

    /**
     * 分页获得已授权给Dept的角色
     *
     * @return
     */
//    @RequestMapping(value = "/pagingQueryGrantRoleByDeptId")
//    @ResponseBody
//    public Response pagingQueryGrantRoleByDeptId(Integer page, Integer pageSize, Integer deptId,
//                                                 HttpServletRequest request) {
//        Integer agencyId = adminUserHelper.getAgencyId(request);
////        Integer agencyId = 0;
//        Page<AdminRoleModel> pageResult = adminRoleService.pagingQueryGrantRoles(page, pageSize, null, null,
//                agencyId, deptId);
//        return Response.success(pageResult);
//    }


    @GetMapping(value = "/findDeptRoleList")
    public Response findDeptRoleList(Integer deptId, HttpServletRequest request) {
        Assert.notNull(deptId, "部门id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        return Response.success(adminDeptService.findDeptRoleList(deptId, agencyId));
    }

    @GetMapping(value = "/findDeptListByParentId")
    public Response findDeptListByParentId(Integer parentId, HttpServletRequest request) {
        Assert.notNull(parentId, "部门id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        return Response.success(adminDeptService.findDeptListByParentId(parentId, agencyId));
    }
}
