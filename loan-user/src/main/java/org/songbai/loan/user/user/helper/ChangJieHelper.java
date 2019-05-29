package org.songbai.loan.user.user.helper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.songbai.cloud.basics.utils.date.SimpleDateFormatUtil;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.model.finance.PlatformConfig;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author: wjl
 * @date: 2018/12/11 23:19
 * Description: 畅捷公共的
 */
@Component
public class ChangJieHelper {

    @Autowired
    private ComFinanceService comFinanceService;

    /**
     * 获取支付平台的支付配置
     */
    public PlatformConfig getConfig(Integer agencyId) {
        PlatformConfig config = comFinanceService.getPayPlatformConfig(agencyId);
        return JSON.parseObject(config.getParam(), PlatformConfig.class);
    }


    /**
     * 获取支付平台的支付配置
     */
	public PlatformConfig getAlreadyReqConfig(Integer agencyId) {
		return comFinanceService.getPlatformConfig(agencyId, FinanceConstant.PayPlatform.CHANGJIE.code);
    }

    /**
     * 畅捷公共返回参数设置
     */
    public Map<String, String> getCommonMap(JSONObject jsonObject) {
        Map<String, String> map = new HashMap<>();
        map.put("PartnerId", jsonObject.getString("PartnerId"));
        map.put("InputCharset", jsonObject.getString("InputCharset"));
        map.put("AcceptStatus", jsonObject.getString("AcceptStatus"));
        map.put("TradeDate", jsonObject.getString("TradeDate"));
        map.put("TradeTime", jsonObject.getString("TradeTime"));
        map.put("Status", jsonObject.getString("Status"));
        map.put("RetCode", jsonObject.getString("RetCode"));
        map.put("RetMsg", jsonObject.getString("RetMsg"));
        map.put("AppRetcode", jsonObject.getString("AppRetcode"));
        map.put("AppRetMsg", jsonObject.getString("AppRetMsg"));
        return map;
    }

    /**
     * 畅捷公共请求参数设置
     */
    public Map<String, String> setCommonMap(Map<String, String> map, String sellId) {
        map.put("Version", "1.0");
        map.put("PartnerId", sellId);
        map.put("InputCharset", "UTF-8");// 字符集
        Date date = new Date();
        map.put("TradeDate", SimpleDateFormatUtil.dateToString(date, SimpleDateFormatUtil.DATE_FORMAT1));// 商户请求时间
        map.put("TradeTime", new SimpleDateFormat("HHmmss").format(date));// 商户请求时间
        return map;
    }
}
