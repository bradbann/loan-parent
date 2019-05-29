package org.songbai.loan.admin.order.vo;

import lombok.Data;

import java.util.Date;

/**
 * Author: qmw
 * Date: 2018/12/27 2:27 PM
 */
@Data
public class OptListVO {
    private String actorName;
    private String statusName;
    private String stageName;
    private String remark;
    private Date createTime;
}
