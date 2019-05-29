package org.songbai.loan.common.util;

import org.songbai.cloud.basics.utils.http.AgentKit;

import java.util.regex.Pattern;

public class AgentKitImpl extends AgentKit {

    private static final String ua = "user-agent";


    public static final Pattern androidKeyword = Pattern.compile("(Android|linux)", Pattern.CASE_INSENSITIVE);

    public static final Pattern iosKeyword = Pattern.compile("(iPhone|iPod|iPad|iOs)", Pattern.CASE_INSENSITIVE);

    public static final Pattern appleKeyword = Pattern.compile("(iPhone|iPod|iPad|ios|Macintosh|Mac OS X)", Pattern.CASE_INSENSITIVE);

    public static final Pattern pcKeyword = Pattern.compile("(Windows|Macintosh|Mac OS X)", Pattern.CASE_INSENSITIVE);

    public static boolean isAndroid(String userAgent) {


        return checkUserAgent(userAgent, androidKeyword);
    }

    public static boolean isIphone(String userAgent) {

        return checkUserAgent(userAgent, iosKeyword);
    }

    public static boolean isApple(String userAgent) {

        return checkUserAgent(userAgent, appleKeyword);
    }

    public static boolean isPc(String userAgent) {

        return checkUserAgent(userAgent, pcKeyword);
    }

    public static boolean checkUserAgent(String userAgent, Pattern keyword) {


        return keyword.matcher(userAgent).find();
    }
}
