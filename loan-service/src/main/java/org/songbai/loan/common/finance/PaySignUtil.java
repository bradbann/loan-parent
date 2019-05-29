package org.songbai.loan.common.finance;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author: wjl
 * @date: 2018/11/20 15:29
 * Description: 支付时用到的签名工具
 */
public class PaySignUtil {
	private static final Logger log = LoggerFactory.getLogger(PaySignUtil.class);

	/**
	 * 将map按照ASCII码从小到大排序，并除去空值和参数 然后生成签名
	 *
	 * @param map
	 * @return
	 */
	public static String getFinalMap(Map<String, String> map) {
		try {
			List<Map.Entry<String, String>> list = new ArrayList<Map.Entry<String, String>>(map.entrySet());
			// 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
			Collections.sort(list, new Comparator<Map.Entry<String, String>>() {

				public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
					return (o1.getKey()).toString().compareTo(o2.getKey());
				}
			});

			// 构造签名键值对的格式
			StringBuilder sb = new StringBuilder();
			for (Map.Entry<String, String> item : list) {
				if (StringUtils.isNotBlank(item.getKey())) {
					String key = item.getKey();
					String val = item.getValue();
					if (StringUtils.isNotBlank(val)) {
						sb.append(key + "=" + val + "&");
					}
				}
			}
			String result = sb.toString();
			result = result.substring(0, result.length() - 1);
//			log.info("排序后的参数为：【{}】", result);
			return result;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 生成畅捷支付签名
	 */
	public static String generateSign(String param, String privateKey, String charset) {
		try {
			return RSA.sign(param, privateKey, charset);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("生成畅捷签名失败！");
			return null;
		}
	}
}
