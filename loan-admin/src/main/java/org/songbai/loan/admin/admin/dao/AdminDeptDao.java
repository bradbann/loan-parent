package org.songbai.loan.admin.admin.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.admin.model.AdminDeptModel;
import org.songbai.loan.admin.admin.model.AdminRoleModel;
import org.songbai.loan.admin.admin.model.vo.AdminDeptVO;

import java.util.List;

public interface AdminDeptDao extends BaseMapper<AdminDeptModel> {
    Integer selectUserCountById(@Param("deptId") Integer deptId);

    List<AdminDeptVO> findDeptPage(@Param("limit") Integer limit, @Param("pageSize") Integer pageSize,
                                   @Param("agencyId") Integer agencyId, @Param("deptCode") String deptCode,
                                   @Param("deptId") Integer deptId);

    /**
     * 获取默认部门
     * deptType = 2,3,4 信审 财务 催收
     *
     * @return
     */
    List<AdminDeptModel> queryDefaultDeptList();

    List<Integer> findDeptIdByParentId(@Param("deptId") Integer deptId);

    List<AdminDeptModel> findDeptListByParentId(@Param("deptId") Integer deptId);

    List<AdminDeptModel> findDeptListByDeptCode(@Param("deptCode") String deptCode, @Param("agencyId") Integer agencyId,
                                                @Param("deptType") Integer deptType);

    Integer getDeptPageCount(@Param("deptCode") String deptCode, @Param("agencyId") Integer agencyId, @Param("deptId") Integer deptId);

    AdminDeptModel getDeptByParentId(@Param("parentId") Integer parentId);

    List<AdminRoleModel> findDeptRoleList(@Param("deptId") Integer deptId, @Param("agencyId") Integer agencyId);
}
