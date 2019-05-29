package org.songbai.loan.model.loan;

import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Author: qmw
 * Date: 2018/12/25 2:21 PM
 */
@TableName("loan_u_join")
@Data
public class JoinModel implements Serializable {
    private Integer id;
    private String mail;//邮箱
    private String phone;//手机

    private Integer status;// 0未处理 1已处理
    private Integer actorId;// 0未删除1 已删除
    private String remark;// 备注

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
