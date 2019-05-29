package org.songbai.loan.admin.admin.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName("dream_a_department")
public class AdminDeptModel {
    Integer id;
    String name;
    Integer parentId;
    Integer agencyId;
    Date createTime;
    Date updateTime;
    Integer deptType;//部门类型，1-普通，2-审批，3-财务，4-催收
//    Integer ministerId;//部长actorId
    Integer createId;//创建人id
    Integer deptLevel;
    String deptCode;
    @TableField(exist = false)
    List<AdminDeptModel> childDept;
}
