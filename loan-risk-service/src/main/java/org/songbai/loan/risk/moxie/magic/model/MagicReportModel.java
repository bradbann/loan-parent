package org.songbai.loan.risk.moxie.magic.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection = "risk_mx_magic_report")
public class MagicReportModel {


    @Id
    private String id;
    @Indexed
    private String userId;
    private String name;
    private String phone;
    @Indexed
    private String idcard;

    private String data;
    private Date createTime;
    private Date updateTime;



}
