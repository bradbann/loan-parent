package org.songbai.loan.constant.sms;

public class SmsConst {

    public static final String DEFAULT_TELECODE = "86";

    public enum Type {
        COMMON(0, "common"), //通用验证码
        PAY_SUCC(1, "pay_succ"), //放款成功通知
        REPAY_REMIND(2, "repay_remind"), //今日还款提醒
        TOMORROW_REPAY_REMIND(3, "tomorrow_repay_remind"),//明日还款提醒
        ADMIN_LOGIN(4, "admin_login"), //后台管理系统用户登录验证码
        AUTO_DEDUCT(5, "auto_deduct"); //自动扣款
        //REGISTER(1, "register"), //注册
        //RESET(2,"rest"); //重置密码

        public final int value;
        public final String code;

        Type(Integer value, String code) {
            this.value = value;
            this.code = code;
        }

        public static Type parse(int type) {

            for (Type t : values()) {

                if (t.value == type) {
                    return t;
                }
            }
            return null;
        }
    }
}
