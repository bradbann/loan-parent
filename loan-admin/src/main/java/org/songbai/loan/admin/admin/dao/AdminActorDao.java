package org.songbai.loan.admin.admin.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.admin.model.AdminActorModel;
import org.songbai.loan.admin.admin.model.AdminAuthorityModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;

import java.util.List;

/**
 * 参与者相关操作
 *
 * @author wangd
 */
public interface AdminActorDao {


    /**
     * 批量删除用户和部门关系 一次删除只能删除一个部门下的用户。该操作需要部门的id和用户的id同时存在才可以
     *
     * @param departmentId 部门Id
     * @param userIds      用户id集合
     */
    public void deleteUserRelation(@Param(value = "departmentId") Integer departmentId,
                                   @Param(value = "userIds") List<Integer> userIds);

    /**
     * 分页获得未与部门关联的用户列表
     *
     * @param limit
     * @param size
     * @return
     */
    public List<AdminUserModel> pagingqueryNotRelationUsers(@Param(value = "limit") Integer limit,
                                                            @Param(value = "size") Integer size, @Param(value = "dataId") Integer dataId);

    public Integer pagingqueryNotRelationUsers_count(@Param(value = "dataId") Integer dataId);

    // ***************************************************************************

    /**
     * 保存用户
     *
     * @param userModel
     */
    public void createAdminUser(AdminUserModel userModel);

    /**
     * 修改代理商账号的昵称
     *
     * @param userModel
     */
    public void updateAdminUserName(AdminUserModel userModel);

    /**
     * 修改除密码之外的用户信息
     *
     * @param userModel
     */
    public void updateAdminUserExceptPassword(AdminUserModel userModel);

    /**
     * 修改用户的登录密码
     *
     * @param id
     * @param password
     */
    public void updateAdminUserPassword(@Param(value = "id") Integer id, @Param(value = "password") String password);

    /**
     * 根据Id获得一个用户 包含用户的密码属性，使用时注意
     *
     * @param id
     * @return
     */
    public AdminUserModel getAdminUser(Integer id);

    /**
     * 删除多个用户（包含删除一个用户）
     *
     * @param id
     */
    public void deletAdminUser(@Param(value = "id") Integer id, @Param(value = "dataId") Integer dataId);

    /**
     * 多条件查询
     */
    public List<AdminActorModel> getUsersBy(@Param(value = "limit") Integer limit, @Param(value = "size") Integer size,
                                            @Param(value = "userAccount") String userAccount, @Param(value = "name") String name,
                                            @Param(value = "email") String email, @Param(value = "disable") Boolean disable,
                                            @Param(value = "phone") String phone, @Param(value = "category") String category,
                                            @Param(value = "dataId") Integer dataId, @Param("deptIds") List<Integer> deptIds);

    /**
     * 多条件查询结果的条数
     *
     * @param userAccount
     * @param name
     * @param email
     * @param disable
     * @param phone
     * @param category
     * @return
     */
    public Integer getUserBy_count(@Param(value = "userAccount") String userAccount, @Param(value = "name") String name,
                                   @Param(value = "email") String email, @Param(value = "disable") Boolean disable,
                                   @Param(value = "phone") String phone, @Param(value = "category") String category,
                                   @Param(value = "dataId") Integer dataId, @Param("deptIds") List<Integer> deptIds);

    /**
     * 根据登录名和密码获得用户 登录验证
     */
    public AdminUserModel getUserByUserAccountPassword(@Param(value = "userAccount") String userAccount,
                                                       @Param(value = "password") String password, @Param(value = "dataId") Integer dataId,
                                                       @Param(value = "agencyCode") String agencyCode);

    /**
     * 获得包含密码的User的全部信息
     */
    public AdminUserModel getUsersAll(Integer id);

    /**
     * 批量激活或禁用User
     *
     * @param disbale true ： 禁用； false ： 激活
     */
    public void disbaleOrActivationUser(@Param(value = "ids") List<Integer> ids,
                                        @Param(value = "disbale") Boolean disbale);

    /**
     * 保存部门和用户关系
     */
    public void addDepartmentActor(@Param(value = "departmentId") Integer departmentId,
                                   @Param(value = "actorId") Integer actorId);

    /**
     * 删除部门和用户关系
     */
    public void deleteDepartmentActor(@Param(value = "actorId") Integer actorId);

    /**
     * 分页获得部门下的用户
     */
    public List<AdminUserModel> pagingqueryByDepartmentId(@Param(value = "departmentId") Integer departmentId,
                                                          @Param(value = "limit") Integer limit, @Param(value = "size") Integer size);

    public Integer pagingqueryByDepartmentId_count(@Param(value = "departmentId") Integer departmentId);

    /**
     * 分页查询角色下的用户
     */
    public List<AdminUserModel> pageQueryActorByRoleId(@Param(value = "roleId") Integer roleId,
                                                       @Param(value = "dataId") Integer dataId, @Param(value = "limit") Integer limit,
                                                       @Param(value = "size") Integer size);

    /**
     * 分页查询角色下的用户信息
     */
    public Integer pageQueryActorByRoleId_count(@Param(value = "roleId") Integer roleId,
                                                @Param(value = "dataId") Integer dataId);

    /**
     * 分页查询角色下的用户
     */
    public List<AdminActorModel> pageQueryAdminActorByRoleId(@Param(value = "roleId") Integer roleId,
                                                             @Param(value = "dataId") Integer dataId, @Param(value = "limit") Integer limit,
                                                             @Param(value = "size") Integer size);

    /**
     * 分页查询角色下的用户信息
     */
    public Integer pageQueryAdminActorByRoleId_count(@Param(value = "roleId") Integer roleId,
                                                     @Param(value = "dataId") Integer dataId);

    /**
     * 移除角色下人员信息
     */
    public void deleteUserInRole(@Param(value = "roleId") Integer roleId, @Param(value = "actorId") Integer actorId,
                                 @Param(value = "dataId") Integer dataId);

    public Integer getChannelLevelByDomain(String domain);

    public void userClosePopsRemind(@Param(value = "id") Integer id);

    public void resetAgencyPassword(AdminUserModel userModel);

    public void createAuthorization(@Param(value = "actorId") Integer actorId, @Param(value = "authorityId") Integer authorityId, @Param(value = "dataId") Integer dataId);

    void updateUserDeptById(@Param("actorId") Integer actorId, @Param("deptId") Integer deptId,
                            @Param("isManager") Integer isManager);

    /**
     * 获取id为1的默认数据
     */
    AdminAuthorityModel queryDefaultAgencyAuthority();

    Integer getDeptManagerCountByDeptId(@Param("deptId") Integer deptId);

    AdminUserModel getDeptManagerByDeptId(@Param("deptId") Integer deptId);

    List<AdminUserModel> findUserListByDeptIds(@Param("deptIds") List<Integer> deptIds, @Param("dataId") Integer dataId);

    /**
     * 获取管理员用户
     */
    AdminUserModel getAdminUserByAgencyId(@Param("agencyId") Integer agencyId);
}
