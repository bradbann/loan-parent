package org.songbai.loan.constant.sms;

public class TemplateConst {


    public static final String TEMPLATE_REDIS_KEY = "sms:template";

    public static final String PREFIX_EMAIL = "email";
    public static final String PREFIX_SMS_SENDER = "sms_sender_";
    public static final String PREFIX_SMS_TEMPLATE = "sms_temp_";
    public static final String PREFIX_SMS_MESSAGE = "sms_mesg_";


    public static enum Type {

        SMS_TEMPLATE(1, "短信模板更新"),
        SMS_SENDER(2, "短信发送器更新"),
        SMS_TEMPLATE_SENDER(3, "短信模板发送器"),
        EMAIL_TEMPLATE(4, "邮件模板更新");

        public final int type;
        public final String msg;

        Type(int type, String msg) {
            this.type = type;
            this.msg = msg;
        }

    }
}
