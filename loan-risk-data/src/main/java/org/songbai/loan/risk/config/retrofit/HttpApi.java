package org.songbai.loan.risk.config.retrofit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface HttpApi {
    String value() default "";//通过key获得配置文件中的值

    Class[] interceptor() default {};


}
