package org.songbai.loan.admin.channel.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.BeanUtil;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminUserService;
import org.songbai.loan.admin.agency.dao.AgencyHostDao;
import org.songbai.loan.admin.channel.dao.ActorChannelDao;
import org.songbai.loan.admin.channel.dao.ChannelDao;
import org.songbai.loan.admin.channel.model.po.ChannelQueryPo;
import org.songbai.loan.admin.channel.model.vo.AgencyChannelVo;
import org.songbai.loan.admin.channel.model.vo.ChannelUserVo;
import org.songbai.loan.admin.channel.service.ActorChannelService;
import org.songbai.loan.admin.channel.service.ChannelService;
import org.songbai.loan.admin.version.dao.FloorDao;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.constant.rediskey.AdminRedisKey;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.model.agency.AgencyHostModel;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.channel.ActorChannelModel;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.model.version.FloorModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class ChannelServiceImpl implements ChannelService {
    @Autowired
    ChannelDao channelDao;
    @Autowired
    AgencyHostDao agencyHostDao;
    @Autowired
    ActorChannelService actorChannelService;
    @Autowired
    ComAgencyService comAgencyService;
    @Autowired
    AdminUserService adminUserService;
    @Autowired
    ActorChannelDao actorChannelDao;
    @Autowired
    private FloorDao floorDao;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional
    public void addChannel(AgencyChannelModel model) {
        AgencyChannelModel channelModel = channelDao.getInfoByChannelCode(model.getChannelCode(), null, model.getAgencyId());
        if (channelModel != null) {
            throw new BusinessException(AdminRespCode.CHANNEL_CODE_IS_EXISIT);
        }
        String landCode = createLandCode(model.getAgencyId());

        model.setLandCode(landCode);
        if (StringUtils.isNotEmpty(model.getLandHtml())) {
            String landUrl = crateLandingUrl(model.getAgencyId(), model.getLandHtml(), landCode);
            model.setLandUrl(landUrl);
        }

        channelDao.insert(model);

        AdminUserModel userModel = adminUserService.getAdminUserByAgencyId(model.getAgencyId());
        if (userModel != null) {
            ActorChannelModel actorChannelModel = new ActorChannelModel();
            actorChannelModel.setChannelId(model.getId());
            actorChannelModel.setActorId(userModel.getId());
            actorChannelModel.setAgencyId(model.getAgencyId());
            actorChannelDao.insert(actorChannelModel);
        }
    }

    private String createLandCode(Integer agencyId) {
        String landCode = OrderIdUtil.generateShortUuid();
        if (checkLandCode(landCode, agencyId)) {
            createLandCode(agencyId);
        }
        return landCode;
    }

    private boolean checkLandCode(String landCode, Integer agencyId) {
        AgencyChannelModel param = new AgencyChannelModel();
        param.setLandCode(landCode);
        param.setAgencyId(agencyId);
        Integer count = channelDao.getChannelCount(param);
        return count > 0;
    }

    private String crateLandingUrl(Integer agencyId, String landHtml, String landCode) {
        String url = landHtml + "?landCode=" + landCode;
        StringBuilder sb = new StringBuilder();
        List<AgencyHostModel> hostModelList = agencyHostDao.findHostListByAgencyId(agencyId);
        if (CollectionUtils.isNotEmpty(hostModelList)) {

            for (AgencyHostModel hostModel : hostModelList) {
                String landUrl = hostModel.getHost() + url;
                sb.append(landUrl).append(",");
            }
            if (sb.length() > 0) {
                return sb.substring(0, sb.length() - 1);
            }
        }
        return null;
    }

    @Override
    public Page<AgencyChannelVo> findAgencyChannelPage(AgencyChannelModel model, AdminUserModel userModel, Integer page, Integer pageSize) {
        Integer count = channelDao.getChannelCount(model);
        if (count == 0) return new Page<>(page, pageSize, count, new ArrayList<>());
        Integer limit = page * pageSize;
        List<AgencyChannelModel> list = channelDao.findChannelList(model, limit, pageSize);
        List<AgencyChannelVo> voList = new ArrayList<>();
        list.forEach(e -> {
            AgencyChannelVo vo = new AgencyChannelVo();
            BeanUtil.copyNotNullProperties(e, vo);
            if (vo.getAgencyId() != null) {
                AgencyModel agencyModel = comAgencyService.findAgencyById(vo.getAgencyId());
                if (agencyModel != null) {
                    vo.setAgencyName(agencyModel.getAgencyName());
                }
            }
            if (vo.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(vo.getVestId());
                if (vestModel != null) {
                    vo.setVestName(vestModel.getName());
                }
            }
            if (StringUtil.isNotEmpty(e.getLandHtml())) {
                FloorModel f = floorDao.selectFloorByUrl(e.getLandHtml(),e.getAgencyId());
                if (f != null) {
                    vo.setFloorName(f.getFloorName());
                }
            }
            voList.add(vo);
        });
        return new Page<>(page, pageSize, count, voList);
    }

    @Override
    public void upateChannel(AgencyChannelModel model) {
        AgencyChannelModel oldModel = channelDao.selectById(model.getId());
        if (oldModel == null) {
            throw new BusinessException(AdminRespCode.CHANNEL_IS_NOT_EXISIT);
        }

        if (StringUtils.isNotBlank(model.getLandHtml()) && !model.getLandHtml().equals(oldModel.getLandHtml())) {
            String landUrl = crateLandingUrl(oldModel.getAgencyId(), model.getLandHtml(), oldModel.getLandCode());
            model.setLandUrl(landUrl);
        }

        if (model.getShowPercent().compareTo(oldModel.getShowPercent()) != 0) {
            redisTemplate.opsForHash().delete(UserRedisKey.USER_CHANNEL_STATIS_LIST + oldModel.getAgencyId(), oldModel.getId());
        }
        channelDao.updateById(model);
        Set<String> keys = redisTemplate.keys(AdminRedisKey.AGENCY_CHANNEL + "*");
        redisTemplate.delete(keys);
    }

    @Override
    public AgencyChannelModel findInfoById(Integer id) {
        return channelDao.selectById(id);
    }

    @Override
    public List<AgencyChannelModel> findChannelList(Integer agencyId) {
        return channelDao.findChannelCodeList(agencyId);
    }

    @Override
    public Page<ChannelUserVo> findMyCustomerPage(ChannelQueryPo po, AdminUserModel userModel) {
        List<AgencyChannelModel> channelModelList = actorChannelService.findActorManagerList(userModel.getDataId(), userModel.getId());
        List<Integer> channelIds = new ArrayList<>();
        channelModelList.forEach(e -> channelIds.add(e.getId()));
        if (CollectionUtils.isEmpty(channelIds)) {
            throw new BusinessException(AdminRespCode.PERMISSION_DENIED, "您暂无管理的渠道");
        }
        po.setAgencyId(userModel.getDataId());
        Integer count = channelDao.getMyCustomerCount(po, channelIds);
        if (count == 0) return new Page<>(po.getPage(), po.getPageSize(), count, new ArrayList<>());
        List<ChannelUserVo> list = channelDao.findMyCustomerList(po, channelIds);
        return new Page<>(po.getPage(), po.getPageSize(), count, list);
    }

    @Override
    public List<AgencyChannelVo> findChannelByAgencyId(Integer agencyId) {

        return channelDao.findChannelByAgencyId(agencyId);

    }


}
