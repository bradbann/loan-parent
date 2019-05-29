package org.songbai.loan.user.user.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: wjl
 * @date: 2018/11/19 13:31
 * Description: 为前台返回地区列表
 */
@Data
public class AreaVo implements Serializable {
	private Integer id;
	private String name;
	private Integer pid;
}
