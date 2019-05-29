package org.songbai.loan.admin.admin.model;

/**
 * 页面元素权限资源，
 * 用于页面上的元素（如按钮Button，标题title等）
 * 理论上适用于页面上的任何元素（隐藏元素不考虑在内）
 * @author wangd
 *
 */
public class AdminPageElementResourceModel extends AdminSecurityResourceModel {

    private static final long serialVersionUID = -712640336398693169L;
    
    public static final String CATEGORY = "PAGE_ELEMENT";
    
    public AdminPageElementResourceModel() {
        this.setCategory(CATEGORY);
    }
    /**
     * 资源标识
     */
    private String identifier;

    
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    
    
}
