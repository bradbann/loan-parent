
package org.songbai.loan.constant.rediskey;

/**
 * @author Administrator
 */
public class UserRedisKey {


    public static final String USER_INFO = "user:info";//当前用户信息
    public static final String USER_DATA = "user:data";//当前用户user_info表的信息

    public static final String LOGIN_LIMIT = "user:login_limit_";//密码错误限制登陆时间(分)
    public static final String USER_LOGIN_ERROR_TIMES = "user:login:error:times";//用户登录密码错误次数

    public static final String REDIS_MSG_CODE_KEY = "user:msg_code_";//短信验证码

    public static final String REDIS_MSG_IMG_KEY = "user:msg_img_code_";//短信验证图片
    
    public static final String REDIS_MSG_LIMIT_KEY = "user:msg_limit_";//获取短信验证码是否需要验证码

    public static final String LOGIN_LIMIT_SECONDS = "user:login_limit_secondes";//密码错误限制登陆时间(秒)

    public static final String ADMIN_USER_UPDATE_BANKPHONE = "user:admin_user_update_bankphone";//密码错误限制登陆时间(秒)

    public static final String REDIS_KEY_USER_REGISTER = "user:filter:register";//注册防刷接口

    public static final String REDIS_KEY = "login:img:code";
    public static final String IMG_CODE = "imgcode";
    public static final String REGISTER = "register";

    public static final String ADD_FEEDBACK_USER = "user:userfeedback:addfeedback";

    public static final String SENDMSGCODE_VALIDATE = "user:validate:sendmsgcode";

    public static final String UPLOAD_IMAGE_USER = "user:upload:image";

    public static final String UPLOAD_IMAGES_USER = "user:upload:images";

    public static final String UPLOAD_FILE_USER = "user:upload:file";

    public static final String UPLOAD_FILES_USER = "user:upload:files";

    public static final String UPLOAD_FILEENCODE_USER = "user:upload:fileencode";

    //------------------------支付相关rediskey----------------------

    public static final String PAYMENT_LIMIT = "payment:limit";//打款短信限制rediskey

    public static final String PAYMENT_YIBAO = "payment:yibao";//易宝打款rediskey

    public static final String PAYMENT_CHANGJIE = "payment:changjie";//畅捷打款rediskey

    public static final String PAYMENT_TEST = "payment:test";//测试打款rediskey

    public static final String REPAYMENT_AUTO = "repayment:auto";//订单号代扣rediskey

    public static final String USER_REPAYMENT = "user:repayment";//当前用户还款订单号

    public static final String PAYMENT_REQUEST = "payment:request";//支付请求限制key
    public static final String CONFIG_PAY_PLATFORM = "config:payPlatform";//支付平台配置相关

    public static final String USER_ORDER_LIMIT = "user:order:limit";//下单限制

    //统计相关
    public static final String USER_CHANNEL_STATIS_LIST = "user:channel:statis:list";//渠道扣量统计
    public static final String USER_CHANNEL_STATIS_DEDUCA = "user:channel:statis:deduca";//渠道扣量数量

    public static final String CHANNEL_UV_STATIS = "user:channel:uv:statis";//渠道uv统计
    public static final String USER_PRODUCT = "user:product";
    public static final String USER_PRODUCT_GROUP = "user:product:group";

    //---------------限流相关key-----------------
    public static final String LIMIT_USER_REPAYMENT = "limit:user:repayment";//还款限流

}
