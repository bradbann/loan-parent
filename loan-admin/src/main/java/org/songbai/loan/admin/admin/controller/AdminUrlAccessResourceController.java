package org.songbai.loan.admin.admin.controller;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.RespCode;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.model.AdminUrlAccessResourceModel;
import org.songbai.loan.admin.admin.service.AdminUrlAccessResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author wangd
 *
 */
@Controller
@RequestMapping("/urlAccess")
public class AdminUrlAccessResourceController {

	@Autowired
	AdminUrlAccessResourceService adminUrlAccessResourceService;

	/**
	 * 
	 * @param name
	 * @param description
	 * @param url
	 * @return
	 */
	@RequestMapping(value = "/addUrlAccess")
	@ResponseBody
	public Response addUrlAccess(String name, String description, String url, Integer type, Integer menuId) {

		Assert.notNull(name, "URL名称不能为空");
		Assert.notNull(url, "URL路径不能为空");
		AdminUrlAccessResourceModel accessResourceModel = new AdminUrlAccessResourceModel();
		accessResourceModel.setName(name);
		accessResourceModel.setDescription(description);
		accessResourceModel.setUrl(url);
		if (adminUrlAccessResourceService.hasUrlAccessByUrlAddress(url, type)) {
			return Response.response(RespCode.SERVER_ERROR, "已存在的URL地址");
		}
		accessResourceModel.setParentId(menuId);
		accessResourceModel.setType(type);
		adminUrlAccessResourceService.saveUrlAccess(accessResourceModel);
		return Response.success();

	}

	/**
	 * 
	 * @param id
	 * @param name
	 * @param description
	 * @param url
	 * @return
	 */
	@RequestMapping(value = "/updateUrlAccess")
	@ResponseBody
	public Response updateUrlAccess(Integer id, String name, String description, String url, Integer type,
			Integer menuId) {

		Assert.notNull(id, "id不能为空");
		Assert.notNull(name, "URL名称不能为空");
		Assert.notNull(url, "URL路径不能为空");
		AdminUrlAccessResourceModel accessResourceModel = new AdminUrlAccessResourceModel();
		accessResourceModel.setId(id);
		accessResourceModel.setName(name);
		accessResourceModel.setDescription(description);
		accessResourceModel.setUrl(url);
		accessResourceModel.setParentId(menuId);
		accessResourceModel.setType(type);
		adminUrlAccessResourceService.updateUrlAccess(accessResourceModel);
		return Response.success();
	}

	/**
	 * 多条件查询角色
	 * 
	 * @param page
	 * @param pageSize
	 * @param name
	 * @param description
	 * @return
	 */
	@RequestMapping(value = "/pagingQuery")
	@ResponseBody
	public Response pagingQuery(Integer page, Integer pageSize, String name, String description, String url,
			Integer type) {

		pageSize = pageSize == null ? Page.DEFAULE_PAGESIZE : pageSize;
		page = page == null ? 0 : page;
		Map<String, Object> param = new HashMap<String, Object>();
		if (name != null) {
			param.put("name", name);
		}
		if (description != null) {
			param.put("description", description);
		}
		if (url != null) {
			param.put("url", url);
		}
		param.put("category", AdminUrlAccessResourceModel.CATEGORY);
		param.put("type", type);
		Page<AdminUrlAccessResourceModel> data = adminUrlAccessResourceService.pagingQueryPermissions(page, pageSize,
				param);
		return Response.success(data);
	}

	@RequestMapping(value = "/deleteUrlAccess")
	@ResponseBody
	public Response deleteUrlAccess(String ids) {

		Assert.notNull(ids, "要删除的菜单Id不能为空");
		List<Integer> urlAccessIds = new ArrayList<Integer>();
		String temp[] = ids.split(",");
		for (int i = 0; i < temp.length; i++) {
			urlAccessIds.add(Integer.valueOf(temp[i]));
		}

		adminUrlAccessResourceService.deleteUrlAccess(urlAccessIds);
		return Response.success();
	}

	/**
	 * URL 地址是否重复验证 前端验证，后台保存时仍需要验证
	 * 
	 * @param url
	 * @return
	 */
	@RequestMapping(value = "/hasUrlAccessByUrlAddress")
	@ResponseBody
	public Response hasUrlAccessByUrlAddress(String url, Integer type) {

		Assert.notNull(url, "配置的地址不能为空");

		if (adminUrlAccessResourceService.hasUrlAccessByUrlAddress(url, type)) {
			return Response.response(0, "URL路径已经存在");
		}
		return Response.response(1, "");
	}

}
