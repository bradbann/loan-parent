package org.songbai.loan.common.helper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Author: qmw
 * Date: 2018/11/2 3:43 PM
 * 生成订单号
 */
public class OrderIdUtil {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyMMddHHmm");
    private static SimpleDateFormat simpleFormat = new SimpleDateFormat("yyMMdd");

    private static String[] chars = new String[]{"0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};


    // 借款订单开头表示
    public static final String LOAN_HEADER = "L";
    // 放款订单开头
    public static final String PAYMENT_ORDER = "F";
    // 还款订单开头
    public static final String REPAYMENT_ORDER = "H";
    // 代扣订单开头
    public static final String DEDUCT_ORDER = "D";
    //催收订单开头
    public static final String CHASE_ORDER = "C";
    // 绑卡请求号开头
    public static final String REQUEST_ID = "Q";

    /**
     * 借款订单
     *
     * @return
     */
    public static String getLoanId() {
        return LOAN_HEADER + simpleDateFormat.format(new Date()) + generateShortUuid();
    }

    /**
     * 放款订单
     *
     * @return
     */
    public static String getPaymentId() {
        return PAYMENT_ORDER + simpleDateFormat.format(new Date()) + generateShortUuid();
    }

    /**
     * 还款订单
     *
     * @return
     */
    public static String getRepaymentId() {
        return REPAYMENT_ORDER + simpleDateFormat.format(new Date()) + generateShortUuid();
    }
    /**
     * 代扣订单
     *
     * @return
     */
    public static String getAutoRepaymentId() {
        return DEDUCT_ORDER + simpleDateFormat.format(new Date()) + generateShortUuid();
    }

    /**
     * 请求号
     * @return
     */
    public static String getRequestId() {
    	return REQUEST_ID + simpleDateFormat.format(new Date()) + generateShortUuid();
    }

    public static String getChaseId() {
        return CHASE_ORDER + simpleDateFormat.format(new Date()) + generateShortUuid();
    }

    /**
     * 例:120181209
     */
    public static String getStatistic(Integer agencyId) {
        return agencyId + simpleFormat.format(new Date());
    }

    public static String generateShortUuid() {
        StringBuilder shortBuffer = new StringBuilder();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 36]);
        }
        return shortBuffer.toString();
    }

}
