package org.songbai.loan.admin.admin.model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * 参与者， 它是一个抽象的概念。
 * 是{@link AdminUserModel} 的基类
 * 后期可能会添加用户组，将不同部门的用户分成一组实现方便的用户管理，如特殊权限的范围授权、消息、邮件群发等功能
 * 可以对该类进行扩展，以达到所希望的业务
 * 可以对该类授予角色 {@link AdminRoleModel} 和权限 {@link AdminPerssionModel}
 *
 * @author wangd
 */
public class AdminActorModel implements Serializable {

    private static final long serialVersionUID = -3993237169611436703L;
    /**
     * id
     */
    private Integer id;
    /**
     * 名称
     */
    private String name;

//    /**
//     * 最后修改时间
//     */
//    private Timestamp lastModifyTime;

    /**
     * 创建者
     */
    private Integer createOwnerId;

    /**
     * 创建者姓名
     */
    private String createOwner;
    /**
     * 创建时间
     */
    private Timestamp createDate;

    /**
     * 描述
     */
    private String description;
    /**
     *
     */
    private String category;

    /**
     * 是否禁用
     */
    private boolean disable = false;
    /**
     * 用户登录账号
     */
    private String userAccount;
    /**
     * 最后登录时间
     */
    private Timestamp lastLoginTime;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 联系方式（手机）
     */
    private String phone;

    /**
     * 管理员头像
     */
    private String userPortrait;

    private Integer deptId;
    private String deptName;
    private Integer isManager;
    private Integer isValidate;

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

    public Integer getCreateOwnerId() {
        return createOwnerId;
    }

    public void setCreateOwnerId(Integer createOwnerId) {
        this.createOwnerId = createOwnerId;
    }

    public String getCreateOwner() {
        return createOwner;
    }

    public void setCreateOwner(String createOwner) {
        this.createOwner = createOwner;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
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

    public boolean isDisable() {
        return disable;
    }

    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public String getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(String userAccount) {
        this.userAccount = userAccount;
    }

    public Timestamp getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public Integer getDeptId() {
        return deptId;
    }

    public void setDeptId(Integer deptId) {
        this.deptId = deptId;
    }

    public String getDeptName() {
        return deptName;
    }

    public Integer getIsManager() {
        return isManager;
    }

    public void setIsManager(Integer isManager) {
        this.isManager = isManager;
    }

    public void setDeptName(String deptName) {
        this.deptName = deptName;
    }

    public Integer getIsValidate() {
        return isValidate;
    }

    public void setIsValidate(Integer isValidate) {
        this.isValidate = isValidate;
    }
}
