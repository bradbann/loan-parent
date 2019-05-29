package org.songbai.loan.admin.admin.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.admin.model.AdminPerssionModel;
import org.songbai.loan.admin.admin.model.AdminRoleModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;

import java.util.List;
import java.util.Map;

/**
 * 授权相关Dao 包含权限、角色、授权Dao
 *
 * @author wangd
 */
public interface AdminAuthorityDao {
    /**
     * 批量删除数据 可以删除角色和权限
     */
    void deleteAuthoritysByIds(@Param(value = "ids") List<Integer> ids);

    /**
     * ****************************************************权限部分DAO接口************
     * *********
     */
    /**
     * 分页多条件查询权限结果
     */
    List<AdminPerssionModel> pagingQueryPermissions(Map<String, Object> queryPermissionCondition);

    /**
     * 分页多条件查询权限结果条数
     */
    int pagingQueryPermissions_count(Map<String, Object> queryPermissionCondition);

    /**
     * 权限保存
     */
    void createPermission(AdminPerssionModel perssionModel);

    /**
     * 动态修改权限信息
     */
    void updatePermission(AdminPerssionModel perssionModel);

    /**
     * 分页获得用户授权的角色
     */
    List<AdminPerssionModel> pagingQueryGrantPsemissionsByActorId(@Param(value = "limit") Integer limit,
                                                                  @Param(value = "size") Integer size, @Param(value = "actorId") Integer actorId,
                                                                  @Param(value = "category") String category);

    Integer pagingQueryGrantPsemissionsByActorId_count(@Param(value = "actorId") Integer actorId,
                                                       @Param(value = "category") String category);

    /**
     * 根据参与者Id分页查询未授权给参与者的权限
     */
    List<AdminPerssionModel> pagingQueryNotGrantPsemissionByActorId(@Param(value = "limit") Integer limit,
                                                                    @Param(value = "size") Integer pagesize, @Param(value = "actorId") Integer actorId,
                                                                    @Param(value = "category") String category);

    Integer pagingQueryNotGrantPsemissionByActorId_count(@Param(value = "actorId") Integer actorId,
                                                         @Param(value = "category") String category);

    /**
     * **********************角色相关的数据库操作开始************************************
     */

    void createAdminRole(AdminRoleModel roleModel);

    void updateAdminRole(AdminRoleModel roleModel);

    void deleteAdminRole(Integer id);

    AdminRoleModel getRole(Integer id);

    /**
     * 多条件查询角色
     */
    List<AdminRoleModel> pagingQueryRole(@Param(value = "name") String name,
                                         @Param(value = "description") String description, @Param(value = "category") String category,
                                         @Param(value = "dataId") Integer dataId, @Param(value = "limit") Integer limit,
                                         @Param(value = "size") Integer size);

    Integer pagingQueryRole_count(@Param(value = "name") String name,
                                  @Param(value = "description") String description, @Param(value = "category") String category,
                                  @Param(value = "dataId") Integer dataId);

    /**
     * 分页获得用户授权的角色
     */
    List<AdminRoleModel> pagingQueryGrantRolesByUserId(@Param(value = "limit") Integer limit,
                                                       @Param(value = "size") Integer size, @Param(value = "actorId") Integer actorId,
                                                       @Param(value = "category") String category, @Param(value = "name") String name,
                                                       @Param(value = "dataId") Integer dataId, @Param("deptIds") List<Integer> deptIds);

    Integer pagingQueryGrantRolesByUserId_count(@Param(value = "actorId") Integer actorId,
                                                @Param(value = "category") String category, @Param(value = "name") String name,
                                                @Param(value = "dataId") Integer dataId, @Param("deptIds") List<Integer> deptIds);

    /**
     * 分页获得未授权给用户的角色
     */
    List<AdminRoleModel> pagingQueryNotGrantRolesByUserId(@Param(value = "limit") Integer limit,
                                                          @Param(value = "size") Integer size,
                                                          @Param(value = "actorId") Integer actorId,
                                                          @Param(value = "category") String category,
                                                          @Param(value = "name") String name,
                                                          @Param(value = "dataId") Integer dataId,
                                                          @Param("deptIds") List<Integer> deptIds);

    Integer pagingQueryNotGrantRolesByUserId_count(@Param(value = "actorId") Integer actorId,
                                                   @Param(value = "category") String category,
                                                   @Param(value = "name") String name,
                                                   @Param(value = "dataId") Integer dataId,
                                                   @Param("deptIds") List<Integer> deptIds);

    /**
     * 获得所有授权给参与者的角色
     */
    List<AdminRoleModel> queryGrantRolesByUserId(@Param(value = "actorId") Integer actorId,
                                                 @Param(value = "category") String category);


    Integer pagingQueryRolesByIsAdmin_count(@Param("userModel") AdminUserModel userModel, @Param("deptIds") List<Integer> deptIds);


    List<AdminRoleModel> findRoles(@Param(value = "dataId") Integer dataId);

    List<AdminRoleModel> queryGrantRolesByDeptId(@Param("deptId") Integer deptId);

    /**
     * 查询自己权限下的角色管理
     */
    List<AdminRoleModel> pagingQueryRolesByIsAdmin(@Param("userModel") AdminUserModel userModel,
                                                   @Param("deptIds") List<Integer> deptIds,
                                                   @Param("limit") Integer limit,
                                                   @Param("pageSize") Integer pageSize);
}
