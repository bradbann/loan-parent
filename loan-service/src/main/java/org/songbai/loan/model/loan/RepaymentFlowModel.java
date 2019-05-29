package org.songbai.loan.model.loan;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/11/7 8:09 PM
 */
@Data
@TableName("loan_a_repayment_flow")
public class RepaymentFlowModel {
    private Integer id;
    private String orderNumber;//订单号
    private String repaymentNumber;//还款单号
    private Integer agencyId;//代理id
    private Integer userId;//用户id
    private Integer actorId;//操作人id

    private String username;//用户昵称
    private String phone;//手机号

    private String repayType;//还款类型 支付宝,微信等
    private Integer type;//1线上还款,2线下还款,3自动扣款
    private Integer repayStatus;//还款状态 2正常还款,5逾期还款 6提前还款, 7催收还款

    private Double money;//还款金额
    private Double payment;//还款金额
    private String payChannel;//支付渠道昵称

    private Double deductMoney;//减免金额
    private Double exceedFee;//逾期费用
    private Integer exceedDays;//逾期天数
    private Double loan;//借款金额
    private Double obtain;//实际到账金额(应打款)
    private Double autoRepayment;//代扣金额

    private String bankName;// 银卡昵称
    private String bankNumber;// 卡号

    private String receipt;// 凭证

    private String remark;// 备注
    private Date repaymentDate;//应该还款日期
    private Date repaymentTime;// 还款时间
    private Date paymentTime;// 放款时间

    private Date createTime;
}
