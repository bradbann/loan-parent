package org.songbai.loan.admin.version.model.vo;

import lombok.Data;
import org.songbai.loan.model.version.AppVersionModel;

/**
 * @author hacfox
 * @date 27/10/2017
 */
@Data
public class AppVersionVO extends AppVersionModel {
    private String agencyName;
    private String phone;
    private String vestName;
    private String channelName;


}
