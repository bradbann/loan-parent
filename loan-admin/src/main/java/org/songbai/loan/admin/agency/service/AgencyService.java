package org.songbai.loan.admin.agency.service;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.model.AdminMenuResourceModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.agency.po.AgencyPo;
import org.songbai.loan.model.agency.AgencyModel;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
public interface AgencyService {

    void addAgency(AgencyModel agencyModel, String createOwner, Integer ownerId, HttpServletRequest request);

    void updateAgency(Integer superId, AgencyModel agencyModel);

    AgencyModel findById(Integer id);

    void resetPassword(Integer id, Integer superId);

    void disabledAgencyAccount(List<Integer> ids, Integer disable, Integer superId);

    void updateShareStatus(String ids, Integer shareStatus);

    Page<AgencyModel> list(AgencyPo po, Integer superAgencyId, Integer page, Integer pageSize);

    void disabledSubAgency(Integer id, Integer status, Integer agencyId);

    List<AgencyModel> listAll(AdminUserModel userModel);

    AgencyModel findAgencyById(Integer id, AdminUserModel userModel);

    void deleteResourceByAgencyId(Integer agencyId);

    void saveResourceToAgencyId(Integer agencyId, List<Integer> resourceIds);

    List<AdminMenuResourceModel> getAllMenuPageUrl(Integer agencyId);

    AgencyModel findAgencyByAgencyId(Integer agencyId);

}
