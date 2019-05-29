package org.songbai.loan.model.finance;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

import lombok.Data;

/**
 * 区域model
 * @author wjl
 * @date 2018年11月09日 10:58:19
 * @description
 */
@Data
@TableName("loan_u_area")
public class AreaModel {
	
	@TableId
	private Integer id;
	private String cName;//名字
	private String pId;//省id
}
