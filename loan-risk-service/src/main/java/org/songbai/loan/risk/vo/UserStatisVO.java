package org.songbai.loan.risk.vo;

import lombok.Data;

/**
 * Author: qmw
 * Date: 2018/11/20 7:34 PM
 */
@Data
public class UserStatisVO {


    private Integer age;

    private Integer inBlackList = 0;//是否黑名单
    private Integer inWhiteList = 0;//是否白名单
    private Integer inGrayList = 0;//是否灰名单
    private Integer customRiskRefuse = 0;//人工复审拒绝次数
    private Integer repeatOrderSuccess = 0;// 用户复借成功次数
    private Integer lastOrderExceedDays = 0;//最后一笔的预期天数
    private Integer exceedCount = 0;//累计预期次数
    private Integer exceedThan3Days = 0;//累计逾期3天及以上次数
    private Integer exceedThan10Days = 0;//累计逾期10天及以上次数
    private Integer deviceAccountBindCount = 0;//设备绑定过账号数量
    private Integer accountDeviceBindCount = 0;//账号绑定过的设备数量




}
