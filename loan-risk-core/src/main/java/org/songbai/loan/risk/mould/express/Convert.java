package org.songbai.loan.risk.mould.express;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class Convert {


    private final static Pattern pattern = Pattern.compile("[\\d\\.\\,]");

    public static boolean isNumber(String... params) {

        boolean number = true;
        for (String s : params) {
            number = number && NumberUtils.isNumber(s);
        }
        return number;
    }


    public static BigDecimal decimal(String param) {

        return new BigDecimal(param);
    }


    public static <T> T convert(Object obj, Class<T> clazz) {

        Object result = ConvertUtils.convert(obj, clazz);

        if (clazz.isInstance(result)) {
            return (T) result;
        }

        return null;

    }


    public static boolean equal(String str1, String str2) {

        return compare(str1, str2) == 0;
    }


    public static int compare(String str1, String str2) {

        if (str1 == null || str2 == null) {
            return (str1 == null && str2 == null) ? 0 : str1 != null ? 1 : -1;
        }


        if (isNumber(str1, str2)) {

            return decimal(str1).compareTo(decimal(str2));
        } else {
            return str1.compareTo(str2);
        }
    }


}