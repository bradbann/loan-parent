package org.songbai.loan.constant.agency;

import org.songbai.loan.constant.user.UserConstant;

public class AgencyDeptConstant {
    public enum DeptType {
        COMMON_DEPT(1,"普通部门"),REVIEW_DEPT(2,"信审部门"),
        FINANCE_DEPT(3,"财务部门"),CHASEDEBT_DEPT(4,"催收部门");
        public final int key;
        public final String name;

        DeptType(int key, String name) {
            this.key = key;
            this.name = name;
        }
        public AgencyDeptConstant.DeptType parse(Integer key) {

            for (AgencyDeptConstant.DeptType type : values()) {
                if (type.key == key) {
                    return type;
                }
            }
            return null;
        }
    }
}
