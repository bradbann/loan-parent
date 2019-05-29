package org.songbai.loan.admin.admin.dao;

import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.admin.model.AdminResourceAssignmentModel;
import org.songbai.loan.admin.admin.model.AdminSecurityResourceModel;

import java.util.List;
import java.util.Set;

/**
 * 权限资源分配Dao
 * @author wangd
 *
 */
public interface AdminResourceAssignmentDao {

    /**
     * 保存权限资源分配纪录
     */
    public void creatAdminResourceAssignment(List<AdminResourceAssignmentModel> list);
   
    /**
     * 删除权限资源分配纪录
     * @param authorityId
     * @param securityResourceId
     * @param dataId
     */
    public void deleteAdminResourceAssignment(@Param(value = "authorityId") Integer authorityId, @Param(value = "securityResourceId") Integer securityResourceId, @Param(value = "dataId") Integer dataId);

    /**
     * 根据参与者Id批量删除该为参与者分配的权限资源纪录
     * @param authorityId
     * @param securityResourceIds
     * @param dataId
     */
    public void deleteAdminResourceAssignments(@Param(value = "authorityId") Integer authorityId, @Param(value = "securityResourceIds") List<Integer> securityResourceIds, @Param(value = "dataId") Integer dataId);

    /**
     * 根据角色Id、权限资源类型和数据类型获得该数据类型下的分配纪录
     * @param roleId
     * @param category
     * @param dataId
     * @return
     */
    public List<Integer> getAdminResourceAssignmentIdsByRoleIdCategory(@Param("roleId") Integer roleId, @Param("category") String category, @Param(value = "dataId") Integer dataId);

    /**
     * 根据Id批量删除纪录
     * @param ids
     */
    public void deleteAdminResourceAssignmentsByIds(@Param(value = "ids") List<Integer> ids);

    /**
     * 根据授权Id删除分配纪录
     * @param authorityId
     */
    public void deleteAdminResourceAssignmentsByAuthorityId(@Param(value = "authorityId") Integer authorityId);

    /**
     * 根据数据id、资源id和角色id获得一条记录
     * @param dataId
     * @param authorityId
     * @param resourceId
     * @return
     */
    public AdminResourceAssignmentModel getByDataIdAthourIdResourceId(@Param(value = "dataId") Integer dataId, @Param(value = "authorityId") Integer authorityId, @Param(value = "resourceId") Integer resourceId);

    /**
     * 根据角色Id、数据类型获得该数据类型下的分配纪录
     * @param dataId
     * @param authorityId
     * @return
     */
    public List<Integer> getAdminResourceAssignmentIdsByRoleId(@Param(value = "dataId") Integer dataId, @Param(value = "authorityId") Integer authorityId);

    /**
     * 删除部门菜单
     * @param deptId
     */
    void deleteAdminResourceAssignmentsByDeptId(@Param("deptId") Integer deptId);

    List<Integer> getAdminResourceAssignmentIdsByDeptId(@Param("dataId") Integer dataId,@Param("deptId") Integer deptId);

    Set<Integer> findResourceByAuthorityId(@Param("authorityId") Integer authorityId);

    List<AdminSecurityResourceModel> getAllByAgencyIdAndCategory(@Param("category") String category,@Param("dataId") Integer dataId);
}
