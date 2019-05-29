package org.songbai.loan.common.util;

import java.util.Date;

import org.apache.commons.lang3.time.FastDateFormat;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;

/**
 * 生成照片名称
 * @author wjl
 * @date 2018年11月06日 19:57:00
 * @description
 */
public class ImageNameUtil {
	private static String generateDateKey() {
        Date date = new Date();
        String month = SimpleDateFormatUtil.dateToString(date, FastDateFormat.getInstance("yyyyMM"));
        String days = SimpleDateFormatUtil.dateToString(date, SimpleDateFormatUtil.DATE_FORMAT1);
        return month + "/" + days;
    }
	
	private static String getImgPrefix(Integer userId,String type) {
        return userId+type;
    }

    private static String imgUrl(String type, Integer userId) {
        return generateDateKey() + "/" + getImgPrefix(userId,type);
    }
}
