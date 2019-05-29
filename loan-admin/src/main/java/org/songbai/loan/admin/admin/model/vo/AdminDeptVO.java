package org.songbai.loan.admin.admin.model.vo;

import com.baomidou.mybatisplus.annotations.TableField;
import lombok.Data;
import org.songbai.loan.admin.admin.model.AdminDeptModel;

import java.util.List;


@Data
public class AdminDeptVO {

    Integer id;
    String name;
    Integer parentId;
    Integer agencyId;
    String agencyName;
    Integer deptType;//部门类型，1-普通，2-审批，3-财务，4-催收
    Integer ministerId;//部长actorId
    String ministerName;
    Integer createId;//创建人id
    Integer deptLevel; // 部门级别,越大级别越低
    String parentDeptName;

    @TableField(exist = false)
    List<AdminDeptModel> childDept;

}
