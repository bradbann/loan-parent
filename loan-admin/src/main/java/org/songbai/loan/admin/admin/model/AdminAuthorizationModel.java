package org.songbai.loan.admin.admin.model;

import java.io.Serializable;

/**
 * 授权中心，
 * 将授权{@link AdminAuthorityModel} 授权给参与者{@link AdminActorModel}
 * 后期可能会考虑授权的范围问题，如在指定的组织机构下。
 * @author wangd
 *
 */
public class AdminAuthorizationModel implements Serializable{

    private static final long serialVersionUID = 3653123952079783915L;

    private Integer id;
    /**
     * 参与者Id，即用户的Id
     */
    private Integer actorId;
    /**
     * 授权Id多为角色{@link AdminRoleModel}Id
     */
    private Integer authorityId;
    /**
     * 范围Id暂时不做考虑
     */
    private Integer scopeId;
    
    /**
     * 数据id，即登录人的渠道id
     */
    private Integer dataId;

    private Integer deptId;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    
    public Integer getActorId() {
        return actorId;
    }
    public void setActorId(Integer actorId) {
        this.actorId = actorId;
    }
    public Integer getAuthorityId() {
        return authorityId;
    }
    public void setAuthorityId(Integer authorityId) {
        this.authorityId = authorityId;
    }
    public Integer getScopeId() {
        return scopeId;
    }
    public void setScopeId(Integer scopeId) {
        this.scopeId = scopeId;
    }
    public Integer getDataId() {
        return dataId;
    }
    public void setDataId(Integer dataId) {
        this.dataId = dataId;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }
}
