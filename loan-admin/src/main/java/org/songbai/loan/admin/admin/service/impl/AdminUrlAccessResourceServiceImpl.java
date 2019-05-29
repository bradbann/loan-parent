package org.songbai.loan.admin.admin.service.impl;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.dao.AdminResourceAssignmentDao;
import org.songbai.loan.admin.admin.dao.AdminSecurityResourceDao;
import org.songbai.loan.admin.admin.model.AdminSecurityResourceModel;
import org.songbai.loan.admin.admin.model.AdminUrlAccessResourceModel;
import org.songbai.loan.admin.admin.service.AdminUrlAccessResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author wangd
 */
@Service
public class AdminUrlAccessResourceServiceImpl implements AdminUrlAccessResourceService {

    @Autowired
    AdminSecurityResourceDao adminSecurityResourceDao;
    @Autowired
    AdminResourceAssignmentDao adminResourceAssignmentDao;

    @Override
    public void saveUrlAccess(AdminUrlAccessResourceModel accessResourceModel) {
        adminSecurityResourceDao.createUrlAccess(accessResourceModel);

    }

    @Override
    public void updateUrlAccess(AdminUrlAccessResourceModel accessResourceModel) {
        adminSecurityResourceDao.updateUrlAccess(accessResourceModel);

    }

    @Override
    public void deleteUrlAccess(List<Integer> ids) {
        for (Integer id : ids) {
            adminSecurityResourceDao.updateStatus(id);
            adminResourceAssignmentDao.deleteAdminResourceAssignmentsByAuthorityId(id);
        }

    }

    @Override
    public Page<AdminUrlAccessResourceModel> pagingQueryPermissions(Integer pageIndex, Integer pageSize,
                                                                    Map<String, Object> param) {
        Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0;
        param.put("limit", limit);
        param.put("size", pageSize);

        List<AdminUrlAccessResourceModel> data = adminSecurityResourceDao.pagingQueryUrlAccess(param);
        int resultCount = adminSecurityResourceDao.pagingQueryUrlAccess_count(param);
        Page<AdminUrlAccessResourceModel> page = new Page<>(pageIndex, pageSize, resultCount);
        page.setData(data);
        return page;
    }

//	@Override
//	public void terminateUrlAccessResourcesFromRole(Integer roleId, List<Integer> urlAccessResourceIdList,
//			Integer dataId) {
//
//		adminResourceAssignmentDao.deleteAdminResourceAssignments(roleId, urlAccessResourceIdList, dataId);
//	}

    @Override
    public boolean hasUrlAccessByUrlAddress(String url, Integer type) {

        Map<String, Object> param = new HashMap<String, Object>();

        param.put("url", url);
        param.put("type", type);
        param.put("category", AdminUrlAccessResourceModel.CATEGORY);

        return adminSecurityResourceDao.pagingQueryUrlAccess_count(param) > 0;
    }

    @Override
    public List<AdminUrlAccessResourceModel> getUrlAccessResourcesByActorId(Integer actorId, Integer dataId) {

        return adminSecurityResourceDao.getUrlAccessResourcesByActorId(actorId, AdminUrlAccessResourceModel.CATEGORY,
                dataId);
    }


    @Override
    public List<AdminUrlAccessResourceModel> getUrlAccessByRoleId(Integer roleId, Integer dataId) {
        List<AdminUrlAccessResourceModel> urlAccessModels = adminSecurityResourceDao
                .getAllUrlAccessByRoleId(AdminUrlAccessResourceModel.CATEGORY, roleId, dataId);
        List<AdminUrlAccessResourceModel> checkedUrlAccess = new ArrayList<AdminUrlAccessResourceModel>();
        for (AdminUrlAccessResourceModel urlAccessModel : urlAccessModels) {
            urlAccessModel.setChecked(true);
            checkedUrlAccess.add(urlAccessModel);
        }
        return checkedUrlAccess;
    }

    @Override
    public List<AdminSecurityResourceModel> getAllByMenuId(Integer menuId, String category, Integer type) {
        return adminSecurityResourceDao.getAllByMenuId(menuId, category, type);
    }

    @Override
    public List<AdminSecurityResourceModel> getSecurityResourcesByActorId(Integer actorId, Integer dataId, String category) {
        return adminSecurityResourceDao.getSecurityResourcesByActorId(actorId, dataId, category);
    }

    @Override
    public List<AdminSecurityResourceModel> getAllByMenuIdByCategory(String category, Integer id) {

        return adminSecurityResourceDao.getAllByMenuIdByCategory(category, id);
    }

    @Override
    public List<AdminSecurityResourceModel> getAllByMenuIdByCategoryForSuperMan(String category) {
        return adminSecurityResourceDao.getAllByMenuIdByCategoryForSuperMan(category);
    }


    @Override
    public boolean isHaveRoleForActor(Integer actorId, Integer[] authorityIds) {
        if (actorId == null || authorityIds == null || authorityIds.length == 0) {
            return false;
        }

        int count = adminSecurityResourceDao.isHaveRoleForActor(actorId, Arrays.asList(authorityIds));
        return count > 0 ? true : false;
    }

    @Override
    public List<AdminSecurityResourceModel> getAllByMenuIdByCategoryAndDeptId(String category, Integer deptId, Integer agencyId, int type) {
        return adminSecurityResourceDao.getAllByMenuIdByCategoryAndDeptId(category, deptId, agencyId, type, null);
    }


}
