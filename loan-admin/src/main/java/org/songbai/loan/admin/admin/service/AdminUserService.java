package org.songbai.loan.admin.admin.service;

import org.apache.ibatis.annotations.Param;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.loan.admin.admin.model.AdminActorModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

@Component
public interface AdminUserService {
    /**
     * 创建管理员用户
     */
    void createAdminUser(AdminUserModel adminUserModel, Integer departmentId);

    /**
     * 为参与者批量授权
     */
    void grantAuthoritysToActor(Integer actorId, List<Integer> authorityIdList, Integer dataId);

    /**
     * 分页获得部门下的用户
     */
    Page<AdminUserModel> pagingquerByDepartmentId(Integer departmentId, Integer pageIndex, Integer pageSize);

    /**
     * 将用户从部门中移除
     */
    void deleteUserRelation(Integer departmentId, String userIds);

    /**
     * 分页获得未与部门关联的用户
     */
    Page<AdminUserModel> pagingqueryNotRelationUsers(Integer page, Integer pageSize, Integer dataId);

    /**
     * 批量关联用户和部门
     *
     * @param departmentId
     * @param userIds
     */
    void createUserRelation(Integer departmentId, String userIds);

    /**
     * 修改User除密码之外的信息 登录名不能修改
     */
    void updateAdminUserExceptPassword(AdminUserModel adminUserModel);

    /**
     * 删除一个User
     */
    void deleteAdminUser(Integer id, Integer dataId);

    /**
     * 删除多个User
     */
    void deleteAdminUsers(List<Integer> ids, Integer dataId);

    /**
     * 根据id获得User
     */
    AdminUserModel getUser(Integer id);

    /**
     * 分页获得多条件查询User列表
     */
    Page<AdminActorModel> getUserList(Integer pageIndex, Integer size, String userAccount, String name,
                                      String email, Boolean disable, String phone, AdminUserModel userModel);

    /**
     * 修改User的密码
     */
    void changePassword(Integer id, String newPassword);

    /**
     * 禁用/激活用户
     * @param disbale 禁用或激活 true ： 禁用 false ： 激活
     */
    void disbaleOrActivationUser(List<Integer> ids, boolean disbale);

    /**
     * 根据用户名和密码获得一个用户 登录验证 关于登录密码： 用户的登录密码在页面时会通过MD5加密一次，在后台会拼接加密盐，再次加密，
     * 保存用户时也是采用此加密流程
     */
    AdminUserModel getUserByUserAccountPassword(String userAccount, String password, Integer dataId,
                                                String agencyCode);

    /**
     * 验证用户的账户是否已经存在
     */
    boolean hasUserAccount(String userAccount, Integer dataId);

    /**
     * 保存用户和部门信息
     */
    void addDepartmentActor(Integer departmentId, Integer actorId);

    /**
     * 分页获得部门下的用户
     */
    Page<AdminUserModel> pagingqueryByDepartmentId(Integer departmentId, Integer pageIndex, Integer pageSize);

    /**
     * 分页查询角色下用户
     */
    Page<AdminUserModel> pageQueryActorByRoleId(Integer roleId, Integer dataId, Integer pageIndex,
                                                Integer pageSize);

    /**
     * 移除角色下人员信息
     */
    void deleteUserInRole(Integer roleId, Integer actorId, Integer dataId);

    Integer getChannelLevelByDomain(String str);

    void userClosePopsRemind(@Param(value = "id") Integer id);

    /**
     * 查询 用户及下属
     */
    List<AdminUserModel> findUserDeptList(AdminUserModel userModel, Integer deptType);

    Integer getDeptManagerCountByDeptId(Integer deptId);

    List<AdminUserModel> findUserListByDeptIds(List<Integer> deptIds, Integer dataId);

    Ret getUserMession(HttpServletRequest request);

    boolean hasUserPhone(String phone, Integer dataId);

    AdminUserModel getAdminUserByAgencyId(Integer agencyId);
}
