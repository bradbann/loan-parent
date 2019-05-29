package org.songbai.loan.common.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.utils.base.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneUtil {

    private static final Pattern PHONE_TW = Pattern.compile("^0[2-9]\\d{8,10}$");
    private static final Pattern PHONE_ZH = Pattern.compile("^1(3|4|5|6|7|8)\\d{9}$");
    private static final Pattern PHONE_HK = Pattern.compile("^(5|6|8|9)\\d{7}$");

    private static final Pattern PHONE_CHINA = Pattern.compile("^(600|86|\\+86)?(1[3-9]\\d{9})$");
    private static final Pattern TEL_CHINA = Pattern.compile("^(\\()?(|0[345678][1-9][0-9]|09[\\d]{2}|02[0-9]|010|852|853)?(-|\\)|[\\s])?([1-9][\\d]{6,7})$");


    public static String trimFirstZero(String code) {

        if (StringUtil.isEmpty(code) || !StringUtils.isNumeric(code)) {
            return code;
        }

        Integer realCode = Integer.parseInt(code);

        return realCode + "";
    }


    //手机号
    public static boolean checkPhone(String phone) {

        return PHONE_ZH.matcher(phone).matches()
                || PHONE_TW.matcher(phone).matches()
                || PHONE_HK.matcher(phone).matches();
    }

    public static void main(String[] args) {

        try {
            List<String> list = FileUtils.readLines(new File("/Users/navy/test/phone.txt"));

//            System.out.println(trimSpaceAndAreaCode("+8615906694760"));
//            System.out.println(trimSpaceAndAreaCode("+8613291869894"));
//            System.out.println(trimSpaceAndAreaCode("8613291869894"));
//            System.out.println(trimSpaceAndAreaCode("188 5817 8998"));

            for (String s1 : list) {
                String s2 = trimSpaceAndAreaCode(s1);

                if (!s1.equals(s2)) {
                    System.out.println(s1 + "\t\t\t" + s2);
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String trimSpaceAndAreaCode(String phone) {


        if (StringUtil.isEmpty(phone)) {
            return phone;
        }


        phone = phone.replaceAll("[\\s-\\(\\)]", "");

        if(StringUtils.isBlank(phone)){
            return null;
        }

        Matcher matcher = PHONE_CHINA.matcher(phone);
        if (matcher.matches()) {
            return matcher.group(2);
        }

        matcher = TEL_CHINA.matcher(phone);
        if (matcher.matches()) {
            String ac = matcher.group(2);
            String ph = matcher.group(4);

            return StringUtils.isEmpty(ac) ? ph : ac + ph;

        }


        return phone;
    }

    // 手机号码前三后四脱敏
    public static String mobileEncrypt(String mobile) {
        if (StringUtils.isEmpty(mobile) || (mobile.length() != 11)) {
            return mobile;
        }
        return mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }
}
