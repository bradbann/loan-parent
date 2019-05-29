package org.songbai.loan.constant.resp;

import org.songbai.cloud.basics.mvc.RespCode;

/**
 * Author: qmw
 * Date: 2018/12/17 1:56 PM
 */
public class ActivityRespCode extends RespCode {
    public static final int ACTIVITY_CODE_EXIST = 7001; // 活动code已存在
    public static final int ACTIVITY_HAS_START = 7002; // 已有启用的活动
    public static final int ACTIVITY_CAN_NOT_OPT = 7003; // 请先停用后再操作
}
