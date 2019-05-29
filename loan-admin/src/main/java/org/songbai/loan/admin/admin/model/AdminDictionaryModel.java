package org.songbai.loan.admin.admin.model;

import java.io.Serializable;
import java.util.Date;

public class AdminDictionaryModel implements Serializable {

	private static final long serialVersionUID = 7031755094198683537L;
	private Integer id; // int(11) NOT NULL COMMENT '主键',
	private String type; // varchar(255) NOT NULL COMMENT '类型',
	private String name; // varchar(255) NOT NULL COMMENT '名称',
	private String code; // varchar(255) NOT NULL COMMENT '代码',
	private String value; // int(11) NOT NULL COMMENT '取值',
	private String comments; // varchar(255) NOT NULL COMMENT '备注',
	private String createUser; // varchar(255) NOT NULL COMMENT '创建用户',
	private String updateUser; // varchar(255) NOT NULL COMMENT '更新用户',
	private Date createTime; // timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
	private Date updateTime; // timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON
								// UPDATE CURRENT_TIMESTAMP,
	private Integer status; // int(11) NOT NULL DEFAULT '0' COMMENT '0可用,1不可用'
//	private String createTimeStart;
//	private String createTimeEnd;
//	private String updateTimeStart;
//	private String updateTimeEnd;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

}
