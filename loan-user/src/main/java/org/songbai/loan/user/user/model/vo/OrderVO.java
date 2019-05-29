package org.songbai.loan.user.user.model.vo;

import lombok.Data;
import org.songbai.loan.model.loan.OrderModel;

/**
 * @author: wjl
 * @date: 2019/1/3 20:38
 * Description:
 */
@Data
public class OrderVO extends OrderModel {
	private String name;
	private String bankPhone;
	private String idCardNum;
	private String bankCardNum;
	private String bindPlatform;
}
