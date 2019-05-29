package org.songbai.loan.admin.channel.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.channel.model.po.ChannelQueryPo;
import org.songbai.loan.admin.channel.model.vo.AgencyChannelVo;
import org.songbai.loan.admin.channel.model.vo.ChannelUserVo;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public interface ChannelService {
    void addChannel(AgencyChannelModel model);

    Page<AgencyChannelVo> findAgencyChannelPage(AgencyChannelModel model, AdminUserModel userModel, Integer page, Integer pageSize);

    void upateChannel(AgencyChannelModel model);

    AgencyChannelModel findInfoById(Integer id);

    List<AgencyChannelModel> findChannelList(Integer agencyId);

    Page<ChannelUserVo> findMyCustomerPage(ChannelQueryPo po, AdminUserModel userModel);

    List<AgencyChannelVo> findChannelByAgencyId(Integer agencyId);

}
