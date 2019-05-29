package org.songbai.loan.admin.admin.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminPageElementResourceModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 页面元素资源权限
 * 
 * @author wangd
 *
 */
@Component
public interface AdminPageElementResourceService {

	public void savePageElement(AdminPageElementResourceModel pageElementResourceModel);

	public void updatePageElement(AdminPageElementResourceModel pageElementResourceModel);

	/**
	 * 删除页面元素之前需要判断该页面元素是否已经分配给角色了 如果分配给角色了默认删除该分配纪录。 删除的时候表示已经认为该页面元素是开放的
	 * 
	 * @param ids
	 */
	public void deletePageElement(List<Integer> ids);

	public Page<AdminPageElementResourceModel> pagingQueryPageElement(Integer page, Integer pageSize,
                                                                      Map<String, Object> param);

	/**
	 * 根据角色Id获得未授权的页面资源 多条件查询，至少角色Id是必须的
	 *
	 * @param page
	 * @param pageSize
	 * @param roleId
	 * @return
	 */
	public Page<AdminPageElementResourceModel> pagingQueryNotGrantPageElementsByRoleId(String name, String description,
                                                                                       String identifier, Integer roleId, Integer dataId, Integer page, Integer pageSize);

	/**
	 * 根据页面元素的标识验证是否已存在该标识的配置
	 *
	 * @param identifier
	 * @return true：已存在的页面元素配置；false ：不存在
	 */
	public boolean hasPageElementByIdentifier(String identifier, Integer type);

	/**
	 * 根据页面元素表示判断参与者是否对此页面元素拥有权限
	 *
	 * @param actorid
	 * @param identifier
	 * @return
	 */
	public boolean hasRightByActorIdDdentifier(Integer actorid, String identifier, Integer dataId, Integer type,
                                               AdminUserModel user);

	/**
	 * 根据登录用户来获取 用户所用有的pageElement
	 *
	 * @param actorId
	 * @return
	 */
	public List<String> getPageElementByActorId(Integer actorId,
                                                AdminUserModel user);

}
