package org.songbai.loan.constant.resp;

import org.songbai.cloud.basics.mvc.RespCode;

public class AdminRespCode extends RespCode {

    public static final int ACCESS_PINGTAI = 5001; // 该账号没有权限， 请平台联系管理员
    public static final int ACCESS_ADMIN = 5002; // 该账号没有权限， 请联系管理员
    public static final int ACCESS_ROLE = 5003; // 必须含有指定的角色， 请联系管理员
    public static final int ACCESS_FINANCE = 5004; // 必须财务账号操作
    public static final int MENU_CODE_EXISIT = 5005; // 菜单编号已被使用
    public static final int ACCESS_ONLY_AGENCY = 5006; // 平台用户不允许操作
    public static final int ACCESS_ONLY_COMMON = 5007;//管理员不允许做此操作
    public static final int ACCESS_NOT_BELONG_AGENCY = 5008;//该用户不属于本平台，不允许操作


    public static final int ARITCLE_CATEGORY_NOT_EXIST = 5101;

    public static final int AGENCY_ACCOUNT_EXIST = 5104;
    public static final int PERMISSION_DENIED = 5105;
    public static final int GROUP_EXIST = 5106;
    public static final int HAS_NOT_AGENCY = 5107;

    public static final int USER_BIND_EXCEPTION = 5117;
    public static final int USER_BIND_ALREADY = 5118;

    public static final int SYSTEM_NOT_BIND = 5121;
    public static final int STATUS_IS_VALID = 5122;
    public static final int USER_STATUS_EXCEPTION = 5128;

//    public static final int PHONE_IS_BLACK = 5226;
//    public static final int EMAIL_IS_BLACK = 5227;


    public static final int AGENCY_INFO_ERROR = 5201;
    public static final int PARAMETER_INPUT_ILLEGALITY = 5202;

    public static final int DOMAIN_DUPLICATION = 5204;
    public static final int RESPONSE_DATA_SIZE_LONG = 5205;
    public static final int AGENCY_USERID_BINGDING = 5206;

    public static final int REQUEST_FAST = 5208;
    public static final int PARAMETER_DATE_BEGIN_AND = 5209;
    public static final int ADMIN_RECHARGE_MAX_PRICE = 5216;

    public static final int PHONE_IS_BLACK = 5219;
    public static final int EMAIL_IS_BLACK = 5220;

    public static final int PHONE_EXISTS = 5228;
    public static final int EMAIL_IS_EXIST = 5229;
    public static final int DOMAIN_ERROR_INPUT = 5230;
    public static final int AGENCY_UPDATE_ALL = 5231;

    public static final int ADMIN_USER_NOT_NULL = 5304;
    public static final int DEPT_HAVE_USER = 5305;
    public static final int DEPT_NOT_EXISIT = 5306;
    public static final int AGENCY_AMOUNT_EXISIT = 5307;
    public static final int AGENCY_HOST_EXISIT = 5308;
    public static final int HOST_ILLEGAL = 5309;
    public static final int ORDER_NOT_EXISIT = 5310;
    public static final int ORDER_STATUS_IS_CHANGE = 5311;//订单状态已修改
    public static final int DEPT_NOT_DELETE = 5312; //特殊部门不允许删除
    public static final int REPAY_ORDER_NOT_EXIST = 5313; //还款订单不存在
    public static final int DEDUCT_LESS_LOAN = 5314; //减免金额不能大于待还金额
    public static final int REPAY_TIME_ERROR = 5315; //还款时间不能小于放款时间
    public static final int REPAY_MONEY_WRONG = 5316; //还款金额不正确

    public static final int DEPT_HAVA_MANAGER = 5317;//该部门已存在管理员
    public static final int MONEY_THAN_ZERO = 5318;//金额应大于0
    public static final int USER_NOT_REVIEW_ORDER = 5319;//该用户下无可退订单
    public static final int NOT_HAVE_ORDER = 5320;//暂无订单记录
    public static final int PUSH_TEMPLATE_NOT_EXIST = 5321;//未找到该类型推送模板
    public static final int CHANNEL_CODE_IS_EXISIT = 5322;//渠道编号已存在
    public static final int CHANNEL_IS_NOT_EXISIT = 5323;//渠道不存在
    public static final int ORDER_NOT_LOAN = 5324;//订单不是放款状态
    public static final int ORDER_HAS_SUBMIT = 5325;//订单已提交
    public static final int PLATFORM_NOT_USE = 5326;//该支付平台未启用
    public static final int PLATFORM_NOT_CONFIG = 5327;//未配置相关秘钥
    public static final int MONEY_NOT_ENOUGH = 5328;//账户余额不足
    public static final int INTERNET_ERROR = 5329;//网络异常
    public static final int ORDER_STATUS_ERROR = 5330;//订单状态不正确
    public static final int USER_NOT_EXISIT = 5331;//该用户不存在
    public static final int VERIFY_SIGN_ERROR = 5332;//放款失败,签名出错，请联系技术人员
    public static final int HAS_NOT_DEFAULT_CARD = 5333;//该用户没有设置默认银行卡
    public static final int PLATFORM_ONLY_ONE = 5334;//最多启用一个支付通道
    public static final int DEDUCT_MONEY_FAIL = 5335;//未还过款,不允许减免所有金额

    public static final int DEDUCT_NOT_EXIST = 5336;//取消的代扣不存在
    public static final int DEDUCT_CANCEL_FAIL= 5337;//当前状态不允许取消
    public static final int ORDER_NOT_COMPLATE= 5338;//该用户存在未结清订单

    //=========5500~5600 标的
    public static final int PRODUCT_MONEY_ERROR = 5500;//标的基础金额与综合费相加应等于展示总额
    public static final int PRODUCT_DEFAULT_EXIST = 5501;//默认标的已存在
    public static final int PRODUCT_DEFAULT_NOT_EXIST = 5502;//请先配置默认标的
    public static final int PRODUCT_OVER_LIMIT = 5503;//平台最大支持配置{}个标的
    public static final int PRODUCT_DEFAULT_NOT_STOP = 5504;//默认标的不能停用
    public static final int PRODUCT_LOAN_EXIST = 5505;//已存在相同展示金额标的
    public static final int EXCEED_DAYS_LESS_BAD_DEBT = 5506;//逾期计费天数不能大于坏账天数
    public static final int PACT_NOT_EXISIT = 5507;//协议不存在
    public static final int PRODUCT_GROUP_NOT_EXIST = 5508;//标的分组不存在或已停用
    public static final int PRODUCT_HAS_START = 5509;//该分组下含有未停用的标的


    //=========5601~5650 推送
    public static final int PUSH_SENDER_NOT_FIND = 5601;//推送不存或已停用
}
