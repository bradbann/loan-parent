package org.songbai.loan.admin.admin.controller;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminMenuResourceModel;
import org.songbai.loan.admin.admin.service.AdminMenuResouceService;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@RequestMapping("/menu")
public class AdminMenuController {

    @Autowired
    AdminMenuResouceService adminMenuResouceService;


    @Autowired
    AdminUserHelper adminUserHelper;

    /**
     * 根据上级菜单Id获得所有的子菜单 完善权限模块之后注意权限的处理
     *
     * @param parentCode
     * @return
     */
    @RequestMapping(value = "/getChildMenuByParentCode")
    @ResponseBody
    public Response getChildMenuByParentCode(Integer parentCode, HttpServletRequest request) {
        Integer actorId = adminUserHelper.getAdminUserId(request);
        AdminMenuResourceModel menuResourceModel = adminMenuResouceService.getMenuPedigree(parentCode, actorId);
        return Response.success(menuResourceModel.getChildren());
    }

    @RequestMapping(value = "/safe_getMenuWithRoleId")
    @ResponseBody
    public Response getMenuWithRoleId(HttpServletRequest request, HttpServletResponse response) {
        List<AdminMenuResourceModel> result = adminUserHelper.getMenu(request);
        return Response.success(result);
    }

    /**
     * 保存菜单
     */
    @RequestMapping(value = "/saveMenu")
    @ResponseBody
    public Response saveMenu(Integer parentId, String name, String url, String menuIcon, String sourceName,
                             String description, Integer type, Integer position, HttpServletRequest request,String code) {

        parentId = parentId == null ? 0 : parentId;
        Assert.notNull(name, "菜单名称不能为空！！");
        Assert.notNull(code,"菜单编号不能为空!");
        AdminMenuResourceModel menuResourceModel = new AdminMenuResourceModel();
        menuResourceModel.setName(name);
        menuResourceModel.setParentId(parentId);
        menuResourceModel.setDescription(description);
        menuResourceModel.setPosition(position);
        menuResourceModel.setUrl(url);
        menuResourceModel.setMenuIcon(menuIcon);
        menuResourceModel.setPosition(position);
        menuResourceModel.setType(type);
        menuResourceModel.setCode(code);
        adminMenuResouceService.saveMenu(menuResourceModel);
        return Response.success();
    }

    /**
     * 菜单的修改
     *
     * @return
     */
    @RequestMapping(value = "/updateMenu")
    @ResponseBody
    public Response update(Integer parentId, Integer id, String name, String url, String menuIcon, String menuDesc,
                           Integer position, String description,String code) {

        Assert.notNull(parentId, "上级菜单Id不能为空！！");
        Assert.notNull(id, "菜单Id不能为空！！");
        Assert.notNull(name, "菜单名称不能为空！！");
        Assert.notNull(code, "菜单编号不能为空！！");
        AdminMenuResourceModel menuResourceModel = new AdminMenuResourceModel();
        menuResourceModel.setId(id);
        menuResourceModel.setName(name);
        menuResourceModel.setParentId(parentId);
        menuResourceModel.setDescription(description);
        menuResourceModel.setPosition(position);
        menuResourceModel.setUrl(url);
        menuResourceModel.setMenuIcon(menuIcon);
        menuResourceModel.setCode(code);
        adminMenuResouceService.updateMenu(menuResourceModel);
        return Response.success();
    }

    /**
     * 删除一条菜单记录
     *
     * @param id
     * @return
     * @deprecated
     */
    @Deprecated
    @RequestMapping(value = "/removeMenu")
    @ResponseBody
    public Response remove(Integer id, HttpServletRequest request) {
        Integer actorId = adminUserHelper.getAdminUserId(request);
        return Response.success(adminMenuResouceService.removeMenu(id, actorId));
    }

    /**
     * 删除菜单 包含菜单的多条删除 前端传递的Id参数为字符串，多条Id用“,”分隔 关于菜单删除的逻辑： 1：菜单包含子菜单不能删除菜单
     * 2：删除菜单时需要删除菜单关系的相关数据
     *
     * @return
     */
    @RequestMapping(value = "/removeMenus")
    @ResponseBody
    public Response removes(String ids) {
        Assert.notNull(ids, "要删除的菜单Id不能为空");
        List<Integer> menuIds = new ArrayList<Integer>();
        String temp[] = ids.split(",");
        for (int i = 0; i < temp.length; i++) {
            menuIds.add(Integer.valueOf(temp[i]));
        }
        List<AdminMenuResourceModel> noRemoves = adminMenuResouceService.removeMenus(menuIds);

        return Response.success(noRemoves);

    }

    /**
     * 获得菜单数据的JSON for JSTree 格式仍需要调整
     */
    @Deprecated
    @RequestMapping(value = "/menuTreeData")
    @ResponseBody
    public List<HashMap<String, Object>> menuTreeData(Integer id, HttpServletRequest request) {
        Integer actorId = adminUserHelper.getAdminUserId(request);
        return this.handleMenu(adminMenuResouceService.getMenuPedigree(id, actorId));
    }

    /**
     * 组装菜单数据 将菜单数据组装成前端认识的数据 注意attributes属性中的数据
     *
     * @return
     */
    private List<HashMap<String, Object>> handleMenu(AdminMenuResourceModel menuResourceModel) {
        List<HashMap<String, Object>> result = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < menuResourceModel.getChildren().size(); i++) {
            AdminMenuResourceModel menuModel = menuResourceModel.getChildren().get(i);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("id", menuModel.getId());
            map.put("text", menuModel.getName());
            map.put("state",
                    (menuModel.getChildren() != null && menuModel.getChildren().size() > 0) ? "open" : "close");
            map.put("url", menuModel.getUrl());// 这个URL是为了treeGrid 下同index和desc
            map.put("index", menuModel.getPosition());
            map.put("desc", menuModel.getDescription());

            if (menuModel.getChildren() != null && menuModel.getChildren().size() > 0) {
                map.put("children", this.handleMenu(menuModel));
            }
            if (menuModel.getUrl() != null) {
                HashMap<String, Object> attrMap = new HashMap<String, Object>();
                attrMap.put("url", menuModel.getUrl());
                map.put("attributes", attrMap);
            }
            result.add(map);
        }
        return result;
    }

    /**
     * 查找菜单树 后台管理的菜单数据，返回全部的菜单数据
     *
     * @param page
     * @param pagesize
     * @return
     * @deprecated
     */
    @Deprecated
    @RequestMapping(value = "/findAllMenusTree")
    @ResponseBody
    public Page<AdminMenuResourceModel> findAllMenusTree(Integer page, Integer pagesize, HttpServletRequest request) {
        Page<AdminMenuResourceModel> menuPage = new Page<AdminMenuResourceModel>();
        Integer actorId = adminUserHelper.getAdminUserId(request);
        List<AdminMenuResourceModel> data = adminMenuResouceService
                .findAllMenusTree(0, actorId);
        menuPage.setData(data);
        menuPage.setStart(0);
        menuPage.setResultCount(data.size());//  分页这样写总条数显然是不合适的
        return menuPage;
    }

    /**
     * 分页获得菜单列表
     *
     * @return
     */
//	@RequestMapping(value = "/pagingqueryMenu")
//	@ResponseBody
//	public Response pagingqueryMenu(String desc, Integer type, Integer page, Integer pageSize) {
//		Page<AdminMenuResourceModel> menuPage = adminMenuResouceService.pagingqueryMenu(desc, type, page, pageSize);
//		return Response.success(menuPage);
//	}
    @RequestMapping(value = "/pagingqueryMenu")
    @ResponseBody
    public Response pagingqueryMenu(Integer type) {
        List<AdminMenuResourceModel> menuPage = adminMenuResouceService.findTreeMenus(null, type);
        return Response.success(menuPage);
    }

    /**
     * 获取叶子菜单
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/getLeafMenu")
    @ResponseBody
    public Response getLeafMenu(Integer type, HttpServletRequest request, HttpServletResponse response) {
        List<AdminMenuResourceModel> leafMenu = adminMenuResouceService.getAllLeafMenu(type);
        return Response.success(leafMenu);
    }

    /**
     * 获取父级菜单
     */
    @Deprecated
    @RequestMapping(value = "/findMenuResources")
    @ResponseBody
    public Response findMenuResources(Integer type) {
        List<AdminMenuResourceModel> menuResourceModels = adminMenuResouceService.findTopMenuResources(type);
        return Response.success(menuResourceModels);

    }

    /**
     * 获取同级菜单
     */
    @RequestMapping(value = "/findMenus")
    @ResponseBody
    public Response findMenus(Integer level, Integer type) {
        List<AdminMenuResourceModel> menuResourceModels = adminMenuResouceService.findMenuResources(level, type);
        return Response.success(menuResourceModels);

    }

}
