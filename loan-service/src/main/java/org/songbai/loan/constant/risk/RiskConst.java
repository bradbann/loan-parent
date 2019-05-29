package org.songbai.loan.constant.risk;

public class RiskConst {


    public enum Catalog {
        BASIC(1, "基础数据"), CONTACTS(2, "通讯录"),
        CARRIERS(3, "运营商"), TAOBAO(4, "淘宝"),
        MOXIEREPORT(5, "魔蝎报告");

        public final int code;
        public final String name;

        Catalog(int code, String name) {
            this.code = code;
            this.name = name;
        }

        public static Catalog parse(Integer code) {
            for (Catalog catalog : values()) {
                if (code != null && catalog.code == code) {
                    return catalog;
                }
            }
            return null;
        }
    }

    public enum Task {
        SUBMIT_SUCCESS(1, "提交事务成功"), SUBMIT_FAIL(2, "提交失败"),
        AUTH_SUCCESS(3, "授权成功"), AUTH_FAIL(4, "授权失败"),
        DATA_SUCCESS(5, "获取数据成功"), DATA_FAIL(6, "获取数据失败");

        public final int code;
        public final String name;

        Task(Integer code, String name) {
            this.code = code;
            this.name = name;
        }
    }

    public enum CalcSymbol {
        EQ("=", "等于"), GT(">", "大于"), LT("<", "小于"), GTE(">=", "大于并等于"), LTE("<=", "小于并等于"), SECTION("~", "区间"),
        CONTAIN("⊆", "包含"), MATCHS("^", "匹配开始"), MATCHE("$", "匹配结束");

        public final String code;
        public final String name;

        CalcSymbol(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public static CalcSymbol parse(String code) {
            for (CalcSymbol value : values()) {
                if (value.code.equalsIgnoreCase(code)) {
                    return value;
                }
            }
            return null;
        }
    }

    public enum Result {
        DEFAULT(0, "默认"), PASS(1, "通过"), REJECT(2, "拒绝"), MAN(3, "人工");

        public final int code;
        public final String name;

        Result(Integer code, String name) {
            this.code = code;
            this.name = name;
        }
    }

}
