package org.songbai.loan.admin.admin.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.admin.model.AdminDeptResourceModel;
import org.songbai.loan.admin.admin.model.AdminMenuResourceModel;

import java.util.List;

public interface AdminDeptResourceDao extends BaseMapper<AdminDeptResourceModel> {


    void deleteResourceByDeptId(@Param("deptId") Integer deptId);

    void createAdminDeptResource(@Param("list") List<AdminDeptResourceModel> list);

    List<Integer> getResourceAssignmentIdsByDeptId(@Param("agencyId") Integer agencyId, @Param("deptId") Integer deptId);

    AdminDeptResourceModel getInfoByDeptIdAndId(@Param("resourceId") Integer resourceId, @Param("deptId") Integer deptId,
                                                @Param("type") Integer type, @Param("agencyId") Integer agencyId);

    List<AdminMenuResourceModel> findResourceByParentId(@Param("parentId") Integer id, @Param("agencyId") Integer agencyId,
                                                        @Param("deptId") Integer deptId, @Param("type") int type,
                                                        @Param("category") String category);
}
