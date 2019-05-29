package org.songbai.loan.risk.moxie.taobao.model.vo;


import lombok.Data;

@Data
public class TaobaoReportTask {

    private String taskId; // 任务ID
    private String userId; // 合作机构的用户ID
    private Boolean result; // 报告结果. true - 成功; false - 失败
    private String message; // 如果result是false, message描述失败的原因. 如果result是true, 则message为前台界面展示的加密请求报文。
    private Long timestamp; // UNIX timestamp(毫秒)

}
