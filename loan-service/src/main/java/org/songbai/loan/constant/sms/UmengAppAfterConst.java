package org.songbai.loan.constant.sms;

public class UmengAppAfterConst {
    public static enum Type {
        go_app(0,"go_app"), //  打开应用
        go_url(1,"go_url"), //跳转到URL
        go_activity(2,"go_activity"),   //打开特定的activity
        go_custom(3,"go_custom");//用户自定义内容。


        public final int value;
        public final String code;

        Type(Integer value, String code) {
            this.value = value;
            this.code = code;
        }

        public static UmengAppAfterConst.Type parse(int type) {

            for (UmengAppAfterConst.Type t : values()) {

                if (t.value == type) {
                    return t;
                }
            }
            return null;
        }
    }

}
