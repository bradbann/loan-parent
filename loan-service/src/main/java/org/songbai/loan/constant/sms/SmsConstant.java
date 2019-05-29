package org.songbai.loan.constant.sms;

/**
 * 短信模块相关常量
 *
 * @author czh
 * 2017年7月11日上午11:04:20
 */
public class SmsConstant {

    public enum SenderType {
        SMS_SENDER_TYPE_ALI(1, "阿里大鱼"), SMS_SENDER_TYPE_YUNXIN(2, "云信"),
        SMS_SENDER_TYPE_JUHE(3, "聚合"), SMS_SENDER_TYPE_TLSG(4, "TLSG"),
        SMS_SENDER_TYPE_CHUANGLAN(5, "创蓝"), SMS_SENDER_TYPE_PAOPAO(6, "泡泡云");

        public final int key;
        public final String name;

        SenderType(int key, String name) {
            this.key = key;
            this.name = name;
        }

        public static SmsConstant.SenderType parse(Integer key) {

            for (SmsConstant.SenderType type : values()) {
                if (type.key == key) {
                    return type;
                }
            }
            return null;
        }
    }

}
