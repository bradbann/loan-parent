package org.songbai.loan.admin.admin.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminMenuResourceModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 权限资源Service 提供菜单资源、页面元素资源的Service
 *
 * @author wangd
 */
@Component
public interface AdminMenuResouceService {

    /**
     * 保存菜单权限资源 根据菜单的父菜单ID判断添加菜单的层级和父子关系 如果父菜单ID为空默认认为是添加的一级菜单
     *
     * @param menuResourceModel
     */
    public void saveMenu(AdminMenuResourceModel menuResourceModel);

    /**
     * 根据菜单Id获得一个菜单
     *
     * @param id
     * @return
     */
    public AdminMenuResourceModel getMenu(Integer id);

    /**
     * 根据角色id和登录者所属的渠道获得该渠道下所有可用的叶子菜单
     *
     * @return
     */
    public List<AdminMenuResourceModel> getAllLeafMenu(Integer type);

    /**
     * 根据菜单Id获得菜单及其子菜单 如果含有子菜单<strong>递归</strong>附带子菜单节点
     *
     * @param id
     * @return
     */
    public AdminMenuResourceModel getMenuPedigree(Integer id, Integer actorId);

    /**
     * 根据参与者Id获得该参与者拥有权限的所有菜单
     *
     * @return
     */
    public List<AdminMenuResourceModel> getMenuPedigreeByActorId(AdminUserModel actor);

    /**
     * 菜单修改 可以修改菜单的父菜单，如果前端有菜单的拖拽功能此处可以不支持菜单父菜单ID的修改 根据父子ID删除一条记录后添加一条新的纪录
     *
     * @param menuResourceModel
     */
    public void updateMenu(AdminMenuResourceModel menuResourceModel);

    /**
     * 删除菜单 删除之前需要验证该菜单是否有子菜单，有子菜单的不能删除 删除是需要删除菜单关系数据，菜单和角色是否有关联
     *
     * @param menuId
     */
    public String removeMenu(Integer menuId, Integer actorId);

    /**
     * 多条删除菜单和菜单关系纪录 菜单含有子菜单的不能删除 返回不能删除的菜单对象集合
     * <strong>全部删除时返回的集合是空的，调用方需要自己验证</strong>
     *
     * @param ids
     * @return
     */
    public List<AdminMenuResourceModel> removeMenus(List<Integer> ids);

    /**
     * 返回所有的菜单数据，并添加菜单的parentId和parentName属性值
     *
     * @return
     */
    public List<AdminMenuResourceModel> findAllMenusTree(Integer level, Integer actorId);

    public List<AdminMenuResourceModel> findTopMenuResources(Integer type);

    public List<AdminMenuResourceModel> findMenuResources(Integer level, Integer type);

    /**
     * 根据角色id查询菜单树， 已选择的有选择标识
     *
     * @param roleId
     * @return
     */
    public List<AdminMenuResourceModel> findMenuResourceTreeSelectItemByRoleId(Integer roleId, Integer dataId, Integer actorId);


    public Page<AdminMenuResourceModel> pagingqueryMenu(String menuName, Integer type, Integer pageIndex,
                                                        Integer pageSize);

    /**
     * 获得系统中所有的菜单以及与菜单相关联的页面元素和后台访问地址
     * <strong> 1：该方法获得的数据跟角色无关，所以在使用时需要注意是否要根据角色获得数据 2：该方法会将所有的菜单查询出来，
     * 3：该方法会同时查询与菜单相关联的页面元素和URL，
     * 4：该方法需要的角色id是为了处理该角色下已经授权的菜单，页面元素和URL，该角色id可以为空。 </strong>
     *
     * @return
     */
    public List<AdminMenuResourceModel> getAllMenuPageUrl(Integer roleId, Integer type, AdminUserModel actor);

    /**
     * 根据角色id 权限资源id 数据类型保存角色权限信息
     *
     * @param roleId
     * @param securityResourceIds
     * @param dataId
     */
    public void saveMenuPageUrlToRole(Integer roleId, List<Integer> securityResourceIds, Integer dataId);


    public void deleteAdminResourceAssignmentsByAuthorityId(Integer roleId);

    /**
     * 删除部门菜单资源
     *
     * @param deptId
     */
    void deleteAdminResourceAssignmentsByDeptId(Integer deptId);

    /**
     * 保存部门菜单权限
     *
     * @param deptId
     * @param securityResourceList
     * @param agencyId
     */
    void saveMenuPageUrlToDeptId(Integer deptId, List<Integer> securityResourceList, Integer agencyId);

    /**
     * 根据部门获取系统菜单
     *
     * @param userModel
     * @return
     */
    List<AdminMenuResourceModel> getMenuPedigressByDeptId(AdminUserModel userModel);

    /**
     * 所有菜单的树形结构,包含子菜单
     *
     * @param level
     * @param type
     * @return
     */
    List<AdminMenuResourceModel> findTreeMenus(Integer level, Integer type);

    /**
     * 获取顶级菜单
     */
    List<AdminMenuResourceModel> findMenuResourceByCategoryAndDeptId(String category, Integer deptId, Integer agencyId, int type);

}
