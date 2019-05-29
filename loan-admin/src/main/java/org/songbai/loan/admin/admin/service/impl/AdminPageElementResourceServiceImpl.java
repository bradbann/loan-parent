package org.songbai.loan.admin.admin.service.impl;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.admin.dao.AdminResourceAssignmentDao;
import org.songbai.loan.admin.admin.dao.AdminSecurityResourceDao;
import org.songbai.loan.admin.admin.model.AdminPageElementResourceModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminPageElementResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminPageElementResourceServiceImpl implements AdminPageElementResourceService {

    @Autowired
    AdminSecurityResourceDao adminSecurityResourceDao;

    @Autowired
    AdminResourceAssignmentDao adminResourceAssignmentDao;

    @Override
    public void savePageElement(AdminPageElementResourceModel pageElementResourceModel) {

        adminSecurityResourceDao.createPageElement(pageElementResourceModel);
    }

    @Override
    public void updatePageElement(AdminPageElementResourceModel pageElementResourceModel) {
        adminSecurityResourceDao.updatePageElement(pageElementResourceModel);

    }

    @Override
    public void deletePageElement(List<Integer> ids) {
        for (Integer id : ids) {
            /**
             * 删除分配纪录
             */
            adminResourceAssignmentDao.deleteAdminResourceAssignmentsByAuthorityId(id);
            /**
             * 删除页面元素
             */
            adminSecurityResourceDao.updateStatus(id);
        }
    }

    @Override
    public Page<AdminPageElementResourceModel> pagingQueryPageElement(Integer pageIndex, Integer pageSize,
                                                                      Map<String, Object> param) {
        Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0;
        param.put("limit", limit);
        param.put("size", pageSize);
        int resultCount = adminSecurityResourceDao.pagingQueryPageElement_count(param);

        if (resultCount == 0) {
            return new Page<>(pageIndex, pageSize, resultCount, new ArrayList<>());
        }
        List<AdminPageElementResourceModel> data = adminSecurityResourceDao.pagingQueryPageElement(param);
        return new Page<>(pageIndex, pageSize, resultCount, data);
    }

    @Override
    public Page<AdminPageElementResourceModel> pagingQueryNotGrantPageElementsByRoleId(String name, String description,
                                                                                       String identifier, Integer roleId, Integer dataId, Integer pageIndex, Integer pageSize) {
        Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0;
        List<AdminPageElementResourceModel> pageElementResourceModels = adminSecurityResourceDao
                .pagingQueryNotGrantPageElementByRoleId(name, description, identifier, roleId, dataId, limit, pageSize);
        int resultCount = adminSecurityResourceDao.pagingQueryNotGrantPageElementByRoleId_count(name, description,
                identifier, roleId, dataId);
        Page<AdminPageElementResourceModel> page = new Page<>(pageIndex, pageSize, resultCount);
        page.setData(pageElementResourceModels);

        return page;
    }

    @Override
    public boolean hasPageElementByIdentifier(String identifier, Integer type) {

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("identifier", identifier);
        param.put("category", AdminPageElementResourceModel.CATEGORY);
        param.put("type", type);
        return adminSecurityResourceDao.pagingQueryPageElement_count(param) > 0;
    }

    @Override
    public boolean hasRightByActorIdDdentifier(Integer actorId, String identifier, Integer dataId, Integer type,
                                               AdminUserModel user) {
        if (user.getId().equals(0)) {
            AdminPageElementResourceModel adminPageElementResourceModel = adminSecurityResourceDao
                    .getPagelementByTypeDdentifier(identifier, dataId);
            return adminPageElementResourceModel != null && adminPageElementResourceModel.getId() != null;
        } else {
            AdminPageElementResourceModel pageElementResourceModel = adminSecurityResourceDao
                    .getPagelementByActorIdIdDdentifier(actorId, identifier, dataId);
            return pageElementResourceModel != null && pageElementResourceModel.getId() != null;
        }
    }

    @Override
    public List<String> getPageElementByActorId(Integer actorId, AdminUserModel user) {
        List<String> pageElementResourcesAll = new ArrayList<>();
        if (user.getId().equals(0)) {
            return adminSecurityResourceDao.getPageElementAll(AdminPageElementResourceModel.CATEGORY);
        } else if (user.getRoleType().equals(1)) {
            pageElementResourcesAll = adminSecurityResourceDao.getPageElementByDeptId(user.getDataId(), null, AdminPageElementResourceModel.CATEGORY, 1);
        } else if (user.getIsManager() != null && user.getIsManager().equals(1)) {
            pageElementResourcesAll = adminSecurityResourceDao.getPageElementByDeptId(user.getDataId(), user.getDeptId(), AdminPageElementResourceModel.CATEGORY, 0);
        }
        List<String> elementList = adminSecurityResourceDao.getPageElementByActorId(actorId, AdminPageElementResourceModel.CATEGORY);
        for (String element : elementList) {
            if (!pageElementResourcesAll.contains(element)) {
                pageElementResourcesAll.add(element);
            }
        }

        return pageElementResourcesAll;
    }

}
