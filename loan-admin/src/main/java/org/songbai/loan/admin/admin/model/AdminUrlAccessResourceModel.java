package org.songbai.loan.admin.admin.model;

/**
 * URL 访问权限资源 表示请求服务器端的URL
 * 
 * @author wangd
 *
 */
public class AdminUrlAccessResourceModel extends AdminSecurityResourceModel {

    private static final long serialVersionUID = -3915060741060773260L;

    
    public static final String CATEGORY = "URL_ACCESS";
    
    public AdminUrlAccessResourceModel() {
	this.setCategory(CATEGORY);

    }

    
    
}
