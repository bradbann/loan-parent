package org.songbai.loan.common.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * Author: qmw
 * Date: 2018/11/5 1:15 PM
 */
public class FormatUtil {
    /**
     * 保留两位小数
     */
    private static DecimalFormat decimalFormat = new DecimalFormat("#0.00");


    /**
     * 格式化double
     *
     * @param digit
     * @return
     */
    public static String formatDouble2(Object digit) {
        Double param;
        if (digit instanceof String) {
            param = Double.valueOf(digit.toString());
        } else if (digit instanceof Double) {
            param = (Double) digit;
        }else if (digit instanceof Integer) {
            param = Double.valueOf(digit.toString());

        }else if (digit instanceof BigDecimal) {
            param = ((BigDecimal) digit).doubleValue();
        }else {
            throw new RuntimeException("参数类型错误!!!");
        }
        return decimalFormat.format(param);
    }

}
