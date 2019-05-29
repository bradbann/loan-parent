package org.songbai.loan.admin.admin.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminRoleModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author SUNDONG_
 */
@Component
public interface AdminRoleService {
    void createAdminRole(AdminRoleModel adminRoleModel);

    void updateAdminRole(AdminRoleModel adminRoleModel);

    void deleteAdminRole(Integer roleId);

    Page<AdminRoleModel> pagingQueryPermissions(String name, String description, String category, Integer dataId,
                                                Integer page, Integer pageSize);

    /**
     * 删除多条角色数据和角色相关的配置数据，如角色权限关系数据、角色页面元素数据。
     * 返回不能删除的角色数据（角色和用户关联时，不能删除该角色数据，应先清空该角色用户关系数据后再删除该角色。这样防止误删除的情况）
     */
    List<AdminRoleModel> deleteRoles(List<Integer> ids);

    /**
     * 根据参与者Id分页查询授权给参与者的角色
     */
    Page<AdminRoleModel> pagingQueryGrantRoles(Integer page, Integer pageSize, Integer actorId, String name,
                                               AdminUserModel userModel);

    /**
     * 根据参与者Id分页查询未授权给参与者的角色
     */
    Page<AdminRoleModel> pagingQueryNotGrantRoles(Integer page, Integer pageSize, Integer actorId, String name,
                                                  AdminUserModel userModel);

    /**
     * 根据参与者Id和授权Id批量撤销权限， <strong>该接口同样适用于权限的撤销，</strong>
     */
    void terminateAuthorizationByUserIdAuthorithIds(Integer userId, List<Integer> authorityIds, Integer dataId);

    /**
     * 获得参与者拥有的角色
     *
     * @param actorId
     * @return
     */
    List<AdminRoleModel> queryGrantRoles(Integer actorId, Integer dataId);


    /**
     * 查询平台渠道下的角色列表
     */
    Page<AdminRoleModel> pagingQueryChannelRoles(AdminUserModel userModel, Integer page, Integer pageSize);


    List<AdminRoleModel> findRoles(Integer dataId);


    /**
     * 给部门赋权
     */
    void grantResourcesToDept(Integer deptId, Integer agencyId);
}
