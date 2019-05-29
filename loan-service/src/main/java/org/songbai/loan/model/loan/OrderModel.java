package org.songbai.loan.model.loan;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/10/30 下午4:36
 */
@Data
@TableName("loan_u_order")
public class OrderModel {
    private Integer id;
    private String orderNumber;//订单号
    private Integer productId;//标的id
    private Integer groupId;//标的分组id
    private Integer agencyId;//代理id
    private Integer userId;//用户id
    private Integer actorId;//操作人id

    private Integer stage;//阶段
    private Integer days;//借款期限
    private Integer status;//状态
    private Integer authStatus;//后台辅助状态(仅取单/退单)
    private Double loan;//借款金额
    private Double stampTax;//综合费
    private Double obtain;//实际到账金额(应打款)

    private Integer exceedDays;//逾期天数
    private Double exceedFee;//逾期费用

    private Integer bankId;//打款银行卡id

    private Double payment;//应还金额
    private Double alreadyMoney;//已还金额
    private Double deductMoney;//减免金额

    private Double chargingMoney;// 自动扣款金额

    private Date againDate;//再次下单时间,(拒绝放款时使用)
    private Date createTime;//下单时间
    private Date transferTime;//到账时间
    private Date repaymentDate;//还款日期
    private Date repaymentTime;//实际还款时间
    private Date optTime;//操作时间
    private Integer reviewId;//复审人
    private Date reviewTime;//复审时间

    private Integer guest;//客群
    private String remark;
    private Date updateTime;

    private Integer chaseDeptId;//催收部门id
    private Integer chaseActorId;//催收人id
    private String chaseId;//催收单号
    private Date chaseDate;//催收开始时间
    private Integer lastFeedType;//最后一次催收类型

}
