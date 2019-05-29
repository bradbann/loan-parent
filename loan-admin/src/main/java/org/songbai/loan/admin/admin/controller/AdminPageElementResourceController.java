package org.songbai.loan.admin.admin.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.RespCode;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminPageElementResourceModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminPageElementResourceService;
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
import java.util.Map;

//import org.songbai.cloud.basics.mvc.RespCode;

@Controller
@RequestMapping("/pageElement")
public class AdminPageElementResourceController {
    @Autowired
    AdminPageElementResourceService adminPageElementResourceService;
    @Autowired
    AdminUserHelper adminUserHelper;

    Logger logger = LoggerFactory.getLogger(AdminPageElementResourceController.class);

    /**
     * @param name
     * @param description
     * @param description
     * @return
     */
    @RequestMapping(value = "/addPageElement")
    @ResponseBody
    public Response addPageElement(String name, String description, String identifier, Integer menuId, Integer type,
                                   String url) {

        Assert.notNull(name, "页面元素名称不能为空");
        Assert.notNull(identifier, "页面元素标识不能为空");
        Assert.notNull(menuId, "菜单id不能为空");
        AdminPageElementResourceModel elementResourceModel = new AdminPageElementResourceModel();
        elementResourceModel.setName(name);
        elementResourceModel.setUrl(url);
        elementResourceModel.setDescription(description);
        elementResourceModel.setIdentifier(identifier);
        elementResourceModel.setParentId(menuId);
        elementResourceModel.setType(type);
        if (adminPageElementResourceService.hasPageElementByIdentifier(identifier, type)) {
            return Response.response(RespCode.SERVER_ERROR, "保存失败，已存在的元素标识");
        }

        adminPageElementResourceService.savePageElement(elementResourceModel);
        return Response.success();
    }

    /**
     * 多条件查询页面元素
     *
     * @param page
     * @param pageSize
     * @param name
     * @param description
     * @return
     */
    @RequestMapping(value = "/pagingQuery")
    @ResponseBody
    public Response pagingQuery(Integer page, Integer pageSize, String name, String description, Integer type,
                                Integer parentId) {

        pageSize = pageSize == null ? Page.DEFAULE_PAGESIZE : pageSize;
        page = page == null ? 0 : page;
        Map<String, Object> param = new HashMap<String, Object>();
        if (name != null) {
            param.put("name", name);
        }
        if (description != null) {
            param.put("description", description);
        }
        param.put("category", AdminPageElementResourceModel.CATEGORY);
        param.put("type", type);
        if (parentId != null) {
            param.put("parentId", parentId);
        }

        Page<AdminPageElementResourceModel> data = adminPageElementResourceService.pagingQueryPageElement(page,
                pageSize, param);
        return Response.success(data);
    }

    /**
     * 页面元素修改
     *
     * @param id
     * @param name
     * @param description
     * @return
     */
    @RequestMapping(value = "/updatePageElement")
    @ResponseBody
    public Response updatePageElement(Integer id, String name, String description, String identifier, Integer type,
                                      Integer menuId, String url) {
        Assert.notNull(id, "id不能为空");
        Assert.notNull(name, "页面元素名称不能为空");
        Assert.notNull(identifier, "页面元素标识不能为空");
        Assert.notNull(menuId, "菜单id不能为空");
        AdminPageElementResourceModel elementResourceModel = new AdminPageElementResourceModel();
        elementResourceModel.setId(id);
        elementResourceModel.setName(name);
        elementResourceModel.setUrl(url);
        elementResourceModel.setDescription(description);
        elementResourceModel.setIdentifier(identifier);
        elementResourceModel.setParentId(menuId);
        elementResourceModel.setType(type);
        adminPageElementResourceService.updatePageElement(elementResourceModel);
        return Response.success();
    }

    /**
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deletePageElement")
    @ResponseBody
    public Response deletePageElement(String ids) {

        Assert.notNull(ids, "要删除的菜单Id不能为空");
        List<Integer> pageElementIds = new ArrayList<Integer>();
        String temp[] = ids.split(",");
        for (int i = 0; i < temp.length; i++) {
            pageElementIds.add(Integer.valueOf(temp[i]));
        }
        adminPageElementResourceService.deletePageElement(pageElementIds);

        return Response.success();
    }

    /**
     * 根据页面元素权限资源ID分页查询已经授权的权限Permission。
     *
     * @param page
     * @param pageSize
     * @param pageElementResourceId
     * @return
     */
    @RequestMapping(value = "/pagingQueryGrantPermissionsByPageElementResourceId")
    @ResponseBody
    public Response pagingQueryGrantPermissionsByPageElementResourceId(Integer page, Integer pageSize,
                                                                       Integer pageElementResourceId) {

        pageSize = pageSize == null ? Page.DEFAULE_PAGESIZE : pageSize;
        page = page == null ? 0 : page;
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("category", AdminPageElementResourceModel.CATEGORY);
        Page<AdminPageElementResourceModel> data = adminPageElementResourceService.pagingQueryPageElement(page,
                pageSize, param);
        return Response.success(data);
    }

//	/**
//	 * 批量判断登录用户是否拥有页面元素的权限 前端在渲染页面时会将页面中需要验证权限的元素的标识一次提交到后台来验证，
//	 * 如果登录用户拥有此权限，会将该权限的标识返回给前端，如果没有此权限将不会返回此标识 如果将所有标识按照key=value格式返回前端不好处理
//	 *
//	 * @param identifiers
//	 *            页面元素数组，多个以","分隔
//	 * @param request
//	 * @param response
//	 * @return
//	 */
//	@RequestMapping(value = "/safe_hasRightByIdentifiers")
//	@ResponseBody
//	public Response hasRightByIdentifiers(String identifiers, HttpServletRequest request,
//			HttpServletResponse response) {
//		Integer type = super.getCurrentChannel(request).getLevel();
//		Assert.notNull(identifiers, "页面元素表示不能为空");
//		String[] identifieres = identifiers.split(",");
//		logger.info(identifiers);
//		Integer actorId = (Integer) request.getSession().getAttribute("userId");
//		AdminUserModel user = (AdminUserModel) request.getSession().getAttribute("user");
//		List<String> result = new ArrayList<String>();
//		for (int i = 0; i < identifieres.length; i++) {
//			if (adminPageElementResourceService.hasRightByActorIdDdentifier(actorId, identifieres[i],
//					super.getDataId(request), type, user)) {
//				result.add(identifieres[i]);
//			}
//		}
//		return Response.success(result);
//	}

    /**
     * 根据登录用户来获取 用户所用有的pageElement
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/safe_getPageElementAll")
    @ResponseBody
    public Response getPageElementAll(HttpServletRequest request, HttpServletResponse response) {
        AdminUserModel user = adminUserHelper.getAdminUser(request);
        Integer actorId = adminUserHelper.getAdminUserId(request);
        List<String> pageElementResourcesAll = adminPageElementResourceService.getPageElementByActorId(actorId, user);
        return Response.success(pageElementResourcesAll);
    }
}
