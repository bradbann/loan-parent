package org.songbai.loan.risk.service.statis.util;

import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AgeUtils {

    //    private static final String reg = "^[1-9]\\d{5}((18|19|([23]\\d))\\d{2})((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
    private static final String reg = "^[1-9]\\d{5}([123]\\d{3})([01]\\d)([0123]\\d)\\d{3}[0-9Xx]$";


    private static Pattern REG = Pattern.compile(reg);


    public static void main(String[] args) {

//        System.out.println(getAge("511321198908168790"));

        System.out.println(validateCardno("20060216-20260216"));
    }


    public static Integer getAge(String idcard) {

        Matcher matcher = REG.matcher(idcard);

        if (matcher.find()) {
            String years = matcher.group(1);
            String month = matcher.group(2);
            String day = matcher.group(3);

            return getAgeFromBirthTime(years, month, day);
        }

        return -1;
    }


    /**
     * 距离身份证有效期截止时间 的年树
     *
     * @param validate
     * @return 0 ,过期 ， 1 ，不到一年， 2一年到两年，3两年到三年， 4三年以上。
     */
    public static Integer validateCardno(String validate) {

        // 20171024-20271024;

        if (StringUtil.isEmpty(validate)) {
            return 0;
        }

        String[] valiArray = validate.split("-");

        if (valiArray.length < 2) {
            return 0;
        }


        Date endDate = SimpleDateFormatUtil.stringToDate(valiArray[1]);

        long c = (endDate.getTime() - System.currentTimeMillis()) / (24 * 60 * 60 * 1000);


        if (c > 30 && c < 365) {
            return 1;
        } else if (c > 365 && c < 365 * 2) {
            return 2;
        } else if (c > 365 * 2 && c < 365 * 3) {
            return 3;
        } else if (c > 365 * 3) {
            return 4;
        } else {
            return 0;
        }

    }


    // 根据年月日计算年龄,birthTimeString:"1994-11-14"
    private static int getAgeFromBirthTime(String year, String month, String day) {
        // 先截取到字符串中的年、月、日
        int selectYear = Integer.parseInt(year);
        int selectMonth = Integer.parseInt(month);
        int selectDay = Integer.parseInt(day);
        // 得到当前时间的年、月、日
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayNow = cal.get(Calendar.DATE);

        // 用当前年月日减去生日年月日
        int yearMinus = yearNow - selectYear;
        int monthMinus = monthNow - selectMonth;
        int dayMinus = dayNow - selectDay;

        int age = yearMinus;// 先大致赋值
        if (yearMinus <= 0) {// 选了未来的年份 或者同一年 都是0岁。
            age = 0;
        } else {
            if (monthMinus < 0) {// 当前月>生日月 表示还没有到生气， 减1岁
                age = age - 1;
            }
            if (monthMinus == 0) {
                if (dayMinus < 0) {
                    age = age - 1;
                }
            }
        }
        return age;
    }
}
