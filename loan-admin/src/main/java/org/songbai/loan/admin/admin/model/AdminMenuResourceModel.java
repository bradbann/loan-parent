package org.songbai.loan.admin.admin.model;

import java.util.List;

/**
 * 菜单权限资源，系统提供的功能入口
 * @author wangd
 *
 */
/**
 * @author wangd
 *
 */

/**
 * @author wangd
 *
 */
public class AdminMenuResourceModel extends AdminSecurityResourceModel {

    private static final long serialVersionUID = 119126976312987596L;

    private Integer id;
    /**
     * 菜单图标
     */
    private String menuIcon;
    
    /**
     * 菜单层级
     */
    private Integer level = 0;
    
    /**
     * 菜单排序
     */
    private Integer position = 0;
    
 
    
    /**
     * 父菜单id（表中不含有该字段）
     */
    private Integer parentId;
    
    
    
    /**
     * 子菜单节点
     */
    
    private List<AdminMenuResourceModel> children;
    
    /**
     * 与菜单相关的页面元素
     */
    private List<? extends AdminSecurityResourceModel> pageElements;
    
    /**
     * 与菜单相关的URL访问方法
     */
    private List<? extends AdminSecurityResourceModel> urlAccess;
    
    /**
     * 与菜单相关联的消息弹窗
     */
    private List<? extends AdminSecurityResourceModel> popup;
    
    
    public static final String CATEGORY = "MENU";
    
    /**
     * 菜单状态
     * 
     */
    private Integer status;

    /**
     * 菜单编码
     */
    private String code;

    public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public String getMenuIcon() {
        return menuIcon;
    }

    public void setMenuIcon(String menuIcon) {
        this.menuIcon = menuIcon;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }


    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }


    public List<AdminMenuResourceModel> getChildren() {
        return children;
    }

    public void setChildren(List<AdminMenuResourceModel> children) {
        this.children = children;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

	public List<? extends AdminSecurityResourceModel> getPageElements() {
		return pageElements;
	}

	public void setPageElements(List<? extends AdminSecurityResourceModel> pageElements) {
		this.pageElements = pageElements;
	}

	public List<? extends AdminSecurityResourceModel> getUrlAccess() {
		return urlAccess;
	}

	public void setUrlAccess(List<? extends AdminSecurityResourceModel> urlAccess) {
		this.urlAccess = urlAccess;
	}

	public List<? extends AdminSecurityResourceModel> getPopup() {
		return popup;
	}

	public void setPopup(List<? extends AdminSecurityResourceModel> popup) {
		this.popup = popup;
	}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
