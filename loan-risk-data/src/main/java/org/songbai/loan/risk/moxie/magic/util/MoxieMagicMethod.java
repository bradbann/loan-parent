package org.songbai.loan.risk.moxie.magic.util;

/**
 * Created by mr.czh on 2018/11/8.
 */
public class MoxieMagicMethod {

    public enum Method{
        MagicWand2("moxie.api.risk.magicwand2.application","魔杖2.0"),
        MagicScore("moxie.api.risk.magicscore","魔分"),
        MagiccueTags("moxie.api.risk.magiccube.tags","魔方标签");
        private String method;
        private String desc;
        Method(String method,String desc){
            this.method = method;
            this.desc = desc;
        }
        public String getMethod(){
            return this.method;
        }
    }


    public static class ReqCommonParams {
       public static final String METHOD = "method";
       public static final String APP_ID = "app_id";
       public static final String VERSION = "version";
       public static final String FORMAT = "format";
       public static final String SIGN_TYPE = "sign_type";
       public static final String TIMESTAMP = "timestamp";
       public static final String BIZ_CONTENT = "biz_content";
       public static final String SIGN = "sign";
    }

    public static class ReqCommonParamsValue {
       public static final String VERSION = "1.0";
       public static final String FORMAT = "JSON";
       public static final String SIGN_TYPE = "RSA";
    }

}
