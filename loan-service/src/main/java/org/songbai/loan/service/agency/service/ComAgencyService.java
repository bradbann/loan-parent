package org.songbai.loan.service.agency.service;

import org.songbai.loan.model.agency.AgencyConfigModel;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.version.AppVestModel;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public interface ComAgencyService {

    AgencyConfigModel findInfoByAmount(Integer amount, Integer agencyId);

    Integer findAgencyIdByRequest(HttpServletRequest request);

    AgencyModel findAgencyById(Integer agencyId);

    AgencyModel getAgencyInfoByHotst(HttpServletRequest request);

    Integer getChannelIdByLandCode(String landCode, Integer agencyId);

    AgencyChannelModel findChannelByLandCode(String landCode, Integer agencyId);

    AppVestModel findVestByIdOrVestCode(Integer agencyId, Integer vestId, String vestCode);
    AppVestModel findOneVestByIdOrVestCode(Integer agencyId, Integer vestId, String vestCode);

    Integer findVestIdByVestCode(Integer agencyId, String vestCode);

    /**
     * 获取马甲信息
     */
    AppVestModel getVestInfoByVestId(Integer vestId);

    AgencyChannelModel findChannelNameByAgencyIdAndChannelCode(Integer agencyId, String channelCode);

    AgencyChannelModel findOneChannelByLandCode(String landCode, Integer agencyId);

}
