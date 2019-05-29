package org.songbai.loan.constant;

public final class JmsDest {


    public static final String SMS_SENT = "queue://loan.user.sendMsgCode";
    public static final String SMS_VOICE_SENT = "queue://loan.user.voice.sendMsgCode";

    public static final String SMS_TEMPLATE_UPDATE = "queue://sms.template.update";

    public static final String SCHEDULE_TIMED_TASK_CHANGEND = "queue://timedTaskChangendJsm";

    public static final String MSG_SEND = "queue://msg:send";

    public static final String CREATE_USER_INFO = "queue://user.create.info";// 用户注册成功,创建用户信息表和用户认证表

    public static final String INSERT_DEVICE = "queue://user.device.insert";//插入用户的设备id


    public static final String RISK_ORDER_MOULD = "queue://risk.user.order";

    public static final String RISK_ORDER_RESULT = "queue://risk.user.order.result";

    public static final String RISK_DATA_NOTIFY = "queue://risk.data.notify";

    public static final String SCHEDULE_ORDER_WAITDATA = "queue://schedule.order.waitdata";

    //回调补偿
    public static final String FINANCE_PAY_QUERY = "queue://finance.pay.query";
    public static final String FINANCE_REPAY_QUERY = "queue://finance.repay.query";
    public static final String JH_AUTO_QUERY = "queue://finance.repay.auto.query";//聚合支付自动查询订单

    //-------催收
    public static final String CHASE_ORDER_STATUS = "queue://chase.order.status";
    //推送
    public static final String LOAN_PUSH_MSG = "queue://user.push.msg";
    public static final String LOAN_PUSH_GROUP_MSG = "queue://user.push.group.msg";

    //认证
    public static final String AUTH_REMAINDAYS = "queue://user.auth.remainDays";

    //黑名单定时任务
    public static final String UPDATE_BLACKLIST = "queue://user.update.blackList";

    //支付平台配置
    public static final String PAYPLATFORM_CONFIG = "topic://finance.payplatform.config";

    //自动放款
    public static final String AUTO_TRANSFER = "queue://finance.auto.transfer";

    //一键代扣
    public static final String AUTO_REPAYMENT = "queue://finance.auto.repayment";
    public static final String AUTO_DEDUCT = "queue://finance.auto.deduct";


    //用户信息统计
    public static final String USER_STATISTIC_RISK = "queue://user.statistic.risk";

    public static final String PUSH_ORDER_REPAY_REMIND = "topic://push.order.repay.remind";//今日还款提醒
    public static final String PUSH_ORDER_REPAY_REMIND_TOMORROW = "queue://order.tomorrow.repay.remind";//明日还款提醒
    public static final String PUSH_ORDER_OVERDUE = "queue://user.statistic.risk";// 逾期提醒

    public static final String ZERO_JMS_TOPIC = "topic://admin.zero.jms";//每天晚上12点jms消息


    //复审人员统计
//    public static final String ORDER_ACTOR_STATISTIC = "queue://statis.actor.review.statisc";
    public static final String ORDER_EXPIRE_STATIS = "queue://statis.order.expire";//超期订单统计
    public static final String ORDER_REPAY_STATISTIC = "queue://order.repay.statistic";//还款统计,应在计算订单逾期之后统计
    public static final String USER_STATISTIC = "queue://admin.user.statistic";//用户统计

    public static final String ORDER_CONFIRM_OPT = "queue://order.confirm.opt";//还款/逾期/减免/坏账
    public static final String ORDER_LOAN_PAY = "queue://order.loan.pay";//财务放款

    public static final String CHANNEL_STATIS = "queue://statis.channel.statis";//渠道统计
    public static final String REVIEW_STATIS = "queue://statis.review.statis";//信审统计
    public static final String ACTOR_REVIEW_STATIS = "queue://statis.actor.review.statis";//信审人员统计
    public static final String CHANNEL_DEDUCTION_STATIS = "queue://statis.channel.deduction:statis";//渠道扣量统计

    // 同步泡泡云上行短信
    public static final String MSG_SYNC_UP = "queue://msg.sync.up";
    // 外呼
    public static final String MSG_CALL_REPAY = "queue://msg.call.repay";
}
