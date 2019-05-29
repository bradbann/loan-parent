package org.songbai.loan.admin.news.model.po;

import lombok.Data;
import org.songbai.loan.common.util.PageRow;

@Data
public class UserFeedPo extends PageRow {
    String userName;
    String userPhone;
    String startTime;
    String endTime;
    Integer agencyId;
    Integer vestId;
}
