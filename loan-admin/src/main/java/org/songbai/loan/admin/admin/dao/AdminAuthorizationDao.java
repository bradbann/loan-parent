package org.songbai.loan.admin.admin.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.admin.model.AdminAuthorizationModel;

import java.util.List;

/**
 * 授权中心DAO
 *
 * @author wangd
 */
public interface AdminAuthorizationDao {

    public void createAuthorization(AdminAuthorizationModel adminAuthorizationMoudel);

    public void updatePermission(AdminAuthorizationModel adminAuthorizationMoudel);

    /**
     * 根据参与者id或者授权id删除记录 该接口可以由下面的多条删除接口替代，留着是为了传值方便
     *
     * @param actorId
     * @param authorityId
     * @param dataId
     */
    public void deleteAuthorization(@Param(value = "actorId") Integer actorId,
                                    @Param(value = "authorityId") Integer authorityId, @Param(value = "dataId") Integer dataId);

    /**
     * 根据参与者Id或授权id获得授权纪录
     *
     * @param actorId
     * @param authorityId
     * @return
     */
    public List<AdminAuthorizationModel> pagingQueryPermissions(@Param(value = "actorId") Integer actorId,
                                                                @Param(value = "authorityId") Integer authorityId, @Param(value = "dataId") Integer dataId);

    /**
     * 根据参与者id和授权id批量撤销授权
     *
     * @param actorId
     * @param authorityIds
     */
    public void deleteAuthorizationByActorIdAuthorityIds(@Param(value = "actorId") Integer actorId,
                                                         @Param(value = "authorityIds") List<Integer> authorityIds, @Param(value = "dataId") Integer dataId);

    /**
     *
     * @param deptId 部门id
     * @param authorityIds 授权id
     * @param dataId 渠道
     */
    void deleteAuthorizationByDeptIdAuthorityIds(@Param("deptId") Integer deptId, @Param("authorityIds") List<Integer> authorityIds,
                                                 @Param("dataId") Integer dataId);
}
