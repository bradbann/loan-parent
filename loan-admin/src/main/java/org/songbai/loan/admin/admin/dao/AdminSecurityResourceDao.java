package org.songbai.loan.admin.admin.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.admin.model.AdminMenuResourceModel;
import org.songbai.loan.admin.admin.model.AdminPageElementResourceModel;
import org.songbai.loan.admin.admin.model.AdminSecurityResourceModel;
import org.songbai.loan.admin.admin.model.AdminUrlAccessResourceModel;

import java.util.List;
import java.util.Map;

/**
 * 权限资源DAO 包括菜单资源、页面元素资源
 *
 * @author wangd
 */
public interface AdminSecurityResourceDao {

    /**
     * 通过id，通用删除
     *
     * @param id
     */
    public void delete(Integer id);

    /**
     * 通用查询，根据菜单id查询菜单所属的页面元素和后台方法，
     *
     * @param category   资源类型，可以为空，不为空则值查询相关类型的
     * @return
     */
    public List<AdminSecurityResourceModel> getAllByMenuId(@Param(value = "menuId") Integer menuId,
                                                           @Param(value = "category") String category,
                                                           @Param(value = "type") Integer type);

    public List<AdminSecurityResourceModel> getAllByMenuIdByActorId(@Param(value = "menuId") Integer menuId,
                                                                    @Param(value = "category") String category,
                                                                    @Param(value = "type") Integer type, @Param("actorId") Integer actorId);

    /**
     * 通过改变其状态来进行软删除
     *
     */
    public void updateStatus(@Param(value = "id") Integer id);

    /**
     * 菜单资源的保存
     *
     */

    public void createMenu(AdminMenuResourceModel menuResourceModel);

    public void updateMenu(AdminMenuResourceModel menuResourceModel);

    /**
     * 根据菜单id获得菜单
     *
     * @param id
     * @return
     */
    public AdminMenuResourceModel getMenu(@Param("id") Integer id, @Param("status") Integer status,
                                          @Param("category") String category);

    /**
     * 根据父菜单Id获得子菜单集合
     *
     * @param parentId
     * @return
     */
    public List<AdminMenuResourceModel> getChildMenu(@Param("parentId") Integer parentId,
                                                     @Param("status") Integer status, @Param("category") String category);

    public List<AdminMenuResourceModel> getChildMenuByActorId(@Param("parentId") Integer parentId,
                                                              @Param("status") Integer status, @Param("category") String category, @Param("actorId") Integer actorId);

    /**
     * 根据角色id和登录者所属的渠道获得该渠道下所有可用的叶子菜单
     *
     * @return
     */
    public List<AdminMenuResourceModel> getAllLeafMenu(@Param("type") Integer type, @Param("category") String category);


    /**
     * 多条件查询菜单
     *
     * @return
     */
    public List<AdminMenuResourceModel> findMenuResources(AdminMenuResourceModel menuResourceModel);

    /**
     * 根据角色Id获得分配给该角色的菜单资源数据
     *
     * @param roleId
     * @param category
     * @param level    菜单层级
     * @return
     */
    public List<AdminMenuResourceModel> findMenuResourcesByRoleId(@Param("roleId") Integer roleId,
                                                                  @Param("category") String category, @Param(value = "level") Integer level,
                                                                  @Param(value = "status") Integer status);

    /**
     * 根据菜单id和角色id获得所有授权给该角色的该菜单的子菜单集合 适用于根据父菜单和角色数据获得已授权的子菜单集合
     *
     * @param parentId
     * @param roleId
     * @return
     */
    public List<AdminMenuResourceModel> queryMenuResourcesByParentIdRoleId(@Param(value = "parentId") Integer parentId,
                                                                           @Param(value = "roleId") Integer roleId, @Param(value = "status") Integer status,
                                                                           @Param(value = "category") String category);

    /**
     * 分页查询菜单数据
     *
     * @param menuName
     * @param limit
     * @param size
     * @return
     */
    public List<AdminMenuResourceModel> pagingqueryMenu(@Param(value = "name") String menuName,
                                                        @Param(value = "category") String category, @Param(value = "type") Integer type,
                                                        @Param(value = "status") Integer status, @Param(value = "limit") Integer limit,
                                                        @Param(value = "size") Integer size);

    public Integer pagingqueryMenu_count(@Param(value = "name") String menuName,
                                         @Param(value = "category") String category, @Param(value = "type") Integer type,
                                         @Param(value = "status") Integer status);

    /**
     * **********************************菜单部分结束*************************
     * **********************************URL资源权限开始**********************
     */

    public void createUrlAccess(AdminUrlAccessResourceModel urlAccessResourceModel);

    public void updateUrlAccess(AdminUrlAccessResourceModel urlAccessResourceModel);

    public List<AdminUrlAccessResourceModel> pagingQueryUrlAccess(Map<String, Object> param);

    public Integer pagingQueryUrlAccess_count(Map<String, Object> param);

    /**
     * @param category
     * @param roleId
     * @param dataId
     * @return
     */
    public List<AdminUrlAccessResourceModel> getAllUrlAccessByRoleId(@Param(value = "category") String category,
                                                                     @Param(value = "roleId") Integer roleId, @Param(value = "dataId") Integer dataId);

    /**
     * 根据参与者Id获得分配给参与者的URL权限
     *
     * @param actorId
     * @param category
     * @return
     */
    public List<AdminUrlAccessResourceModel> getUrlAccessResourcesByActorId(@Param(value = "actorId") Integer actorId,
                                                                            @Param(value = "category") String category, @Param(value = "dataId") Integer dataId);

    public List<AdminSecurityResourceModel> getSecurityResourcesByActorId(@Param(value = "actorId") Integer actorId,
                                                                          @Param(value = "dataId") Integer dataId, @Param(value = "category") String category);

    /**
     * **********************************URL资源权限结束*************************
     * **********************************页面元素资源权限开始**********************
     */
    public void createPageElement(AdminPageElementResourceModel elementResourceModel);

    public void updatePageElement(AdminPageElementResourceModel elementResourceModel);

    /**
     * 多条件查询页面元素资源
     *
     * @param param
     * @return
     */
    public List<AdminPageElementResourceModel> pagingQueryPageElement(Map<String, Object> param);

    public Integer pagingQueryPageElement_count(Map<String, Object> param);

    /**
     * 根据角色Id分页获得已授权的页面资源 多条件查询，至少角色Id是必须的 如果没有角色Id将会出现异常
     *
     * @param name
     * @param description
     * @param identifier
     * @param roleId
     * @param dataId
     * @param limit
     * @param size
     * @return
     */
    public List<AdminPageElementResourceModel> pagingQueryGrantPageElementByRoleId(@Param(value = "name") String name,
                                                                                   @Param(value = "description") String description, @Param(value = "identifier") String identifier,
                                                                                   @Param(value = "roleId") Integer roleId, @Param(value = "dataId") Integer dataId,
                                                                                   @Param(value = "limit") Integer limit, @Param(value = "size") Integer size);

    public Integer pagingQueryGrantPageElementByRoleId_count(@Param(value = "name") String name,
                                                             @Param(value = "description") String description, @Param(value = "identifier") String identifier,
                                                             @Param(value = "roleId") Integer roleId, @Param(value = "dataId") Integer dataId);

    /**
     * 根据角色Id分页获得未授权的页面资源 多条件查询
     *
     * @param name
     * @param description
     * @param identifier
     * @param roleId
     * @param dataId
     * @param limit
     * @param size
     * @return
     */
    public List<AdminPageElementResourceModel> pagingQueryNotGrantPageElementByRoleId(
            @Param(value = "name") String name, @Param(value = "description") String description,
            @Param(value = "identifier") String identifier, @Param(value = "roleId") Integer roleId,
            @Param(value = "dataId") Integer dataId, @Param(value = "limit") Integer limit,
            @Param(value = "size") Integer size);

    public Integer pagingQueryNotGrantPageElementByRoleId_count(@Param(value = "name") String name,
                                                                @Param(value = "description") String description, @Param(value = "identifier") String identifier,
                                                                @Param(value = "roleId") Integer roleId, @Param(value = "dataId") Integer dataId);

    /**
     * @param actorId
     * @return
     */
    public AdminPageElementResourceModel getPagelementByActorIdIdDdentifier(@Param(value = "actorId") Integer actorId,
                                                                            @Param(value = "identifier") String identifier, @Param(value = "dataId") Integer dataId);

    public AdminPageElementResourceModel getPagelementByTypeDdentifier(@Param(value = "identifier") String identifier,
                                                                       @Param(value = "dataId") Integer dataId);

    /**
     * 根据登录用户来获取 用户所用有的pageElement
     *
     * @param actorId
     * @param category
     * @return
     */

    public List<String> getPageElementByActorId(@Param(value = "actorId") Integer actorId,
                                                @Param(value = "category") String category);

    public List<String> getPageElementAll(@Param(value = "category") String category);

    public List<AdminSecurityResourceModel> getAllByMenuIdByCategory(@Param("category") String category, @Param("id") Integer id);


    public List<AdminSecurityResourceModel> getAllByMenuIdByCategoryForSuperMan(@Param("category") String category);

    public List<AdminMenuResourceModel> getAdminMenuResourceByActorId(Integer actorId);


    public Integer isHaveRoleForActor(@Param("actorId") Integer actorId, @Param("authorityIds") List<Integer> authorityIds);

    Integer findCountByCode(@Param("code") String code, @Param("id") Integer id);

    List<AdminSecurityResourceModel> getAllByMenuIdByCategoryAndDeptId(@Param("category") String category, @Param("deptId") Integer deptId,
                                                                       @Param("agencyId") Integer agencyId, @Param("type") int type,
                                                                       @Param("parentId") Integer parentId);

    List<AdminMenuResourceModel> findMenuResourcesByDeptId(@Param("deptId") Integer deptId, @Param("category") String category,
                                                           @Param("level") Integer level, @Param("status") Integer status,
                                                           @Param("agencyId") Integer agencyId, @Param("type") int type);

    List<AdminMenuResourceModel> findMenuResourceByAgencyId(@Param("dataId") Integer dataId, @Param("level") Integer level,
                                                            @Param("category") String category);

    List<AdminMenuResourceModel> queryMenuResourcesByParentIdActor(@Param("parentId") Integer parentId, @Param("deptId") Integer deptId,
                                                                   @Param("agencyId") Integer agencyId, @Param("type") Integer type,
                                                                   @Param("status") Integer status, @Param("category") String category);

    List<String> getPageElementByDeptId(@Param("agencyId") Integer agencyId, @Param("deptId") Integer deptId,
                                        @Param("category") String category, @Param("type") int type);
}
