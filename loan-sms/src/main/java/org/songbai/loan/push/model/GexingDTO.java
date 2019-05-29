package org.songbai.loan.push.model;

import lombok.Data;
import org.songbai.loan.model.sms.PushSenderModel;

/**
 * Author: qmw
 * Date: 2018/11/23 3:07 PM
 */
@Data
public class GexingDTO extends PushSenderModel {
    private Long expired;//过期时间
    private Integer exist;//0 不存在 1存在
}
