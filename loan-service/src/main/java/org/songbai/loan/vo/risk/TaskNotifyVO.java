package org.songbai.loan.vo.risk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskNotifyVO {

    String userId; // 用户ID
    String taskId; // 任务ID

    /**
     * @link {}
     */
    String sources; // 来源


    /**
     * @link { RiskConst.Task }
     */
    Integer status; // 认证状态 1， 提交成功，2认证失败， 3

}
