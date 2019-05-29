package org.songbai.loan.model.sms;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class PushGroupModel {

    private Set<Integer> vestIds;//推送的马甲集合

    private String msg;//内容

    private Integer classify;// 系统消息

    private String title;//标题

    private String url;//跳转的地址

    private Integer isJump = 0;//是否跳转 1跳转

    private Integer type;//类型

    private Integer subType;//子类型

    private Set<String> deviceIds;//要推送的用户组群

    /**
     * 适用范围：1安卓,2ios,3web,4客户端
     */
    private List<Integer> scopes;

    private JSONObject condition;//条件集合:根据自己的具体业务来操作
}

