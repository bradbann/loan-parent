package org.songbai.loan.constant.statis;

import org.songbai.loan.constant.agency.AgencyDeptConstant;

public class ChannelStatisConst {
    public enum ChannelStatisType {
        REGISTER(1,"注册"),LOGIN(2,"登录"),ORDER(3,"提单"),TRANSFER(4,"下款"),
        REGISTER_LOGIN(5,"注册和登录");
        public final int key;
        public final String name;

        ChannelStatisType(int key, String name) {
            this.key = key;
            this.name = name;
        }
        public ChannelStatisConst.ChannelStatisType parse(Integer key) {

            for (ChannelStatisConst.ChannelStatisType type : values()) {
                if (type.key == key) {
                    return type;
                }
            }
            return null;
        }
    }
}
