package org.songbai.loan.admin.agency.service;


import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.model.agency.AgencyConfigModel;
import org.springframework.stereotype.Component;

@Component
public interface AgencyConfigService {
    void addConfig(AgencyConfigModel model, AdminUserModel userModel);

    void updateConfig(AgencyConfigModel model, AdminUserModel userModel);

    Page<AgencyConfigModel> findAgencyConfigPage(AgencyConfigModel model, AdminUserModel userModel, Integer page, Integer pageSize);

    AgencyConfigModel findInfoById(Integer id);
}
