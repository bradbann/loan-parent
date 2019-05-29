package org.songbai.loan.constant.sms;

public class UmengPushTypeCons {
    public static enum Type {
        AUTH_SUCC(1, "高级认证审核成功", "", "UserInfo"),
        AUTH_FAIL(2, "高级认证审核失败", "", "UserInfo"),
        DRAW_SUCC(3, "充币到账", "", "Assets"),
        DRAW_COIN(4, "提币申请", "", "Bills"),
        DRAW_AUTH_SUCC(5, "提币审核成功", "", "Bills"),
        DRAW_AUTH_FAIL(6, "提币审核失败", "", "Bills"),
        DRAW_CONFIRM_SUCC(7, "提币确认成功", "", "Bills"),
        ENTRUST_DEAL(8, "币币成交", "", "History"),
        ENTRUST_SYSTEM_CANCALE(9, "币币系统撤单", "", "Orders"),
        OTC_WARES_SELL_TO_BUY(10, "法币出售购买方", "", "Details"),
        OTC_WARES_SELL_TO_SELL(11, "法币广告出售方", "", "Details"),
        OTC_WARES_BUY(12, "法币广告购买出售方", "", "Details"),
        OTC_WARES_PAY(13, "法币广告对方已付款", "", "Details"),
        OTC_WARES_CONFIRM(14, "法币广告确认收款", "", "Details"),
        OTC_WARES_SYSTEM_CANCALE(15, "法币广告超时未付自动取消", "", "Details"),
        OTC_WARES_CHAT(16, "法币otc广告聊天", "","Chat"),
        ORDER_WARNING(17, "预警通知", "", "Details"),;


        public final int value;
        public final String code;
        public final String url;
        public final String action;

        Type(Integer value, String code, String url, String action) {
            this.value = value;
            this.code = code;
            this.url = url;
            this.action = action;
        }

        public static UmengPushTypeCons.Type parse(int type) {

            for (UmengPushTypeCons.Type t : values()) {

                if (t.value == type) {
                    return t;
                }
            }
            return null;
        }
    }
}
