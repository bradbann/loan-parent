package org.songbai.loan.constant.sms;

/**
 * Author: qmw
 * Date: 2018/11/14 11:11 AM
 */
public class PushEnum {
    public static final String DEFAULT_TELECODE = "86";

    public enum Classify {
        SYSTEM(1, "系统推送"),
        GROUP(2, "分组推送"),
        SINGLE(3,"单推");

        public final int value;
        public final String code;

        Classify(Integer value, String code) {
            this.value = value;
            this.code = code;
        }

        public static PushEnum.Classify parse(int type) {

            for (PushEnum.Classify t : values()) {

                if (t.value == type) {
                    return t;
                }
            }
            return null;
        }
    }
    public enum TYPE {
        NOTICE(1, "公告/bannner"),
        ORDER(2, "订单相关");

        public final int value;
        public final String code;

        TYPE(Integer value, String code) {
            this.value = value;
            this.code = code;
        }
        public static PushEnum.TYPE parse(int type) {

            for (PushEnum.TYPE t : values()) {

                if (t.value == type) {
                    return t;
                }
            }
            return null;
        }
    }
    public enum LOAN {
        AUTH_REJECT(1, "审核失败"),
        AUTH_PASS(2, "审核通过"),
        PAY_REJECT(3, "放款拒绝"),
        PAY_SUCCESS(4, "放款成功"),
        REPAY_REMIND(5, "还款提醒"),
        REPAY_SUCCESS(6, "还款成功"),
        LOAN_OVERDUE(7, "逾期提醒"),
        AUTH_DEDUCT(8, "自动扣款");

        public final int value;
        public final String code;

        LOAN(Integer value, String code) {
            this.value = value;
            this.code = code;
        }
        public static PushEnum.LOAN parse(int type) {

            for (PushEnum.LOAN t : values()) {

                if (t.value == type) {
                    return t;
                }
            }
            return null;
        }
    }
}
