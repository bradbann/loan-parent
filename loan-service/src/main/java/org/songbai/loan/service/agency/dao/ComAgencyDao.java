package org.songbai.loan.service.agency.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.agency.AgencyConfigModel;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.version.AppVestModel;

public interface ComAgencyDao extends BaseMapper<AgencyModel> {

    AgencyModel findDefaultAgency();

    AgencyConfigModel findInfoByAmount(@Param("agencyId") Integer agencyId, @Param("amount") Integer amount);

    Integer findAgencyIdByHost(@Param("host") String host);

    Integer getChannelId(@Param("channelCode") String channelCode, @Param("agencyId") Integer agencyId);

    Integer getDefualtChannelId(@Param("agencyId") Integer agencyId);

    Integer getChannelIdByLandCode(@Param("landCode") String landCode, @Param("agencyId") Integer agencyId);

    AgencyChannelModel findChannelByLandCode(@Param("landCode") String landCode, @Param("agencyId") Integer agencyId);

    AgencyChannelModel findDefualtChannel(@Param("agencyId") Integer agencyId);

    AppVestModel findVestByIdOrVestCode(@Param("agencyId") Integer agencyId,@Param("vestId") Integer vestId, @Param("vestCode") String vestCode);

    AppVestModel findDefualtVest(@Param("agencyId") Integer agencyId);

    AppVestModel findVestInfoByVestCode(@Param("agencyId") Integer agencyId, @Param("vestCode") String vestCode);

    AppVestModel getVestInfoByVestId(@Param("vestId") Integer vestId);

    AgencyChannelModel findChannelNameByAgencyIdAndChannelCode(@Param("agencyId") Integer agencyId, @Param("channelCode") String channelCode);

}
