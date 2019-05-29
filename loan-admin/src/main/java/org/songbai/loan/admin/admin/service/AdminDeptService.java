package org.songbai.loan.admin.admin.service;

import org.songbai.loan.admin.admin.model.AdminDeptModel;
import org.songbai.loan.admin.admin.model.AdminMenuResourceModel;
import org.songbai.loan.admin.admin.model.AdminRoleModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.model.vo.AdminDeptVO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface AdminDeptService {

    /**
     * 删除部门
     */
    void deleteById(Integer id);

    /**
     * 部门管理-查询
     */
    AdminDeptModel findDeptPage(AdminUserModel userModel);


    /**
     * 部门下拉列表
     */
    List<AdminDeptModel> findDeptList(AdminUserModel userModel);

    void updateDept(AdminDeptModel model);

    AdminDeptModel findDeptById(Integer id);

    /**
     * 根据部门获取部门列表--走权限
     */
    List<AdminDeptModel> findDeptListByType(AdminUserModel userModel, Integer deptType);

    /**
     * 获取所有的部门列表，不走权限
     */
    List<AdminDeptModel> findAllDeptListByType(Integer agencyId, Integer deptType);

    /**
     * 获取部门id--走权限
     */
    List<Integer> findDeptIdsByType(AdminUserModel userModel, Integer deptType);

    /**
     * 获取部门id--不走权限
     */
    List<Integer> findAllDeptIdsByType(Integer agencyId, Integer deptType);

    /**
     * 部门赋权
     */
    void saveResourceToDeptId(Integer deptId, List<Integer> asList, Integer agencyId);

    /**
     * 删除部门权限
     */
    void deleteResourceByDeptId(Integer deptId);

    List<AdminMenuResourceModel> getAllMenuPageUrl(Integer deptId, AdminUserModel userModel);

    List<AdminMenuResourceModel> findResourceByParentId(Integer parentId, Integer deptId, AdminUserModel userModel);

    AdminDeptModel getDeptByParentId(Integer parentId);

    List<AdminRoleModel> findDeptRoleList(Integer deptId, Integer agencyId);

    List<AdminDeptVO> findDeptListByParentId(Integer parentId, Integer agencyId);
}
