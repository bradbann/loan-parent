package org.songbai.loan.constant.user;


import java.util.HashMap;
import java.util.Map;

/**
 * Author: qmw
 * Date: 2018/10/31 7:58 PM
 */
public class OrderConstant {
    private static final Map<String, String> statusNameMap = new HashMap<>();

    public enum Stage {
        MACHINE_AUTH(1, "机审阶段"), ARTIFICIAL_AUTH(2, "复审阶段"),
        LOAN(3, "放款阶段"), REPAYMENT(4, "还款阶段");

        public final int key;
        public final String name;

        Stage(int key, String name) {
            this.key = key;
            this.name = name;
        }

        public static OrderConstant.Stage parse(Integer key) {

            for (OrderConstant.Stage type : values()) {
                if (type.key == key) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum Status {
        PROCESSING(0, "进行中"),//  机审阶段 跳过复审直接到财务放款
        WAIT(1, "等待"),//机审/人审/放款/还款
        SUCCESS(2, "成功"),//已结清
        FAIL(3, "失败"), // 坏账(放款拒绝)
        OVERDUE(4, "逾期"),  // (opt 放款阶段 表示财务退回
        //      机审阶段 表示机审转人工

        OVERDUE_LOAN(5, "逾期还款"),//仅用于还款终结状态
        ADVANCE_LOAN(6, "提前还款"),//仅用于还款终结状态
        CHASE_LOAN(7, "催收还款"),//仅用于还款终结状态

        EXCEPTION(8, "放款失败"),// (opt 放款阶段 放款失败
        //   还款阶段 还款失败
        DEDUCT(9, "减免金额"),//仅用作操作记录,减免金额
        SEPERATE_ORDER(10, "催收分配"),//仅用作操作记录,催收分配
        GROUP_SEPERATE(11, "组内分单"),//仅用作操作记录,组内分单
        AUTO_DEDUCT(12, "部分还款");//仅用作操作记录,部分还款

        public final int key;
        public final String name;
        Status(int key, String name) {
            this.key = key;
            this.name = name;
        }

        public static OrderConstant.Status parse(Integer key) {

            for (OrderConstant.Status type : values()) {
                if (type.key == key) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum Guest {
        NEW_GUEST(1, "新客"),
        SECOND_GUEST(2, "次新客"),
        OLD_GUEST(3, "老客");
        public final int key;
        public final String name;

        Guest(int key, String name) {
            this.key = key;
            this.name = name;
        }

        public static OrderConstant.Guest parse(Integer key) {

            for (OrderConstant.Guest type : values()) {
                if (type.key == key) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum AuthStatus {
        WAIT_REVIEW(0, "待复审"),
        OVER_TKE(1, "已提取");
        public final int key;
        public final String name;

        AuthStatus(int key, String name) {
            this.key = key;
            this.name = name;
        }

        public static OrderConstant.AuthStatus parse(Integer key) {

            for (OrderConstant.AuthStatus type : values()) {
                if (type.key == key) {
                    return type;
                }
            }
            return null;
        }
    }

    public enum RepayType {
        ALIPAY(0, "alipay"),
        WEIXIN(1, "wepay"),
        BANKCARD(2, "thirdPay"),
        RMB(3, "offline");

        public final Integer key;
        public final String value;

        RepayType(Integer key, String value) {
            this.key = key;
            this.value = value;
        }

        public static RepayType parseName(Integer key) {
            for (RepayType type : values()) {
                if (type.key.equals(key)) return type;
            }
            return null;
        }

    }


    public static String handleOrderStatus(Integer stage, Integer status) {
        if (stage == null || status == null) return null;
        String key = stage + "," + status;
        if (statusNameMap.get(key) != null) return statusNameMap.get(key);
        String statusName = null;
        if (stage == Stage.MACHINE_AUTH.key) {
            switch (status) {
                case 1:
                    statusName = "已提单";
                    break;
                case 3:
                    statusName = "机审拒绝";
                    break;
            }
        } else if (stage == Stage.ARTIFICIAL_AUTH.key) {
            switch (status) {
                case 1:
                    statusName = "待复审";
                    break;
                case 3:
                    statusName = "复审拒绝";
                    break;
            }
        } else if (stage == Stage.LOAN.key) {
            switch (status) {
                case 0:
                    statusName = "放款中";
                    break;
                case 1:
                    statusName = "待放款";
                    break;
                case 3:
                    statusName = "放款拒绝";
                    break;
            }
        } else if (stage == Stage.REPAYMENT.key) {
            switch (status) {
                case 0:
                    statusName = "还款中";
                    break;
                case 1:
                    statusName = "待还款";
                    break;
                case 2:
                    statusName = "已结清";
                    break;
                case 3:
                    statusName = "坏账";
                    break;
                case 4:
                    statusName = "逾期";
                    break;
                case 5:
                    statusName = "逾期还款";
                    break;
                case 6:
                    statusName = "提前还款";
                    break;
                case 7:
                    statusName = "催收还款";
                    break;
                case 8:
                    statusName = "还款失败";
                    break;
            }

        }
        if (statusName != null)
            statusNameMap.put(key, statusName);
        return statusName;
    }
}
