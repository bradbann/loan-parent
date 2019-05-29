package org.songbai.loan.admin.admin.service;

import org.apache.ibatis.annotations.Param;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminSecurityResourceModel;
import org.songbai.loan.admin.admin.model.AdminUrlAccessResourceModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * URL 访问资源权限
 *
 * @author wangd
 */
@Component
public interface AdminUrlAccessResourceService {

    void saveUrlAccess(AdminUrlAccessResourceModel accessResourceModel);

    void updateUrlAccess(AdminUrlAccessResourceModel accessResourceModel);

    void deleteUrlAccess(List<Integer> ids);

    Page<AdminUrlAccessResourceModel> pagingQueryPermissions(Integer page, Integer pageSize,
                                                             Map<String, Object> param);

    /**
     * 根据角色ID批量删除角色下的URL访问资源纪录
     *
     * @param roleId
     * @param urlAccessResourceIdList
     *            URL权限访问记录Id集合
     */
//	public void terminateUrlAccessResourcesFromRole(Integer roleId, List<Integer> urlAccessResourceIdList,
//			Integer dataId);

    /**
     * 根据url地址判断该地址是否已经存在， 适用于数据保存之前的验证和保存的时候的验证
     *
     * @param url
     * @return true : URL 地址已经存在； false： 不存在该URL地址
     */
    boolean hasUrlAccessByUrlAddress(String url, Integer type);

    /**
     * 根据参与者Id获得分配给参与者的URL资源
     *
     * @param actorId
     * @return
     */
    List<AdminUrlAccessResourceModel> getUrlAccessResourcesByActorId(Integer actorId, Integer dataId);

    List<AdminUrlAccessResourceModel> getUrlAccessByRoleId(Integer roleId, Integer dataId);

    /**
     * @param menuId
     * @param category
     * @param type
     * @return
     */
    List<AdminSecurityResourceModel> getAllByMenuId(Integer menuId, String category, Integer type);

    /**
     * 获取用户下的权限
     *
     * @param actorId
     * @param dataId
     * @return
     */
    List<AdminSecurityResourceModel> getSecurityResourcesByActorId(Integer actorId, Integer dataId,
                                                                   String category);

    List<AdminSecurityResourceModel> getAllByMenuIdByCategory(String category, Integer id);

    List<AdminSecurityResourceModel> getAllByMenuIdByCategoryForSuperMan(String category);

    boolean isHaveRoleForActor(Integer actorId, Integer[] authorityIds);

    List<AdminSecurityResourceModel> getAllByMenuIdByCategoryAndDeptId(String category, Integer deptId, Integer agencyId, int type);


}
