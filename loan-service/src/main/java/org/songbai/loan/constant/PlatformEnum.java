package org.songbai.loan.constant;

public enum PlatformEnum {

    Android(1, "android"),
    IOS(2, "ios"),
    Web(3, "web"),
    H5(4, "h5"),
    other(5, "第三方");


    public final int value;
    public final String code;

    public static PlatformEnum valueOf(Integer plat) {
        if (null == plat) {
            return null;
        }
        switch (plat) {
            case 1:
                return Android;
            case 2:
                return IOS;
            case 3:
                return Web;
            default:
                return H5;
        }
    }

    PlatformEnum(int value, String code) {
        this.value = value;
        this.code = code;
    }

    public static PlatformEnum parse(Integer id) {
        for (PlatformEnum platformEnum : values()) {
            if (platformEnum.value == id) {
                return platformEnum;
            }
        }

        return null;
    }

}
