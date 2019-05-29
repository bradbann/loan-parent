package org.songbai.loan.user.appConfig.model.vo;

import lombok.Data;

@Data
public class AppVestVo {
    /**
     * 标识
     */
    private String identify;
    /**
     * 版本
     */
    private String version;
    /**
     * 平台 1-android,2-ios , 3-h5
     */
    private Integer platform;

}
