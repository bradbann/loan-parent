package org.songbai.loan.admin.admin.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminPerssionModel;
import org.songbai.loan.admin.admin.model.AdminRolePermissionsModel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 
 * @author SUNDONG_
 *
 */
@Component
public interface AdminRolePermissionsService
{
	/**
	 * 创建权限
	 * @param permissionsModel
	 */
	public void createRolePermissions(Integer roleId, Integer permissionsId);
	/**
	 * 更新权限
	 * @param permissionsModel
	 */
	public void updateRolePermissions(Integer roleId, Integer permissionsId);
	/**
	 * 获取一条权限数据
	 * @param PermissionsId
	 * @return
	 */
	public AdminRolePermissionsModel getPermissions(Integer PermissionsId);
	/**
	 * 获取一组权限数据
	 * @param roleId
	 * @return
	 */
	public List<AdminRolePermissionsModel> getPermissionsList(Integer roleId);
	/**
	 * 删除一条权限
	 * @param PermissionsId
	 */
	public void deletePermissions(Integer roleId);
	
	/**
	 * 根据页面元素Id获得授予页面元素的权限
	 * @param page
	 * @param pageSize
	 * @param pageElementId
	 * @return
	 */
	public Page<AdminPerssionModel> pagingQueryGrantPermissionsByPageElementResourceId(Integer page, Integer pageSize, Integer pageElementId);
}
