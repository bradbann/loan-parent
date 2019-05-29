package org.songbai.loan.constant.resp;

import org.songbai.cloud.basics.mvc.RespCode;

/**
 * 2000 -3000
 */
public class UserRespCode extends RespCode {

    public static final int PHONE_EXISTS = 2010;
    public static final int PARAM_VALIDATE_TYPE = 2011;
    public static final int USER_ACCOUNT_ERROR = 2012;
    public static final int USER_LOGIN_FAIL = 2013;
    public static final int USERNAME_NOT_NULL = 2014;
    public static final int IDENTITY_CARD_NOT_NULL = 2015;
    public static final int USERNAME_WRONG = 2016;
    public static final int IDENTITY_CARD_FORM_WRONG = 2017;
    public static final int IDENTITY_CARD_FRONT_NOT_NULL = 2018;
    public static final int IDENTITY_CARD_BACK_NOT_NULL = 2019;
    public static final int USERNAME_NOT_SUPPORT = 2020;

    public static final int ACCOUNT_NOT_EXISTS = 2021;
    public static final int CONTACT_WAY_WRONG = 2022;
    public static final int IDFAS_OUTWARDS_MAX_LIMIT = 2023;
    public static final int PASSWORD_RETRY_UPDATE = 2024;
    public static final int APPLY_PROMOTER_WRONG = 2025;
    public static final int PHONE_WRONG = 2026;
    public static final int MSG_CODE_TYPE_WRONG = 2027;
    //    public static final int PROMOTER_URL_WRONG = 2028;
    public static final int PROMOTER_NOT_EXISTS = 2029;
    public static final int USER_REGISTER_FAIL = 2030;

    public static final int OLD_PASSWORD_WRONG = 2031;
    public static final int USERNAME_NOT_LEGAL = 2032;
    public static final int USERNAME_EXISTS = 2033;
    public static final int IDENTITY_CARD_IS_BIND = 2034;
    public static final int IDENTITY_CARD_WRONG = 2035;
    public static final int COMMIT_MAX_LIMIT = 2036;
    public static final int ALREADY_PROMOTER = 2037;
    public static final int DEFAULT_PROMOTER_LEVEL_NOT_EXIST = 2038;
    public static final int PROMOTER_CODE_NOT_EXIST = 2039;
    public static final int MSG_RAPPORT_CODE = 2040;

    public static final int PROVISION_NOT_EXIST = 2046;

    public static final int ACCOUNT_ALREADY_REGISTER = 2050;
    public static final int ACCOUNT_NOT_REGISTER = 2051;
    public static final int ACCOUNT_ALREADY_BIND = 2052;
    public static final int ACCOUNT_PASS_NOTSET = 2056; // 用户没有设置登录密码
    public static final int VLIDATE_ERROR_REPEAT = 2058; // 验证码错误，请重新输入
    public static final int REQUEST_PARAM_ERROR = 2052;
    public static final int UPLOAD_DATA_NULL = 2057;
    public static final int USERNAME_NOT_EMOJI = 2059;
    public static final int REQUEST_MORE = 2060;
    public static final int USER_API_IP_ERROR = 2061;
    public static final int USER_API_COUNT_OVER = 2062;
    /**
     * 资金密码输入不一致
     */
    public static final int CAPITAL_PASS_NOT_EQUALS = 2100;
    /**
     * 手机验证码错误
     */
    public static final int PHONE_MSG_CODE_ERROR = 2101;
    /**
     * 邮箱验证码错误
     */
    public static final int EMAIL_MSG_CODE_ERROR = 2102;
    /**
     * 验证码错误
     */
    public static final int MSG_CODE_ERROR = 2103;
    /**
     * 手机号不能为空
     */
    public static final int PHONE_NOT_NULL = 2104;
    /**
     * 验证码不能为空
     */
    public static final int MSG_NOT_NULL = 2105;
    /**
     * 邮箱不能为空
     */
    public static final int EMAIL_NOT_NULL = 2106;
    /**
     * 参数为空
     */
    public static final int PARAM_IS_NULL = 2107;
    /**
     * 用户不存在
     */
    public static final int USER_NOT_EXIST = 2108;
    /**
     * 邮箱格式不对
     */
    public static final int EMAIL_FORMAT_ERROR = 2109;
    /**
     * 密码为至少8位的英文、数字、符号的组合
     */
    public static final int PASS_WORD_LENGTH_ERROR = 2110;
    /**
     * 证件格式不符
     */
    public static final int NUM_FORMAT_ERROR = 2111;
    /**
     * 未设置资金密码
     */
    public static final int DRAW_PASS_IS_NULL = 2112;
    /**
     * 邮箱已存在
     */
    public static final int EMAIL_IS_EXIST = 2114;
    /**
     * 资金密码错误次数超限
     */
    public static final int DRAW_PASS_ERROR_COUNT_LIMIT = 2115;

    public static final int VALIDATE_TIMEOUT = 2116;
    public static final int PARAM_MUST_JSON = 2118;
    /**
     * 资金密码和登陆密码一致
     */
    public static final int DRAW_PASS_AND_USER_PASS_EQUALS = 2119;
    /**
     * 验证码错误
     */
    public static final int CODE_ERROR = 2120;
    /**
     * 新手机验证码错误
     */
    public static final int NEW_PHONE_MSG_CODE_ERROR = 2131;
    /**
     * 新邮箱验证码错误
     */
    public static final int NEW_EMAIL_MSG_CODE_ERROR = 2132;
    /**
     * 旧手机验证码错误
     */
    public static final int OLD_PHONE_MSG_CODE_ERROR = 2133;
    /**
     * 旧邮箱验证码错误
     */
    public static final int OLD_EMAIL_MSG_CODE_ERROR = 2134;
    /**
     * 用户api已失效
     */
    public static final int USER_API_EXPIRED = 2135;
    /**
     * 用户登录图片验证不能为空
     */
    public static final int USER_IMGCODE_NOT_EXISIT = 2136;

    public static final int USER_REAL_NAME_FAIL = 2137;

    /**
     * 用户已经认证过
     */
    public static final int USER_AUTHENTICATION_THROUGH = 2200;
    public static final int USER_BASE_AUTH_NOT = 2201; // 用户没有进行初级认证

    /**
     * API关闭
     */
    public static final int USER_API_NO_CLOSE = 2202; // 用户没有进行初级认证
    /**
     * API关闭
     */
    public static final int COIN_TO_ADDR_ERROR = 2204; // 用户没有进行初级认证

    //----------现金贷
    public static final int CREDIT_NOT_DISSATISFACTION = 2401; //您的信用评级未达到平台要求
    public static final int AUTHENTICATION_NOT_COMPLETE = 2402; //您还有未完成的认证,请先完成认证
    public static final int ORDER_NOT_COMPLETE = 2403; //您还有未完成订单
    public static final int LOAN_NOT_EXIST = 2404; //暂无可用的借款
    public static final int ORDER_NOT_EXIST = 2405; //订单不存在
    public static final int USER_FAIL_DAYS_LIMIT = 2406; //x天后才能继续下单
    public static final int BANK_CARD_NOT_EXIST = 2407; //银行卡不存在
    public static final int REQUEST_PAY_FAILED = 2408; //请求支付失败
    public static final int REQUEST_BINDCARD_FAILED = 2409; //请求绑卡失败
    public static final int MONEY_NOT_MATCH = 2410; //金额不匹配
    public static final int LOAN_REJECT = 2411; //请于xx后再申请借款
    public static final int NAME_NOT_MATCH = 2412; //请填写身份证上的姓名
    public static final int AUTH_FAILED = 2413; //认证失败
    public static final int INTERNET_ERROR = 2414; //请检查网络
    public static final int NOT_REPEAT_AUTH = 2415; //不要重复认证
    public static final int NOT_REPEAT_SUBMIT = 2416; //不要重复提交
    public static final int REQUEST_UNBIND_FAILED = 2417; //请求解绑失败
    public static final int HAS_UNFINISHED_ORDER = 2418; //还有未完成的订单
    public static final int BANKCARD_ERROR = 2419; //银行卡位数不正确
    public static final int PHONE_ERROR = 2420; //手机号位数不正确
    public static final int MERCHANT_NOT_FOUND = 2421; //该商户暂不支持交易（没有配置秘钥等相关）
    public static final int MERCHANT_NOT_USE = 2422;//该支付平台未启用
    public static final int DO_NOT_DELETE_TABLE = 2423;//谁删表了
    public static final int PLEASE_AUTH = 2424;//请先绑卡
    public static final int PLEASE_GET_MSG= 2425;//请先获取验证码
    public static final int VERIFICATION_FAILED= 2426;//验签失败
    public static final int IDCARD_ALREADY_USE= 2427;//身份证号已经被使用
    public static final int ORDER_NOT_REPAY_TIME= 2428;//不是还款阶段
    public static final int ORDER_ALREADY_SUCCESS= 2429;//订单已经成功
    public static final int ORDER_REPAY_PROCESSING= 2430;//订单正在还款中
    public static final int ORDER_HAS_FAILED= 2431;//订单已失效，需要重新获取验证码
    public static final int PHONE_HAS_EXIST= 2432;//不可选择自己的手机号
    public static final int SYSTEM_EXCEPTION = 2433;//系统异常，请稍后再试
    public static final int IMG_NOT_EDIT = 2434;//图片不允许编辑
    public static final int HAS_BIND= 2435;//您已经绑过银行卡了
    public static final int PLEASE_DO_OTHER_AUTH= 2436;//请先完成其他几项认证
    public static final int TEST_ACCOUNT= 2437;//测试账号
    public static final int THIS_CARD_BINDED= 2438;//您已经绑过此卡了
    public static final int BINDED_ONLY_FIVE= 2439;//最多绑定五张卡
    public static final int BANK_CARD_HAS_NOT_DEFAULT= 2440;//您还未设置默认收款银行卡
    public static final int PLEASE_UPDATE_APP= 2441;//请更新app
    public static final int DEFAULT_HAS_UNFINISHED_ORDER = 2442; //默认银行卡还有未完成的订单
    public static final int NOT_SUPPORT_THIS_BANK = 2443; //暂不支持该银行卡
    public static final int REGET_MSG = 2444; //重新获取验证码
    public static final int PRODUCT_VEST_NOT_START = 2445; //平台正在维护，请稍后尝试下单

    public static final int ORDER_HAS_REPAYMENT =2446 ;//您已发起订单，请在支付软件中继续支付。如已支付，请稍候片刻。
    public static final int NUMBER_IS_LAGER = 2447;//金额过大，暂不支持
    public static final int USER_MSG_SEND = 2448;//请使用短信验证码
    public static final int IMG_CODE_NEED = 2449;//需要图片验证码
    public static final int AGENCY_NOT_REGISTER = 2450;//平台暂不支持注册，请稍后再试
}

