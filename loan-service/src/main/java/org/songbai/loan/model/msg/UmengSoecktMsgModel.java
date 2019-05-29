package org.songbai.loan.model.msg;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class UmengSoecktMsgModel {
    private Integer userId;
    private String title;//标题
    private String text;//内容
    private String sendAction;
    private JSONObject sendValue;
    private Integer contentType;
    private String uuid;

}
