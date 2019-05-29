package org.songbai.loan.admin.admin.model;

import java.io.Serializable;

/**
 * 菜单关系数据
 * 与权限资源{@link AdminSecurityResourceModel} 中的菜单类型的数据结合表示菜单的层级关系
 * @author wangd
 *
 */
public class AdminMenuRelationModel implements Serializable{

    private static final long serialVersionUID = -7875575229527376695L;
    private Integer id;
    private Integer parentId;
    private Integer childId;
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getParentId() {
        return parentId;
    }
    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
    public Integer getChildId() {
        return childId;
    }
    public void setChildId(Integer childId) {
        this.childId = childId;
    }
    
    
}
