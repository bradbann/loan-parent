package org.songbai.loan.admin.admin.model;

import java.io.Serializable;

/**
 * 
 * @author SUNDONG_
 *	角色权限关系。
 *  权限资源{@link AdminSecurityResourceModel} 中的权限{@link AdminPerssionModel}和角色{@link AdminRoleModel}的关系
 */
public class AdminRolePermissionsModel implements Serializable {
    
    private static final long serialVersionUID = -4311775524180596047L;
    private Integer id;
    /**
     * 权限Id
     */
	Integer	permissionId;
	/**
	 * 角色Id
	 */
	Integer	roleId;
	

	public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRoleId()
	{
		return roleId;
	}
	
	public void setRoleId(Integer roleId)
	{
		this.roleId=roleId;
	}

    public Integer getPermissionId() {
        return permissionId;
    }

    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }

}
