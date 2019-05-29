package org.songbai.loan.risk.moxie.taobao.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "risk_mx_tb_reportdata")
public class TaobaoReportModel {


    @Id
    private String id;
    @Indexed
    private String taskId; // 任务ID
    @Indexed
    private String userId; // 合作机构的用户ID

    private String reportData; //

    private String message; // 如果result是false, message描述失败的原因. 如果result是true, 则message为前台界面展示的加密请求报文。

    private Date createTime;
    private Date updateTime;

}
