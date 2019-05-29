package org.songbai.loan.admin.admin.model;

/**
 * 权限是一个抽象的概念，代表一项操作或责任，因此是授权的细粒度表示
 * 
 * @author wangd
 *
 */
public class AdminPerssionModel extends AdminAuthorityModel {

	private static final long serialVersionUID = -378650414418606907L;

	public static final String CATEGORY = "PERMISSION";
	/**
	 * 权限标识符
	 */
	private String identifier;

	/**
	 * 自带标识
	 */
	public AdminPerssionModel() {
		this.setCategory(CATEGORY);
	}

	/**
	 * 权限层级编码
	 */
	private String levelCode;

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}

}
