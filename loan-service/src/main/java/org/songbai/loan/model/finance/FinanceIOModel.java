package org.songbai.loan.model.finance;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import lombok.Data;

import java.util.Date;

/**
 * 资金操作表（流水表）
 * @author wjl
 * @date 2018年11月12日 16:33:18
 * @description
 */
@Data
@TableName("loan_u_finance_io")
public class FinanceIOModel {

	@TableId(type=IdType.AUTO)
	private Integer id;
	private Integer agencyId;
	private Integer userId;
	private String thirdUserId;//请求第三方的userId，用于和第三方对接时做唯一标识
	private String orderId;//内部订单id
	private String requestId;//请求第三方id，uuid形式，用于和第三方对接时做唯一标识
	private String thirdOrderId;//第三方平台通知过来的唯一标识
	private String payPlatform;//支付平台编码
	private Integer status;//状态（0 初始化，1 还、放款成功，2还、放款失败，3 等待短验，4拒绝放款）
	private Integer type;//-1 放款 1还款 2 自动扣款
	private String typeDetail;//出入金类型描述（财务打款，用户还款）
	private String bankCardNum;//用户银行卡
	private Double money;
	private String remark;
	private Integer payType;//0支付宝,1微信,2第三方,3在线
	private Integer operatorId;//放款人
	private Date operatorTime;//放款时间
	private Date createTime;
	private Date updateTime;
}
