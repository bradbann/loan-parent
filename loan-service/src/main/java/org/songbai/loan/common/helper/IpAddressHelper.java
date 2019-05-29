package org.songbai.loan.common.helper;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.cloud.basics.utils.http.HttpTools;
import org.songbai.cloud.basics.utils.http.IpUtil;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Locale;


@Component
public class IpAddressHelper {


    public String getRegion(String ip) {

        JSONObject address = getAddress(ip);

        if (address != null) {
            return shortTrimToEmpty(address.getString("country")) +
                    " " +
                    shortTrimToEmpty(address.getString("city"));
        }

        return "";
    }

    public String getCountry(String ip) {

        JSONObject address = getAddress(ip);

        if (address != null) {
            return shortTrimToEmpty(address.getString("country"));
        }

        return "";
    }


    public String getCountryCode(HttpServletRequest request) {

        String ip = IpUtil.getIp(request);

        JSONObject address = getAddress(ip);

        if(address != null){
            String countryCode = address.getString("countryCode");

            if (StringUtils.isNotEmpty(countryCode)) {

                if (countryCode.matches("^[a-zA-Z]{2}$")) {
                    return countryCode;
                }
            }
        }

        Locale locale = LocaleKit.getLocale(request);

        return locale.getCountry();
    }


    public String getCountryCode(String ip) {

        JSONObject address = getAddress(ip);

        return address != null ? address.getString("countryCode") : null;
    }


    private JSONObject getAddress(String ip) {
        HashMap<String, String> param = new HashMap<>();
        param.put("ip", ip);
        String result = HttpTools.doGet("http://lemi.esongbai.com/prevent/ip/find.do", param);
        JSONObject jsonObject = JSONObject.parseObject(result);
        if (jsonObject.getInteger("code") == 200) {
            return jsonObject.getJSONObject("data");
        }
        return null;
    }


    private String shortTrimToEmpty(String data) {

        data = StringUtils.trimToEmpty(data);

        return data.getBytes().length < 4 ? "" : data;
    }




}
