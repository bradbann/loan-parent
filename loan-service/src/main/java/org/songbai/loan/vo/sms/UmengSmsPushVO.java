package org.songbai.loan.vo.sms;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class UmengSmsPushVO {
    private String ticker;//通知栏提示文字
    private String title;//通知标题
    private String text;//通知文字描述
    private Integer sendType;//短信类型
    private String alias;
    private String aliasType;
    private Integer badge;//默认为0
    private Integer userId;//userId
    //data
    private String deviceId;//设备id
    private Integer platform;//来源，1-安卓，2-ios
    private String umengToken;//设备在友盟下产生的token
    private String lang;//内容版本，zh_CN,en,zh_HK
    private Integer goAppAfterOpen;//0-打开app,1-跳转url，2-打开特定的activity，3-用户自定义内容。
    private String url;//跳转的目标url
    private String activity;//特定的activity
    private String custom;//用户自定义内容。
    private Object extra;
    private Integer contentType;
    //dataType

    private String sendAction;
    private JSONObject sendValue;

    private String uuid;//唯一标识
}
