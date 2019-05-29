package org.songbai.loan.common.util;

import lombok.Data;

/**
 * Author: qmw
 * Date: 2018/11/2 3:10 PM
 */
@Data
public class PageRow {
    private int pageSize = 20;
    private Integer page = 0;
    private int limit;

    public void initLimit() {
        limit = page > 0 ? page * pageSize : 0;
    }
}
