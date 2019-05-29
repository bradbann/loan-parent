package org.songbai.loan.model.loan;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @author: wjl
 * @date: 2019/1/4 16:33
 * Description: 代扣记录Model
 */
@Data
@TableName("loan_a_repayment_auto_flow")
public class RepaymentAutoFlowModel {

	private Integer id;
	private Integer userId;
	private Integer agencyId;
	private Integer actorId;//操作人id
	private String orderNumber;//订单号
	private String autoRepaymentNumber;//代扣订单号
	private String name;//姓名
	private String phone;//手机号
	private Integer channelId;//渠道id
	private Integer vestId;//马甲id
	private Double payment;//应还金额
	private Double autoRePayment;//本次代扣金额
	private Integer status;//代扣状态 0进行中 1成功 2失败
	private String payPlatform;//付款平台名字
	private String bankName;//银行名字
	private String bankCardNum;//银行卡号
	private String remark;//备注
	private Date repaymentDate;//应还日期
	private Date autoRepaymentTime;//代扣时间
	private Date createTime;
	private Date updateTime;
}
