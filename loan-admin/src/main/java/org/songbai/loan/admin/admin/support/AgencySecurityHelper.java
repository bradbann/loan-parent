package org.songbai.loan.admin.admin.support;


import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminUrlAccessResourceService;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.model.agency.AgencyModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AgencySecurityHelper {


    @Autowired
    AdminUserHelper adminUserHelper;

    @Autowired
    AdminUrlAccessResourceService adminUrlAccessResourceService;


    @Autowired
    SpringProperties properties;


    public Integer getAgencyId(HttpServletRequest request) {

        AdminUserModel model = adminUserHelper.getAdminUser(request);


        return model != null ? model.getDataId() : 0;
    }


    public Integer trimPingtaiToNull(HttpServletRequest request) {

        AdminUserModel model = adminUserHelper.getAdminUser(request);

        int agencyId = model != null ? model.getDataId() : 0;
        return agencyId <= 0 ? null : agencyId;
    }


    public boolean checkIsPingtai(HttpServletRequest request) {

        AgencyModel model = adminUserHelper.getAgency(request);


        return model != null && model.getId() == 0;
    }


    public void onlySuperUser(HttpServletRequest request) {
        if (!checkIsPingtai(request)) {
            throw new ResolveMsgException(AdminRespCode.ACCESS_PINGTAI);
        }

        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        if (userModel.getRoleType() != 1) {
            throw new ResolveMsgException(AdminRespCode.ACCESS_ADMIN);
        }

    }

    public void onlyPingtai(HttpServletRequest request) {

        if (!checkIsPingtai(request)) {
            throw new ResolveMsgException(AdminRespCode.ACCESS_PINGTAI);
        }
    }

    public void onlyAgencyAdmin(HttpServletRequest request) {

        if (!checkIsPingtai(request)) {
            AdminUserModel userModel = adminUserHelper.getAdminUser(request);
            if (userModel.getRoleType() != 1) {
                throw new ResolveMsgException(AdminRespCode.ACCESS_ADMIN);
            }
        }
    }


    public void onlyHaveRole(Integer actorId, String roleIds) {
        Integer[] roleId = StringUtil.split2Int(roleIds);

        boolean isHave = adminUrlAccessResourceService.isHaveRoleForActor(actorId, roleId);

        if (!isHave) {
            throw new ResolveMsgException(AdminRespCode.ACCESS_ROLE);
        }
    }


    public void onlyAgency(HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId == null || agencyId.equals(0)) {
            throw new BusinessException(AdminRespCode.ACCESS_ONLY_AGENCY);
        }
    }

    public void onlyAgencyCommon(HttpServletRequest request) {
        AdminUserModel model = adminUserHelper.getAdminUser(request);
        if (model == null || model.getDataId().equals(0)) {
            throw new BusinessException(AdminRespCode.ACCESS_ONLY_AGENCY);
        }
        if (model.getRoleType().equals(1)) {
            throw new BusinessException(AdminRespCode.ACCESS_ONLY_COMMON);
        }
    }
}
