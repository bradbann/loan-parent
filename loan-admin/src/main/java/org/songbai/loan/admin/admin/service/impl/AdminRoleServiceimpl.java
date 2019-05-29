package org.songbai.loan.admin.admin.service.impl;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.dao.AdminActorDao;
import org.songbai.loan.admin.admin.dao.AdminAuthorityDao;
import org.songbai.loan.admin.admin.dao.AdminAuthorizationDao;
import org.songbai.loan.admin.admin.dao.AdminResourceAssignmentDao;
import org.songbai.loan.admin.admin.model.AdminRoleModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminDeptService;
import org.songbai.loan.admin.admin.service.AdminRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SUNDONG_
 */
@Service
public class AdminRoleServiceimpl implements AdminRoleService {

    @Autowired
    AdminAuthorityDao adminAuthorityDao;

    @Autowired
    AdminActorDao adminActorDao;

    @Autowired
    AdminAuthorizationDao adminAuthorizationDao;

    @Autowired
    AdminResourceAssignmentDao adminResourceAssignmentDao;
    @Autowired
    AdminDeptService adminDeptService;

    @Override
    public void createAdminRole(AdminRoleModel adminRoleModel) {
        adminAuthorityDao.createAdminRole(adminRoleModel);
    }

    @Override
    public void updateAdminRole(AdminRoleModel adminRoleModel) {
        adminAuthorityDao.updateAdminRole(adminRoleModel);
    }

    @Override
    public void deleteAdminRole(Integer roleId) {
        adminAuthorityDao.deleteAdminRole(roleId);
    }

    @Override
    public Page<AdminRoleModel> pagingQueryPermissions(String name, String description, String category, Integer dataId,
                                                       Integer pageIndex, Integer pageSize) {

        Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0;

        List<AdminRoleModel> data = adminAuthorityDao.pagingQueryRole(name, description, category, dataId, limit,
                pageSize);
        int resultCount = adminAuthorityDao.pagingQueryRole_count(name, description, category, dataId);

        Page<AdminRoleModel> page = new Page<>(pageIndex, pageSize, resultCount);
        page.setData(data);
        return page;
    }

    @Override
    public List<AdminRoleModel> deleteRoles(List<Integer> ids) {
        List<AdminRoleModel> result = new ArrayList<AdminRoleModel>();
        for (Integer id : ids) {
            // 验证该角色是否是 系统角色 否则该角色不能被删除
            AdminRoleModel roleModel = adminAuthorityDao.getRole(id);
            if (roleModel.getIsAdmin() == 0) {
                // 验证带角色下是否已经有用户了，有的就不能删除，需要先清空该角色下面的用户，然后再删除角色
                adminAuthorizationDao.deleteAuthorizationByActorIdAuthorityIds(null, ids, null);
                // 删除角色权限、角色页面元素资源、角色URL、角色菜单等配置数据
                adminResourceAssignmentDao.deleteAdminResourceAssignments(id, null, null);
                adminAuthorityDao.deleteAdminRole(id);
            }

        }
        return result;
    }

    @Override
    public Page<AdminRoleModel> pagingQueryGrantRoles(Integer page, Integer pageSize, Integer actorId, String name,
                                                      AdminUserModel userModel) {
        Integer limit = page > 0 ? page * pageSize : 0;
        List<Integer> deptIds = adminDeptService.findDeptIdsByType(userModel, null);
        List<AdminRoleModel> resultList = adminAuthorityDao.pagingQueryGrantRolesByUserId(limit, pageSize, actorId,
                AdminRoleModel.CATEGORY, name, userModel.getDataId(), deptIds);
        Integer count = adminAuthorityDao.pagingQueryGrantRolesByUserId_count(actorId, AdminRoleModel.CATEGORY, name,
                userModel.getDataId(), deptIds);

        Page<AdminRoleModel> resultPage = new Page<AdminRoleModel>(page, pageSize, count);
        resultPage.setData(resultList);
        return resultPage;
    }

    @Override
    public Page<AdminRoleModel> pagingQueryNotGrantRoles(Integer page, Integer pageSize, Integer actorId, String name,
                                                         AdminUserModel userModel) {
        Integer limit = page > 0 ? page * pageSize : 0;
        List<Integer> deptIds = adminDeptService.findDeptIdsByType(userModel, null);

        List<AdminRoleModel> resultList = adminAuthorityDao.pagingQueryNotGrantRolesByUserId(limit, pageSize, actorId,
                AdminRoleModel.CATEGORY, name, userModel.getDataId(), deptIds);
        Integer count = adminAuthorityDao.pagingQueryNotGrantRolesByUserId_count(actorId, AdminRoleModel.CATEGORY, name,
                userModel.getDataId(), deptIds);

        Page<AdminRoleModel> resultPage = new Page<AdminRoleModel>(page, pageSize, count);
        resultPage.setData(resultList);
        return resultPage;
    }

    @Override
    public void terminateAuthorizationByUserIdAuthorithIds(Integer userId, List<Integer> authorityIds, Integer dataId) {
        adminAuthorizationDao.deleteAuthorizationByActorIdAuthorityIds(userId, authorityIds, dataId);

    }

    public void customerOffLine(Integer userId) {

    }

    @Override
    public List<AdminRoleModel> queryGrantRoles(Integer actorId, Integer dataId) {

        return adminAuthorityDao.queryGrantRolesByUserId(actorId, AdminRoleModel.CATEGORY);
    }

    @Override
    public Page<AdminRoleModel> pagingQueryChannelRoles(AdminUserModel userModel, Integer page, Integer pageSize) {
        Integer limit = page > 0 ? page * pageSize : 0;
//        List<Integer> deptIds = adminDeptService.findUserDeptIds(userModel);
        //角色查询不根据部门id查询
        List<Integer> deptIds = null;
        Integer resultCount = adminAuthorityDao.pagingQueryRolesByIsAdmin_count(userModel, deptIds);
        if (resultCount == 0) {
            return new Page<>(page, pageSize, resultCount, new ArrayList<>());
        }
        List<AdminRoleModel> resultLists = adminAuthorityDao.pagingQueryRolesByIsAdmin(userModel, deptIds, limit, pageSize);

        Page<AdminRoleModel> resultPage = new Page<AdminRoleModel>(page, pageSize, resultCount);
        resultPage.setData(resultLists);
        return resultPage;

    }

    @Override
    public List<AdminRoleModel> findRoles(Integer dataId) {
        return adminAuthorityDao.findRoles(dataId);
    }


    @Override
    public void grantResourcesToDept(Integer deptId, Integer agencyId) {

    }

}
