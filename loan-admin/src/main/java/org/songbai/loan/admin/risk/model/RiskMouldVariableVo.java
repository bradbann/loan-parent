package org.songbai.loan.admin.risk.model;

import lombok.Data;
import org.songbai.loan.risk.model.mould.RiskMouldVariableModel;

import java.util.List;

/**
 * Created by mr.czh on 2018/11/15.
 */
@Data
public class RiskMouldVariableVo{

    private Integer cataLg;
    private String variableCode;
    private List<RiskMouldVariableModel> list;


}
