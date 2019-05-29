package org.songbai.loan.common.util;

import org.apache.commons.lang3.StringUtils;

/**
 * @author: wjl
 * @date: 2018/12/14 14:17
 * Description: 银行卡号校验、处理，包含脱敏、获取前六位和后四位
 */
public class BankCardUtil {

	/**
	 * 匹配银行卡
	 * @param cardNo
	 * @return
	 */
	public static boolean matchLuhn(String cardNo) {
		try {
			int[] cardNoArr = new int[cardNo.length()];
			for (int i = 0; i < cardNo.length(); i++) {
				cardNoArr[i] = Integer.valueOf(String.valueOf(cardNo.charAt(i)));
			}
			for (int i = cardNoArr.length - 2; i >= 0; i -= 2) {
				cardNoArr[i] <<= 1;
				cardNoArr[i] = cardNoArr[i] / 10 + cardNoArr[i] % 10;
			}
			int sum = 0;
			for (int i = 0; i < cardNoArr.length; i++) {
				sum += cardNoArr[i];
			}
			return sum % 10 == 0;
		} catch (Exception e) {
			return false;
		}
	}

	public static String hideCardNo(String cardNo){
		if (StringUtils.isBlank(cardNo)) {
			return cardNo;
		}

		int length = cardNo.length();
		int beforeLength = 6;
		int afterLength = 4;
		//替换字符串，当前使用“*”
		String replaceSymbol = "*";
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			if (i < beforeLength || i >= (length - afterLength)) {
				sb.append(cardNo.charAt(i));
			} else {
				sb.append(replaceSymbol);
			}
		}

		return sb.toString();
	}

	public static String startSix(String cardNo){
		return cardNo.substring(0, 6);
	}

	public static String endFour(String cardNo){
		return cardNo.substring(cardNo.length() - 4);
	}
}
