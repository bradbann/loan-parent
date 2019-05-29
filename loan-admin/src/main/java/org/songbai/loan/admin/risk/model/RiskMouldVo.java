package org.songbai.loan.admin.risk.model;

/**
 * Created by mr.czh on 2018/11/6.
 */
public class RiskMouldVo {

    private Integer id;

    private String name;

    private Integer status;

    private Integer agencyId;

    private Integer defaultSore;

    private Integer scoreType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(Integer agencyId) {
        this.agencyId = agencyId;
    }

    public Integer getDefaultSore() {
        return defaultSore;
    }

    public void setDefaultSore(Integer defaultSore) {
        this.defaultSore = defaultSore;
    }

    public Integer getScoreType() {
        return scoreType;
    }

    public void setScoreType(Integer scoreType) {
        this.scoreType = scoreType;
    }
}
