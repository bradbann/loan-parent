package org.songbai.loan.admin.agency.service.impl;

import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.agency.dao.AgencyConfigDao;
import org.songbai.loan.admin.agency.service.AgencyConfigService;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.model.agency.AgencyConfigModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AgencyConfigServiceImpl implements AgencyConfigService {
    @Autowired
    AgencyConfigDao agencyConfigDao;
    @Autowired
    ComAgencyService comAgencyService;

    @Override
    public void addConfig(AgencyConfigModel model, AdminUserModel userModel) {
        AgencyConfigModel agencyConfigModel = comAgencyService.findInfoByAmount(model.getAmount(), userModel.getDataId());
        if (agencyConfigModel != null) {
            throw new BusinessException(AdminRespCode.AGENCY_AMOUNT_EXISIT);
        }
        agencyConfigDao.insert(model);
    }

    @Override
    public void updateConfig(AgencyConfigModel model, AdminUserModel userModel) {
        AgencyConfigModel agencyConfigModel = comAgencyService.findInfoByAmount(model.getAmount(), userModel.getDataId());
        if (agencyConfigModel != null && !model.getId().equals(agencyConfigModel.getId())) {
            throw new BusinessException(AdminRespCode.AGENCY_AMOUNT_EXISIT);
        }
        agencyConfigDao.updateById(model);
    }

    @Override
    public Page<AgencyConfigModel> findAgencyConfigPage(AgencyConfigModel model, AdminUserModel userModel, Integer page, Integer pageSize) {
        Integer limit = page * pageSize;
        if (userModel.getId().equals(0)) {
            userModel.setDataId(null);
        }
        Integer count = agencyConfigDao.findAgencyConfigCount(model, userModel.getDataId());
        if (count == 0) {
            return new Page<>(limit, pageSize, count, new ArrayList<>());
        }
        List<AgencyConfigModel> list = agencyConfigDao.findAgencyConfigPage(model, userModel.getDataId(), limit, pageSize);

        return null;
    }

    @Override
    public AgencyConfigModel findInfoById(Integer id) {
        return agencyConfigDao.selectById(id);
    }


}
