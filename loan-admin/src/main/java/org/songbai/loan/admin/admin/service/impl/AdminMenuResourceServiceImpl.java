package org.songbai.loan.admin.admin.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.dao.AdminActorDao;
import org.songbai.loan.admin.admin.dao.AdminAuthorityDao;
import org.songbai.loan.admin.admin.dao.AdminResourceAssignmentDao;
import org.songbai.loan.admin.admin.dao.AdminSecurityResourceDao;
import org.songbai.loan.admin.admin.model.*;
import org.songbai.loan.admin.admin.service.AdminDeptService;
import org.songbai.loan.admin.admin.service.AdminMenuResouceService;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminMenuResourceServiceImpl implements AdminMenuResouceService {
    @Autowired
    AdminSecurityResourceDao adminSecurityResourceDao;

    @Autowired
    AdminResourceAssignmentDao adminResourceAssignmentDao;

    @Autowired
    AdminAuthorityDao adminAuthorityDao;

    @Autowired
    AdminActorDao adminActorDao;
    @Autowired
    AdminDeptService adminDeptService;

    public static final Integer MENU_STATUS_NOTDELETE = 1;

    @Override
    public void saveMenu(AdminMenuResourceModel menuResourceModel) {
        //判断当前code是否已被使用
        Integer count = adminSecurityResourceDao.findCountByCode(menuResourceModel.getCode(), null);
        if (count > 0) {
            throw new BusinessException(AdminRespCode.MENU_CODE_EXISIT);
        }
        // 修改上级菜单的是否是叶子菜单为false
        if (menuResourceModel.getParentId() != null) {
            AdminMenuResourceModel parentNode = adminSecurityResourceDao.getMenu(menuResourceModel.getParentId(),
                    MENU_STATUS_NOTDELETE, AdminMenuResourceModel.CATEGORY);
            menuResourceModel.setLevel(parentNode.getLevel() + 1);

            parentNode.setIsLeaf(false);
            adminSecurityResourceDao.updateMenu(parentNode);
        }
        menuResourceModel.setCategory(AdminMenuResourceModel.CATEGORY);
        adminSecurityResourceDao.createMenu(menuResourceModel);
    }

    @Override
    public AdminMenuResourceModel getMenuPedigree(Integer id, Integer actorId) {
        id = id == null ? 0 : id;
        AdminMenuResourceModel menuResourceModel = adminSecurityResourceDao.getMenu(id, MENU_STATUS_NOTDELETE,
                AdminMenuResourceModel.CATEGORY);
        menuResourceModel.setChildren(this.getChildrenIsType(menuResourceModel.getId(), actorId));
        return menuResourceModel;
    }

    /**
     * 递归查找菜单下所有的子菜单
     *
     * @return
     */
//	private List<AdminMenuResourceModel> getChildren(Integer parentId) {
//		List<AdminMenuResourceModel> result = adminSecurityResourceDao.getChildMenu(parentId, MENU_STATUS_NOTDELETE,
//				AdminMenuResourceModel.CATEGORY);
//		for (AdminMenuResourceModel menuResourceModel : result) {
//			if (this.getChildren(menuResourceModel.getId()) != null
//					&& this.getChildren(menuResourceModel.getId()).size() > 0) {
//				menuResourceModel.setChildren(this.getChildren(menuResourceModel.getId()));
//			}
//		}
//		return result;
//	}
    @Override
    public void updateMenu(AdminMenuResourceModel menuResourceModel) {
        Integer count = adminSecurityResourceDao.findCountByCode(menuResourceModel.getCode(), menuResourceModel.getId());
        if (count > 0) {
            throw new BusinessException(AdminRespCode.MENU_CODE_EXISIT);
        }
        adminSecurityResourceDao.updateMenu(menuResourceModel);
    }

    @SuppressWarnings("unused")
    @Override
    public String removeMenu(Integer menuId, Integer actorId) {

        AdminMenuResourceModel menuResourceModel = this.getMenuPedigree(menuId, actorId);
        if (menuResourceModel == null || menuResourceModel != null) {
            return "删除失败，该菜单包含有子菜单";
        }
        adminSecurityResourceDao.delete(menuId);
        return "菜单删除成功";
    }

    /**
     * 获得所有的菜单数据
     */
    @Override
    public List<AdminMenuResourceModel> findAllMenusTree(Integer level, Integer actorId) {
        List<AdminMenuResourceModel> result = new ArrayList<AdminMenuResourceModel>();
        List<AdminMenuResourceModel> topMenus = null;
        if (actorId.equals(0)) {
            topMenus = this.findTopMenuResources(level);
        } else {
            topMenus = adminSecurityResourceDao.getAdminMenuResourceByActorId(actorId);
        }
        result.addAll(topMenus);
        for (AdminMenuResourceModel menuResourceModel : topMenus) {
            List<AdminMenuResourceModel> childNode = this.getChildrenIsType(menuResourceModel.getId(), actorId);
            if (childNode != null && childNode.size() > 0) {
                this.setParent(menuResourceModel.getId(), menuResourceModel.getName(), childNode);
            }
            result.addAll(childNode);
        }
        return result;
    }

    /**
     * 递归修改子菜单的父菜单信息，包括父菜单的Id和name
     *
     * @param parentId
     * @param childNode
     */
    private void setParent(Integer parentId, String name, List<AdminMenuResourceModel> childNode) {
        for (AdminMenuResourceModel model : childNode) {
            model.setParentId(parentId);
            model.setParentName(name);
            if (model.getChildren() != null && model.getChildren().size() > 0) {
                this.setParent(model.getId(), model.getName(), model.getChildren());
            }
        }
    }

    /**
     * 获得顶级菜单 顶级菜单的层级为0 菜单维护时一定要注意菜单的层级
     *
     * @return
     */
    public List<AdminMenuResourceModel> findTopMenuResources(Integer type) {
        AdminMenuResourceModel menuResourceModel = new AdminMenuResourceModel();
        menuResourceModel.setCategory(AdminMenuResourceModel.CATEGORY);
        menuResourceModel.setLevel(0);
        menuResourceModel.setType(type);
        return adminSecurityResourceDao.findMenuResources(menuResourceModel);
    }

    public List<AdminMenuResourceModel> findMenuResources(Integer level, Integer type) {
        AdminMenuResourceModel menuResourceModel = new AdminMenuResourceModel();
        menuResourceModel.setCategory(AdminMenuResourceModel.CATEGORY);
        menuResourceModel.setLevel(level);
        menuResourceModel.setType(type);
        return adminSecurityResourceDao.findMenuResources(menuResourceModel);
    }

    @Override
    public List<AdminMenuResourceModel> removeMenus(List<Integer> ids) {
        List<AdminMenuResourceModel> result = new ArrayList<AdminMenuResourceModel>();
        for (Integer id : ids) {
            // 判断是否还有子菜单
            if (!hasChildren(id)) {
                adminSecurityResourceDao.updateStatus(id);
                // 删除菜单 删除与菜单相关联的权限记录
                adminResourceAssignmentDao.deleteAdminResourceAssignment(null, id, null);
            } else if (hasChildren(id)) {
                adminSecurityResourceDao.updateStatus(id);
                // 获取该菜单的子菜单
                List<AdminMenuResourceModel> menuResourceModels = adminSecurityResourceDao.getChildMenu(id,
                        MENU_STATUS_NOTDELETE, AdminMenuResourceModel.CATEGORY);
                for (AdminMenuResourceModel menuResourceModel : menuResourceModels) {
                    adminSecurityResourceDao.updateStatus(menuResourceModel.getId());
                    adminResourceAssignmentDao.deleteAdminResourceAssignment(null, menuResourceModel.getId(), null);
                }
            } else {
                result.add(this.getMenu(id));
            }
        }
        return result;
    }

    @Override
    public AdminMenuResourceModel getMenu(Integer id) {
        return adminSecurityResourceDao.getMenu(id, MENU_STATUS_NOTDELETE, AdminMenuResourceModel.CATEGORY);
    }

    /**
     * 根据菜单Id判断菜单是否含有子菜单
     *
     * @param id
     * @return 如果含有子菜单返回true否则返回false
     */
    private boolean hasChildren(Integer id) {
        return adminSecurityResourceDao.getChildMenu(id, MENU_STATUS_NOTDELETE, AdminMenuResourceModel.CATEGORY) != null
                && adminSecurityResourceDao
                .getChildMenu(id, MENU_STATUS_NOTDELETE, AdminMenuResourceModel.CATEGORY).size() > 0;
    }

    @Override
    public List<AdminMenuResourceModel> findMenuResourceTreeSelectItemByRoleId(Integer roleId, Integer dataId, Integer actorId) {
        List<AdminMenuResourceModel> topMenuResources = null;
        if (actorId.equals(0)) {
            topMenuResources = this.findTopMenuResources(0);
        } else {
            topMenuResources = adminSecurityResourceDao.getAdminMenuResourceByActorId(actorId);
        }

        this.findTopMenuResources(null);
        List<AdminMenuResourceModel> allMenuResourcesAsRole = adminSecurityResourceDao.findMenuResourcesByRoleId(roleId,
                AdminMenuResourceModel.CATEGORY, null, MENU_STATUS_NOTDELETE);
        for (AdminMenuResourceModel menuResourceModel : topMenuResources) {
            menuResourceModel.setChecked(this.content(menuResourceModel, allMenuResourcesAsRole));
            menuResourceModel.setChildren(this.getChildren(menuResourceModel.getId(), allMenuResourcesAsRole, actorId));
        }
        return topMenuResources;
    }

    /**
     * @param id
     * @param allMenuResourcesAsRole
     * @return
     */
    private List<AdminMenuResourceModel> getChildren(Integer id, List<AdminMenuResourceModel> allMenuResourcesAsRole, Integer actorId) {
        List<AdminMenuResourceModel> result = adminSecurityResourceDao.getChildMenu(id, MENU_STATUS_NOTDELETE,
                AdminMenuResourceModel.CATEGORY);
        for (AdminMenuResourceModel menuResourceModel : result) {
            menuResourceModel.setChecked(this.content(menuResourceModel, allMenuResourcesAsRole));
            if (this.getChildrenIsType(menuResourceModel.getId(), actorId) != null
                    && this.getChildrenIsType(menuResourceModel.getId(), actorId).size() > 0) {
                menuResourceModel.setChildren(this.getChildren(menuResourceModel.getId(), allMenuResourcesAsRole, actorId));
            }
        }
        return result;
    }

    /**
     * 分配给角色的菜单资源集合中是否包含指定的菜单资源
     *
     * @param menuResourceModel
     * @param allMenuResourcesAsRole
     * @return
     */
    private boolean content(AdminMenuResourceModel menuResourceModel,
                            List<AdminMenuResourceModel> allMenuResourcesAsRole) {
        for (AdminMenuResourceModel menuResourceModelAsRole : allMenuResourcesAsRole) {
            if (menuResourceModelAsRole.getId().equals(menuResourceModel.getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 集合中是否已存在给菜单 菜单对象是否相等或菜单对象的id是否相等
     *
     * @param menuResourceModels
     * @param resourceModel
     * @return
     */
    private boolean hasMenuContent(List<AdminMenuResourceModel> menuResourceModels,
                                   AdminMenuResourceModel resourceModel) {

        for (AdminMenuResourceModel model : menuResourceModels) {
            if (model.getId().intValue() == resourceModel.getId().intValue()
                    || menuResourceModels.contains(resourceModel)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<AdminMenuResourceModel> getMenuPedigreeByActorId(AdminUserModel actor) {
        if (actor == null) {
            return new ArrayList<>();
        }
        /**
         * 1.根据actorId判断 actor是否是admin 是的话返回所有的 menu 2.不是的话根据
         * actorId获取其角色来获取menu
         */
        List<AdminMenuResourceModel> firstMenus = new ArrayList<AdminMenuResourceModel>();
        List<AdminMenuResourceModel> tempList = new ArrayList<>();
        if (actor.getId().equals(0)) {
            // 获得所有的一级菜单
            AdminMenuResourceModel menuResourceModel = new AdminMenuResourceModel();
            menuResourceModel.setCategory(AdminMenuResourceModel.CATEGORY);
            menuResourceModel.setType(0);
            menuResourceModel.setLevel(0);
            tempList = adminSecurityResourceDao.findMenuResources(menuResourceModel);
            for (AdminMenuResourceModel firstMenuResourceModel : tempList) {
                if (!this.hasMenuContent(firstMenus, firstMenuResourceModel)) {
                    firstMenus.add(firstMenuResourceModel);
                }
            }
            for (AdminMenuResourceModel firstMenu : firstMenus) {
                handleChilden(firstMenu);
            }
            return firstMenus;

        } else if (actor.getRoleType().equals(1)) {//代理系统管理员
            tempList = adminSecurityResourceDao
                    .findMenuResourcesByDeptId(null, AdminMenuResourceModel.CATEGORY, 0, MENU_STATUS_NOTDELETE, actor.getDataId(), 1);
        } else if (actor.getIsManager().equals(1)) {//部门管理员
            tempList = adminSecurityResourceDao
                    .findMenuResourcesByDeptId(actor.getDeptId(), AdminMenuResourceModel.CATEGORY, 0, MENU_STATUS_NOTDELETE, actor.getDataId(), 0);

        }
        List<AdminRoleModel> roleModels = adminAuthorityDao.queryGrantRolesByUserId(actor.getId(),
                AdminRoleModel.CATEGORY);
        /**
         * 获得已授权的一级菜单数据
         */
        for (AdminRoleModel roleModel : roleModels) {
            List<AdminMenuResourceModel> roleTempList = adminSecurityResourceDao
                    .findMenuResourcesByRoleId(roleModel.getId(), AdminMenuResourceModel.CATEGORY, 0,
                            MENU_STATUS_NOTDELETE);
            for (AdminMenuResourceModel temp_menu : roleTempList) {
                if (!this.hasMenuContent(firstMenus, temp_menu)) {
                    firstMenus.add(temp_menu);
                }
            }

            for (AdminMenuResourceModel firstMenu : firstMenus) {
                handleChildenMeusByRoleIds(firstMenu, roleModels);
            }

        }

        for (AdminMenuResourceModel temp_menu : tempList) {
            if (!this.hasMenuContent(firstMenus, temp_menu)) {
                firstMenus.add(temp_menu);
            }
        }

        /**
         * 组装一级菜单下的已授权的子菜单数据
         */
        for (AdminMenuResourceModel firstMenu : firstMenus) {
            handleChildenMenusByActor(firstMenu, actor);
        }
        return firstMenus;


    }

    private void handleChildenMenusByActor(AdminMenuResourceModel firstMenu, AdminUserModel actor) {
        List<AdminMenuResourceModel> childrenMenus = getChildenMeusByMenuIdAndActor(firstMenu.getId(), actor);
        if (childrenMenus != null && childrenMenus.size() > 0) {
            firstMenu.setChecked(true);
            List<AdminMenuResourceModel> childList = firstMenu.getChildren();
            if (CollectionUtils.isEmpty(childList)) {
                childList = new ArrayList<>();
            }
            for (AdminMenuResourceModel temp_menu : childrenMenus) {
                if (!this.hasMenuContent(childList, temp_menu)) {
                    childList.add(temp_menu);
                }
            }
            firstMenu.setChildren(childList);

            // for (AdminMenuResourceModel childMenu : childrenMenus) {
            // /**
            // * 递归一下处理孙子以及以下的菜单
            // */
            // this.handleChildenMeusByRoleIds(childMenu, roleModels, dataId);
            // }
        }
    }

    private List<AdminMenuResourceModel> getChildenMeusByMenuIdAndActor(Integer id, AdminUserModel actor) {
        List<AdminMenuResourceModel> result = new ArrayList<AdminMenuResourceModel>();
        Integer type = 0;
        if (actor.getRoleType().equals(1)) {
            type = 1;
            actor.setDeptId(null);
        } else if (actor.getIsManager().equals(0)) {//不是部门管理员
            return result;
        }

        List<AdminMenuResourceModel> list = adminSecurityResourceDao.queryMenuResourcesByParentIdActor(id, actor.getDeptId(), actor.getDataId(),
                type, MENU_STATUS_NOTDELETE, AdminMenuResourceModel.CATEGORY);
        for (AdminMenuResourceModel menu_temp : list) {
            if (!this.hasMenuContent(result, menu_temp)) {
                result.add(menu_temp);
            }
        }
        return result;
    }

    // ****************************************************************************************************
    private void handleChilden(AdminMenuResourceModel menuModel) {
        /**
         * 1：获取所有的菜单数据 2：根据菜单的上下级关系组装菜单数据 3：递归子菜单
         */
        List<AdminMenuResourceModel> childrenMenus = getChildenMeusByMenuId(menuModel.getId());
        if (childrenMenus != null && childrenMenus.size() > 0) {
            menuModel.setChecked(true);
            menuModel.setChildren(childrenMenus);
            // for (AdminMenuResourceModel childMenu : childrenMenus) {
            // /**
            // * 递归一下处理孙子以及以下的菜单
            // */
            // this.handleChilden(childMenu, type);
            // }
        }
    }

    private List<AdminMenuResourceModel> getChildenMeusByMenuId(Integer menuId) {
        List<AdminMenuResourceModel> result = new ArrayList<AdminMenuResourceModel>();
        List<AdminMenuResourceModel> temp_list = adminSecurityResourceDao.getChildMenu(menuId, MENU_STATUS_NOTDELETE,
                AdminMenuResourceModel.CATEGORY);
        for (AdminMenuResourceModel menu_temp : temp_list) {
            if (!this.hasMenuContent(result, menu_temp)) {
                result.add(menu_temp);
            }
        }
        return result;
    }

    // ********************************************************************************************

    /**
     * 递归组装一级菜单下所有分配给角色的菜单数据
     *
     * @param roleModels
     */
    private void handleChildenMeusByRoleIds(AdminMenuResourceModel menuModel, List<AdminRoleModel> roleModels) {
        /**
         * 1：根据角色数据获得所有的菜单数据 2：根据菜单的上下级关系组装菜单数据 3：递归子菜单
         */
        List<AdminMenuResourceModel> childrenMenus = getChildenMeusByMenuIdRoles(menuModel.getId(), roleModels);
        if (childrenMenus != null && childrenMenus.size() > 0) {
            menuModel.setChecked(true);
            menuModel.setChildren(childrenMenus);
            // for (AdminMenuResourceModel childMenu : childrenMenus) {
            // /**
            // * 递归一下处理孙子以及以下的菜单
            // */
            // this.handleChildenMeusByRoleIds(childMenu, roleModels, dataId);
            // }
        }

    }

    /**
     * 带权限查询子菜单 关于带权限子菜单数据的查询
     * 根据角色id和菜单的id获得子菜单的数据，汇总成子菜单的一个集合，该集合需要验证子菜单是否已经存在，如果存在了就不添加了
     * 因为多角色情况下不同的角色授权的菜单数据可能会重叠，需要判重操作
     *
     * @param menuId
     * @param roleModels
     * @return
     */
    private List<AdminMenuResourceModel> getChildenMeusByMenuIdRoles(Integer menuId, List<AdminRoleModel> roleModels) {
        List<AdminMenuResourceModel> result = new ArrayList<AdminMenuResourceModel>();
        for (AdminRoleModel roleModel : roleModels) {
            List<AdminMenuResourceModel> temp_list = adminSecurityResourceDao.queryMenuResourcesByParentIdRoleId(menuId,
                    roleModel.getId(), MENU_STATUS_NOTDELETE, AdminMenuResourceModel.CATEGORY);
            for (AdminMenuResourceModel menu_temp : temp_list) {
                if (!this.hasMenuContent(result, menu_temp)) {
                    result.add(menu_temp);
                }
            }
        }
        return result;
    }

    @Override
    public Page<AdminMenuResourceModel> pagingqueryMenu(String menuName, Integer type, Integer pageIndex,
                                                        Integer pageSize) {
        Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0;

        List<AdminMenuResourceModel> resourceModels = adminSecurityResourceDao.pagingqueryMenu(menuName,
                AdminMenuResourceModel.CATEGORY, type, MENU_STATUS_NOTDELETE, limit, pageSize);
        Integer totalCount = adminSecurityResourceDao.pagingqueryMenu_count(menuName, AdminMenuResourceModel.CATEGORY,
                type, MENU_STATUS_NOTDELETE);

        Page<AdminMenuResourceModel> menuPage = new Page<>(pageIndex, pageSize, totalCount);
        menuPage.setData(resourceModels);

        return menuPage;
    }

    /**
     * 根据获取 叶子菜单
     */
    @Override
    public List<AdminMenuResourceModel> getAllLeafMenu(Integer type) {
        List<AdminMenuResourceModel> leafMenus = new ArrayList<AdminMenuResourceModel>();
        List<AdminMenuResourceModel> menuResourceModels = adminSecurityResourceDao.getAllLeafMenu(type,
                AdminMenuResourceModel.CATEGORY);
        for (AdminMenuResourceModel menuResourceModel : menuResourceModels) {
            if (!this.hasMenuContent(leafMenus, menuResourceModel)) {
                leafMenus.add(menuResourceModel);
            }
        }
        return leafMenus;
    }

    @Override
    public List<AdminMenuResourceModel> getAllMenuPageUrl(Integer roleId, Integer type, AdminUserModel actor) {
        /**
         * 1：获得所有的一级菜单，一级菜单应该是不会有相关的页面和URL的配置 2：根据一级菜单获得子菜单（注意需要递归，因为菜单的层级不能确定）
         * 3：处理子菜单，子菜单授权检查、页面元素和URL数据关联和授权检查
         */
        List<AdminMenuResourceModel> topMenus = null;
        if (actor.getId().equals(0)) {
            topMenus = this.findTopMenuResources(type);
        } else if (actor.getRoleType().equals(1)) {//代理admin
            topMenus = this.findMenuResourceByCategoryAndDeptId(AdminMenuResourceModel.CATEGORY, null, actor.getDataId(), 1);
        } else if (actor.getIsManager().equals(1)) {
            topMenus = this.findMenuResourceByCategoryAndDeptId(AdminMenuResourceModel.CATEGORY, actor.getDeptId(), actor.getDataId(), 0);
        } else {
            topMenus = adminSecurityResourceDao.getAdminMenuResourceByActorId(actor.getId());
        }

        for (AdminMenuResourceModel topMenu : topMenus) {
            topMenu.setChecked(this.isAssignmen(roleId, topMenu.getId(), actor.getDataId()));
            if (actor.getRoleType().equals(1) || actor.getIsManager().equals(1)) {
                this.handleRoleChildenMenu(topMenu, roleId, actor);
            } else {
                this.handleChildMenu(topMenu, roleId, actor.getDataId(), type, actor.getId());
            }
        }
        return topMenus;
    }

    private void handleRoleChildenMenu(AdminMenuResourceModel topMenu, Integer roleId, AdminUserModel actor) {
        List<AdminMenuResourceModel> childMenus = adminDeptService.findResourceByParentId(topMenu.getId(), null, actor);
        for (AdminMenuResourceModel childMenu : childMenus) {
            List<AdminSecurityResourceModel> pageElementResourceModels = null;
            if (actor.getId().equals(0)) {
                pageElementResourceModels = adminSecurityResourceDao
                        .getAllByMenuId(childMenu.getId(), AdminPageElementResourceModel.CATEGORY, 0);
            } else if (actor.getRoleType().equals(1)) {
                pageElementResourceModels = adminSecurityResourceDao
                        .getAllByMenuIdByCategoryAndDeptId(AdminPageElementResourceModel.CATEGORY, null, actor.getDataId(), 1, childMenu.getId());

            } else {
                pageElementResourceModels = adminSecurityResourceDao
                        .getAllByMenuIdByCategoryAndDeptId(AdminPageElementResourceModel.CATEGORY, actor.getDeptId(), actor.getDataId(), 0, childMenu.getId());
            }

            for (AdminSecurityResourceModel resourceModel : pageElementResourceModels) {
                resourceModel.setChecked(this.isAssignmen(roleId, resourceModel.getId(), actor.getDataId()));
            }
            childMenu.setPageElements(pageElementResourceModels);

            childMenu.setChecked(this.isAssignmen(roleId, childMenu.getId(), actor.getDataId()));
            List<AdminMenuResourceModel> sonMenus = adminDeptService.findResourceByParentId(childMenu.getId(), null, actor);
            // 递归
            if (sonMenus != null && sonMenus.size() > 0) {
                for (AdminMenuResourceModel sonMenu : sonMenus) {
                    this.handleRoleChildenMenu(sonMenu, roleId, actor);
                }
                childMenu.setChildren(sonMenus);
            }
        }

        topMenu.setChildren(childMenus);
    }

    /**
     * 子菜单数据以及相关数据的组装 1: 检查是否授权 2：页面元素和URL数据获得 3：检查页面和URL数据的授权
     * 4：递归一下是否还有子菜单，基本上是不会有的
     *
     * @param topMenu
     * @param roleId
     * @param dataId
     */
    private void handleChildMenu(AdminMenuResourceModel topMenu, Integer roleId, Integer dataId, Integer type, Integer actorId) {
        List<AdminMenuResourceModel> childMenus = this.getChildrenIsType(topMenu.getId(), actorId);
        for (AdminMenuResourceModel childMenu : childMenus) {
            List<AdminSecurityResourceModel> pageElementResourceModels = null;
            if (actorId.equals(0)) {
                pageElementResourceModels = adminSecurityResourceDao
                        .getAllByMenuId(childMenu.getId(), AdminPageElementResourceModel.CATEGORY, type);
            } else {
                pageElementResourceModels = adminSecurityResourceDao
                        .getAllByMenuIdByActorId(childMenu.getId(), AdminPageElementResourceModel.CATEGORY, type, actorId);
            }

            for (AdminSecurityResourceModel resourceModel : pageElementResourceModels) {
                resourceModel.setChecked(this.isAssignmen(roleId, resourceModel.getId(), dataId));
            }
            childMenu.setPageElements(pageElementResourceModels);

            childMenu.setChecked(this.isAssignmen(roleId, childMenu.getId(), dataId));
            List<AdminMenuResourceModel> sonMenus = this.getChildrenIsType(childMenu.getId(), actorId);
            // 递归
            if (sonMenus != null && sonMenus.size() > 0) {
                for (AdminMenuResourceModel sonMenu : sonMenus) {
                    this.handleChildMenu(sonMenu, roleId, dataId, type, actorId);
                }
                childMenu.setChildren(sonMenus);
            }
        }
        topMenu.setChildren(childMenus);
    }

    /**
     * 数据授权验证
     *
     * @param roleId
     * @param resourceId
     * @param dataId
     * @return
     */
    private boolean isAssignmen(Integer roleId, Integer resourceId, Integer dataId) {
        AdminResourceAssignmentModel assignmentModel = adminResourceAssignmentDao.getByDataIdAthourIdResourceId(dataId,
                roleId, resourceId);
        return assignmentModel != null;
    }

    /**
     * 根据角色id 权限资源id 数据类型保存角色权限信息
     *
     * @param roleId
     * @param securityResourceIds
     * @param dataId
     */
    @Override
    public void saveMenuPageUrlToRole(Integer roleId, List<Integer> securityResourceIds, Integer dataId) {
        List<Integer> oldAminResourcesAssignment = adminResourceAssignmentDao
                .getAdminResourceAssignmentIdsByRoleId(dataId, roleId);
        if (!oldAminResourcesAssignment.isEmpty()) {
            // Map<String, Object> param = new HashMap<String, Object>();
            // param.put("authorityId", roleId);
            // param.put("securityResourceIds", oldAminResourcesAssignment);
            // adminResourceAssignmentDao.deleteAdminResourceAssignmentsByIds(oldAminResourcesAssignment);
            adminResourceAssignmentDao.deleteAdminResourceAssignmentsByAuthorityId(roleId);
        }
        if (securityResourceIds.size() > 0) {
            List<AdminResourceAssignmentModel> list = new ArrayList<>();
            for (Integer securityResourceId : securityResourceIds) {
                AdminResourceAssignmentModel resourceAssignmentModel = new AdminResourceAssignmentModel();
                resourceAssignmentModel.setAuthorityId(roleId);
                resourceAssignmentModel.setSecurityResourceId(securityResourceId);
                resourceAssignmentModel.setDataId(dataId);
                list.add(resourceAssignmentModel);
            }
            adminResourceAssignmentDao.creatAdminResourceAssignment(list);
        }

    }

    /**
     * 递归查找菜单下所有的子菜单
     *
     * @param parentId
     * @return
     */
    private List<AdminMenuResourceModel> getChildrenIsType(Integer parentId, Integer actorId) {
        List<AdminMenuResourceModel> result = null;
        if (actorId.equals(0)) {
            result = adminSecurityResourceDao.getChildMenu(parentId, MENU_STATUS_NOTDELETE,
                    AdminMenuResourceModel.CATEGORY);
        } else {
            result = adminSecurityResourceDao.getChildMenuByActorId(parentId, MENU_STATUS_NOTDELETE,
                    AdminMenuResourceModel.CATEGORY, actorId);
        }
        for (AdminMenuResourceModel menuResourceModel : result) {
            if (this.getChildrenIsType(menuResourceModel.getId(), actorId) != null
                    && this.getChildrenIsType(menuResourceModel.getId(), actorId).size() > 0) {
                menuResourceModel.setChildren(this.getChildrenIsType(menuResourceModel.getId(), actorId));
            }
        }
        return result;
    }


    @Override
    public void deleteAdminResourceAssignmentsByAuthorityId(Integer roleId) {
        adminResourceAssignmentDao.deleteAdminResourceAssignmentsByAuthorityId(roleId);

    }

    @Override
    public void deleteAdminResourceAssignmentsByDeptId(Integer deptId) {
        adminResourceAssignmentDao.deleteAdminResourceAssignmentsByDeptId(deptId);
    }

    @Override
    public void saveMenuPageUrlToDeptId(Integer deptId, List<Integer> securityResourceList, Integer agencyId) {
        List<Integer> oldAminResourcesAssignment = adminResourceAssignmentDao
                .getAdminResourceAssignmentIdsByDeptId(agencyId, deptId);
        if (!oldAminResourcesAssignment.isEmpty()) {
            adminResourceAssignmentDao.deleteAdminResourceAssignmentsByDeptId(deptId);
        }
        if (securityResourceList.size() > 0) {
            List<AdminResourceAssignmentModel> list = new ArrayList<>();
            for (Integer securityResourceId : securityResourceList) {
                AdminResourceAssignmentModel resourceAssignmentModel = new AdminResourceAssignmentModel();
                resourceAssignmentModel.setDeptId(deptId);
                resourceAssignmentModel.setSecurityResourceId(securityResourceId);
                resourceAssignmentModel.setDataId(agencyId);
                list.add(resourceAssignmentModel);
            }

            adminResourceAssignmentDao.creatAdminResourceAssignment(list);
        }
    }

    @Override
    public List<AdminMenuResourceModel> getMenuPedigressByDeptId(AdminUserModel userModel) {
        if (userModel == null) {
            return new ArrayList<>();
        }
        /**
         * 1.根据actorId判断 actor是否是admin 是的话返回所有的 menu 2.不是的话根据
         * actorId获取其角色来获取menu
         */
        List<AdminMenuResourceModel> firstMenus = new ArrayList<>();
        if (userModel.getId().equals(0)) {
            // 获得所有的一级菜单
            AdminMenuResourceModel menuResourceModel = new AdminMenuResourceModel();
            menuResourceModel.setCategory(AdminMenuResourceModel.CATEGORY);
            menuResourceModel.setType(0);
            menuResourceModel.setLevel(0);
            List<AdminMenuResourceModel> firstMenuResourceModels = adminSecurityResourceDao
                    .findMenuResources(menuResourceModel);
            for (AdminMenuResourceModel firstMenuResourceModel : firstMenuResourceModels) {
                if (!this.hasMenuContent(firstMenus, firstMenuResourceModel)) {
                    firstMenus.add(firstMenuResourceModel);
                }
            }
            for (AdminMenuResourceModel firstMenu : firstMenus) {
                handleChilden(firstMenu);
            }
            return firstMenus;
        }

        List<AdminRoleModel> roleModels = adminAuthorityDao.queryGrantRolesByDeptId(userModel.getDeptId());
        /**
         * 获得已授权的一级菜单数据
         */
        for (AdminRoleModel roleModel : roleModels) {
            List<AdminMenuResourceModel> firstMenuResourceModels_temp = adminSecurityResourceDao
                    .findMenuResourcesByRoleId(roleModel.getId(), AdminMenuResourceModel.CATEGORY, 0,
                            MENU_STATUS_NOTDELETE);
            for (AdminMenuResourceModel temp_menu : firstMenuResourceModels_temp) {
                if (!this.hasMenuContent(firstMenus, temp_menu)) {
                    firstMenus.add(temp_menu);
                }
            }
        }
        /**
         * 组装一级菜单下的已授权的子菜单数据
         */
        for (AdminMenuResourceModel firstMenu : firstMenus) {
            handleChildenMeusByRoleIds(firstMenu, roleModels);
        }
        return firstMenus;
    }

    @Override
    public List<AdminMenuResourceModel> findTreeMenus(Integer level, Integer type) {
        // 获得所有的一级菜单
        List<AdminMenuResourceModel> firstMenus = new ArrayList<AdminMenuResourceModel>();
        AdminMenuResourceModel menuResourceModel = new AdminMenuResourceModel();
        menuResourceModel.setCategory(AdminMenuResourceModel.CATEGORY);
        menuResourceModel.setType(0);
        menuResourceModel.setLevel(0);
        List<AdminMenuResourceModel> firstMenuResourceModels = adminSecurityResourceDao.findMenuResources(menuResourceModel);
        for (AdminMenuResourceModel firstMenuResourceModel : firstMenuResourceModels) {
            if (!this.hasMenuContent(firstMenus, firstMenuResourceModel)) {
                firstMenus.add(firstMenuResourceModel);
            }
        }
        for (AdminMenuResourceModel firstMenu : firstMenus) {
            handleChilden(firstMenu);
        }
        return firstMenus;
    }

    @Override
    public List<AdminMenuResourceModel> findMenuResourceByCategoryAndDeptId(String category, Integer deptId, Integer agencyId, int type) {

        return adminSecurityResourceDao.findMenuResourcesByDeptId(deptId, category, 0, 1, agencyId, type);
    }


}
