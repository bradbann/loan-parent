package org.songbai.loan.common.util;

import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.utils.http.AgentKit;
import org.songbai.cloud.basics.utils.http.HeaderKit;
import org.songbai.loan.constant.PlatformEnum;

import javax.servlet.http.HttpServletRequest;

public class PlatformKit {

    public static final String CHANNEL_IOS = "ios";
    public static final String CHANNEL_ANDROID = "android";


    public static final String DEVICE = "loan-device";//设备号
    public static final String CHANNEL = "loan-channel";//马甲code
    public static final String MARKET = "loan-market";// 市场 ios:shahsha
    public static final String MOBILE_NAME = "mobile-name";//手机品牌
    public static final String MOBILE_TYPE = "mobile-type";//手机型号
    public static final String SYSTEM_VERSION = "system-version";//系统版本号
    public static final String VERSION = "loan-version";//应用版本号
    public static final String GEXING = "loan-gexing";//应用版本号


    public static PlatformEnum parsePlatform(HttpServletRequest request) {
        String channel = HeaderKit.getHeader(request, MARKET);
        if (StringUtils.isNotEmpty(channel)) {
            if (channel.startsWith(CHANNEL_ANDROID)) {
                return PlatformEnum.Android;
            }else if (channel.startsWith(CHANNEL_IOS)) {
                return PlatformEnum.IOS;
            }
        }
        if (AgentKit.isPc(request)) {
            return PlatformEnum.Web;
        } else if (AgentKit.isAndroid(request)) {
            return PlatformEnum.Android;
        } else if (AgentKit.isIphone(request)) {
            return PlatformEnum.IOS;
        } else {
            return PlatformEnum.H5;
        }
    }

    public static String parseChannel(HttpServletRequest request) {
        return HeaderKit.getHeader(request, CHANNEL);
    }

    public static String parseMarket(HttpServletRequest request) {
        return HeaderKit.getHeader(request, MARKET);
    }

    public static String getVersion(HttpServletRequest request) {

        return HeaderKit.getHeader(request, VERSION);
    }

    public static String getDevice(HttpServletRequest request) {

        return HeaderKit.getHeader(request, DEVICE);
    }
    public static String getSystemVersion(HttpServletRequest request) {

        return HeaderKit.getHeader(request, SYSTEM_VERSION);
    }
    public static String getMobileName(HttpServletRequest request) {

        return HeaderKit.getHeader(request, MOBILE_NAME);
    }
    public static String getMobileType(HttpServletRequest request) {
        return HeaderKit.getHeader(request, MOBILE_TYPE);
    }
    public static String getGexing(HttpServletRequest request) {
        return HeaderKit.getHeader(request, GEXING);
    }
}
