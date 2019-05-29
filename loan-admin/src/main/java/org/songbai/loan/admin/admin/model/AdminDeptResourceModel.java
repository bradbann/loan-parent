package org.songbai.loan.admin.admin.model;


import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

@Data
@TableName("dream_a_department_resource")
public class AdminDeptResourceModel {

    private Integer id;
    private Integer deptId;
    /**
     * 权限资源Id
     */
    private Integer resourceId;
    /**
     * 数据id
     */
    private Integer agencyId;
    private Integer type ; //0-部门，1-代理admin

}
