package org.songbai.loan.risk.vo;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskResultVO {

    public static final int CODE_FAIL = 0 ;
    public static final int CODE_SUCESS = 1 ;
    public static final int CODE_REPEAT = 2 ;
    public static final int CODE_WAITDATA = 3 ;

    private Integer code; // 0 失败 ,1 成功， 2,重复数据，3，等待数据
    private String msg; // 备注

    /**
     * @link RiskConst.Result
     */
    private Integer riskResult; // 结果， 0
    private Integer[] riskResultList;
    private List<String> riskResultMsg;
    private Integer scoring; // 风控得分



    private String userId;
    private String orderNumber;//订单号
    private Integer mouldId;



}
