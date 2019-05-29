package org.songbai.loan.admin.channel.model.vo;

import lombok.Data;
import org.songbai.loan.model.channel.AgencyChannelModel;

@Data
public class AgencyChannelVo extends AgencyChannelModel {
    String vestName;
    String agencyName;
    String floorName;
}
