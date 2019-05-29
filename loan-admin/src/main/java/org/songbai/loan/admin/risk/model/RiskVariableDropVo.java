package org.songbai.loan.admin.risk.model;

import java.io.Serializable;

/**
 * Created by mr.czh on 2018/11/7.
 */
public class RiskVariableDropVo implements Serializable{

    private String name;

    private Integer catalog;

    private String code;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCatalog() {
        return catalog;
    }

    public void setCatalog(Integer catalog) {
        this.catalog = catalog;
    }
}
