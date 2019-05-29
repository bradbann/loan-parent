package org.songbai.loan.risk.vo;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VariableExtractVO {

    private String userId;
    private String sources;


    // 扩展使用的字段
    private String taskId;
    private String orderNumber;
}
