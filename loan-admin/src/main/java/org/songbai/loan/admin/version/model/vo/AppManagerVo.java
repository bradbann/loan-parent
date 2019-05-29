package org.songbai.loan.admin.version.model.vo;

import lombok.Data;
import org.songbai.loan.model.version.AppManagerModel;

@Data
public class AppManagerVo extends AppManagerModel {
    String vestName;
    private String agencyName;
}
