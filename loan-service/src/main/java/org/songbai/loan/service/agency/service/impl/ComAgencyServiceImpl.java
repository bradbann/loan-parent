package org.songbai.loan.service.agency.service.impl;

import org.apache.commons.lang.StringUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.RespCode;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.http.HeaderKit;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.rediskey.AdminRedisKey;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.model.agency.AgencyConfigModel;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.dao.ComAgencyDao;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Service
public class ComAgencyServiceImpl implements ComAgencyService {
    @Autowired
    ComAgencyDao comAgencyDao;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public AgencyConfigModel findInfoByAmount(Integer amount, Integer agencyId) {
        return comAgencyDao.findInfoByAmount(agencyId, amount);
    }

    @Override
    public Integer findAgencyIdByRequest(HttpServletRequest request) {
        String host = HeaderKit.getHost(request);
        if (StringUtils.isEmpty(host)) {
            throw new BusinessException(AdminRespCode.HOST_ILLEGAL);
        }

        Integer agencyId = this.findAgencyIdByHost(host);

        //代理状态校验
        AgencyModel model = (AgencyModel) redisTemplate.opsForHash().get(AdminRedisKey.AGENCY_INFO, agencyId);
        if (model == null) {
            AgencyModel info = comAgencyDao.selectById(agencyId);
            if (info == null) throw new BusinessException(RespCode.SERVER_ERROR, "该代理不存在");
            if (info.getStatus().equals(CommonConst.STATUS_INVALID))
                throw new BusinessException(RespCode.SERVER_ERROR, "该代理已禁用，请联系管理员");
            redisTemplate.opsForHash().put(AdminRedisKey.AGENCY_INFO, agencyId, info);
        }

        return agencyId;
    }

    private Integer findAgencyIdByHost(String host) {
        Integer agencyId = (Integer) redisTemplate.opsForHash().get(AdminRedisKey.AGENCY_INFO, host);
        if (agencyId != null) return agencyId;

        agencyId = comAgencyDao.findAgencyIdByHost(host);
        if (agencyId == null) throw new BusinessException(AdminRespCode.HOST_ILLEGAL);
        redisTemplate.opsForHash().put(AdminRedisKey.AGENCY_INFO, host, agencyId);
        return agencyId;
    }

    @Override
    public AgencyModel findAgencyById(Integer agencyId) {
        AgencyModel model = (AgencyModel) redisTemplate.opsForHash().get(AdminRedisKey.AGENCY_INFO, agencyId);
        if (model == null) {
            AgencyModel info = comAgencyDao.selectById(agencyId);
            redisTemplate.opsForHash().put(AdminRedisKey.AGENCY_INFO, agencyId, info);
            return info;
        }
        return model;
    }

    @Override
    public AgencyModel getAgencyInfoByHotst(HttpServletRequest request) {
        String host = HeaderKit.getHost(request);
        if (StringUtils.isEmpty(host)) {
            throw new BusinessException(AdminRespCode.HOST_ILLEGAL);
        }

        Integer agencyId = (Integer) redisTemplate.opsForHash().get(AdminRedisKey.AGENCY_INFO, host);
        if (agencyId == null ){
            agencyId = comAgencyDao.findAgencyIdByHost(host);
            if (agencyId == null) throw new BusinessException(AdminRespCode.HOST_ILLEGAL);
            redisTemplate.opsForHash().put(AdminRedisKey.AGENCY_INFO, host, agencyId);
        }

        AgencyModel model = (AgencyModel) redisTemplate.opsForHash().get(AdminRedisKey.AGENCY_INFO, agencyId);
        if (model == null) {
            AgencyModel info = comAgencyDao.selectById(agencyId);
            redisTemplate.opsForHash().put(AdminRedisKey.AGENCY_INFO, agencyId, info);
        }

        return model;
    }

    @Override
    public Integer getChannelIdByLandCode(String landCode, Integer agencyId) {
        Integer channelId = comAgencyDao.getChannelIdByLandCode(landCode, agencyId);
        if (channelId == null) {
            channelId = comAgencyDao.getDefualtChannelId(agencyId);
        }
        return channelId;
    }

    @Override
    public AgencyChannelModel findChannelByLandCode(String landCode, Integer agencyId) {
        AgencyChannelModel model = comAgencyDao.findChannelByLandCode(landCode, agencyId);
        if (model == null) {
            model = comAgencyDao.findDefualtChannel(agencyId);
        }
        return model;
    }

    @Override
    public AppVestModel findVestByIdOrVestCode(Integer agencyId, Integer vestId, String vestCode) {
        AppVestModel model;
        if (StringUtil.isEmpty(vestCode) && vestId == null) {

            return comAgencyDao.findDefualtVest(agencyId);
        }
        model = comAgencyDao.findVestByIdOrVestCode(agencyId, vestId, vestCode);
        if (model == null) {
            model = comAgencyDao.findDefualtVest(agencyId);
        }
        return model;
    }

    @Override
    public AppVestModel findOneVestByIdOrVestCode(Integer agencyId, Integer vestId, String vestCode) {
        if (StringUtil.isEmpty(vestCode) && vestId == null) {
            return comAgencyDao.findDefualtVest(agencyId);
        }
        return comAgencyDao.findVestByIdOrVestCode(agencyId, vestId, vestCode);
    }

    @Override
    public Integer findVestIdByVestCode(Integer agencyId, String vestCode) {
        AppVestModel model = comAgencyDao.findVestInfoByVestCode(agencyId, vestCode);
        if (model == null) {
            model = comAgencyDao.findDefualtVest(agencyId);
        }
        if (model != null) return model.getId();
        return null;
    }

    @Override
    public AppVestModel getVestInfoByVestId(Integer vestId) {
        AppVestModel model = (AppVestModel) redisTemplate.opsForHash().get(AdminRedisKey.AGENCY_VEST, vestId);
        if (model == null) {
            model = comAgencyDao.getVestInfoByVestId(vestId);
            if (model != null)
                redisTemplate.opsForHash().put(AdminRedisKey.AGENCY_VEST, vestId, model);
        }
        return model;
    }

    @Override
    public AgencyChannelModel findChannelNameByAgencyIdAndChannelCode(Integer agencyId, String channelCode) {
        AgencyChannelModel model = (AgencyChannelModel) redisTemplate.opsForHash().get(AdminRedisKey.AGENCY_CHANNEL + agencyId, channelCode);
        if (model == null) {
            model = comAgencyDao.findChannelNameByAgencyIdAndChannelCode(agencyId, channelCode);
            if (model != null) {
                redisTemplate.opsForHash().put(AdminRedisKey.AGENCY_CHANNEL + agencyId, channelCode, model);
            }
        }
        return model;
    }

    @Override
    public AgencyChannelModel findOneChannelByLandCode(String landCode, Integer agencyId) {
        return comAgencyDao.findChannelByLandCode(landCode, agencyId);
    }

}
