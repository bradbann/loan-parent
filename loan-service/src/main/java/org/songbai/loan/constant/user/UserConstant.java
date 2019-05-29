/*
 * Copyright (c) 2017 Srs Group - 版权所有
 *
 * This software is the confidential and proprietary information of
 * Strong Group. You shall not disclose such confidential information
 * and shall use it only in accordance with the terms of the license
 * agreement you entered into with org.songbai
 */
package org.songbai.loan.constant.user;

/**
 * 描述:用户常量
 *
 * @author C.C
 * @created 2017年2月28日 上午9:34:08
 * @since v1.0.0
 */
public class UserConstant {
    public enum Status {
        BLACK_LIST(0, "黑名单"), NORMAL(1, "正常"), GREY_LIST(2, "灰名单"), WHITE_LIST(3, "白名单");
        public final int key;
        public final String name;

        Status(int key, String name) {
            this.key = key;
            this.name = name;
        }

        public Status parse(Integer key) {

            for (Status type : values()) {
                if (type.key == key) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum Opt {
        LOGIN(11),// 登录
        REGISTER(12),// 注册
        MODIFY_PWD(21), // 修改密码
        MODIFY_SAFE_PWD(22), // 修改安全密码
        MODIFY_PHONE(23), // 修改手机号
        BIND_EMAIL(24), // 绑定邮箱
        BIND_GOOGLE_KEY(25), //绑定google验证
        MODIFY_PAY_CARD(26), //法币收款账户修改
        CHECK_DRAW_PASSWORD(27), //资金密码验证
        RESET_GOOGLE_KEY(28); //重置谷歌验证码
        public final int key;

        Opt(int key) {
            this.key = key;
        }
    }

    public enum Sex {
        DEFAULT(0, "未知"), MALE(1, "男"), FEMAIL(2, "女");

        public final int code;
        public final String name;

        Sex(Integer code, String name) {
            this.code = code;
            this.name = name;
        }
    }
}
