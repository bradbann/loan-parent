package org.songbai.loan.config;


import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Accessible {

    /**
     * 平台（所有用户） 可以
     *
     * @return
     */
    boolean platform() default false;

    /**
     * 平台（所有用户） 可以， 管理员也可以
     *
     * @return
     */
    boolean admin() default false;


    /**
     * 平台管理员才可以
     *
     * @return
     */
    boolean superUser() default false;

    /**
     * 仅仅代理可以操作
     */
    boolean onlyAgency() default false;

    /**
     * 只有代理普通用户可以操作
     */
    boolean onlyAgencyCommon() default false;
}
