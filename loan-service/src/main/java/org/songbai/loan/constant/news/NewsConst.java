package org.songbai.loan.constant.news;

public class NewsConst {

    public static enum NewsType {

        News(2, ""), Notice(1, "");

        public final int value;
        public final String name;

        NewsType(int value, String name) {
            this.value = value;
            this.name = name;
        }
    }


    public static enum ArticleType {
        TYPE_NEWBIE_GUIDE(1, "新手引导"),
        TYPE_GAME_RULES(2, "规则"),
        TYPE_AGREEMENTS(3, "合作协议"),
        TYPE_HELP(4, "帮助中心");

        public final int value;
        public final String name;


        ArticleType(int value, String name) {
            this.value = value;
            this.name = name;
        }

        public static ArticleType parse(int value) {

            for (ArticleType type : values()) {
                if (type.value == value) {
                    return type;
                }
            }
            return null;
        }
    }

    public static enum JumpType {
        H5(1, "h5"),
        HTML(2, "HTML"),
        MODULE(3, "模块");

        public final int value;
        public final String name;


        JumpType(int value, String name) {
            this.value = value;
            this.name = name;
        }
    }


}
