package org.songbai.loan.admin.admin.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 权限资源， 它是菜单资源{@link AdminMenuModel}、页面元素资源
 * {@link AdminPageElementResourceModel}、URL访问
 * {@link AdminUrlAccessResourceModel} 的基类
 * 它是所有纳入系统管理的信息实体，如订单，页面上的任何一个元素，菜单功能，方法调用，权限资源，就这些。
 * 
 * @author wangd
 *
 */
public class AdminSecurityResourceModel implements Serializable {

	private static final long serialVersionUID = -5635129397039177303L;
	/**
	 * 业务ID
	 */
	Integer id;
	/**
	 * 添加时间
	 */
	private Timestamp createTime;
	/**
	 * 最后修改时间
	 */
	private Timestamp updateTime;

	/**
	 * 名称
	 */
	private String name;

	/**
	 * 描述
	 */
	private String description;
	/**
	 * 资源类型 菜单、URL、页面元素、方法（后实）等
	 */
	private String category;

	/**
	 * 父级ID号 数据类型为菜单时为上级菜单的id，其他类型的数据时改id为所属菜单id
	 */
	private Integer parentId;
	/**
	 * 父菜单名称（数据类型为菜单时） 所属菜单名称（数据类型为页面元素和URL访问地址）
	 */
	private String parentName;
	/**
	 * 是否叶子菜单
	 */
	private Boolean isLeaf = true;
	/**
	 * 0：表示平台独有 1：表示一级渠道、平台可用 2：表示二级、一级渠道、平台可用
	 */
	private Integer isAdmin;

	/**
	 * 是否已分配给角色 数据标识，表中不含有该属性对应的字段
	 */
	private boolean checked = false;

	/**
	 * 平台或者各个渠道权限 0表示平台 1表示一级渠道 2表示二级渠道
	 */
	private Integer type;

	/**
	 * 菜单 URL Page 地址
	 */
	private String url;
	
	private String identifier;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public Timestamp getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Timestamp updateTime) {
		this.updateTime = updateTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Integer getParentId() {
		return parentId;
	}

	public void setParentId(Integer parentId) {
		this.parentId = parentId;
	}

	public Boolean getIsLeaf() {
		return isLeaf;
	}

	public void setIsLeaf(Boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public String getParentName() {
		return parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public Integer getIsAdmin() {
		return isAdmin;
	}

	public void setIsAdmin(Integer isAdmin) {
		this.isAdmin = isAdmin;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}
