package org.songbai.loan.admin.admin.model;

/**
 * 角色是权限的集合，它可以是逻辑上的权限{@link AdminPerssionModel},也可以是物理上的权限
 * {@link AdminSecurityResourceModel} 它代表一系列可执行的操作或责任，因此它是授权的粗粒度表现
 * 
 * @author SUNDONG_ 管理员角色数据
 */
public class AdminRoleModel extends AdminAuthorityModel {
	private static final long serialVersionUID = 1L;
	public static final String CATEGORY = "role";

	public AdminRoleModel() {
		this.setCategory(CATEGORY);
	}

}
