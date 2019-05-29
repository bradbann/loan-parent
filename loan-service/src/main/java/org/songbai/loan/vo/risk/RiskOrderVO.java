package org.songbai.loan.vo.risk;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RiskOrderVO {


    private String thridId;
    private String orderNumber;
}
