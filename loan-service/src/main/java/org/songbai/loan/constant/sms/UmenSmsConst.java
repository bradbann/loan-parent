package org.songbai.loan.constant.sms;

public class UmenSmsConst {


    public static enum Type {
        UNICAST(1,"unicast"),//单播
        LISTCAST(2,"listcast"),//列播，要求不超过500个device_token
        FILECAST(3,"filecast"),//文件播，多个device_token可通过文件形式批量发送
        BROADCAST(4,"broadcast"),//广播
        GROUPCAST(5,"groupcast"),//组播，按照filter筛选用户群, 请参照filter参数
        /**
         * customizedcast，通过alias进行推送，包括以下两种case:
         * - alias: 对单个或者多个alias进行推送
         * - file_id: 将alias存放到文件后，根据file_id来推送
         */
        CUSTOMIZEDCAST(6,"customizedcast");

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
