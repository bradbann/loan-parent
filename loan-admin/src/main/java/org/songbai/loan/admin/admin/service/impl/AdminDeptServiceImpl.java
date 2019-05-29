package org.songbai.loan.admin.admin.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.utils.base.BeanUtil;
import org.songbai.loan.admin.admin.dao.*;
import org.songbai.loan.admin.admin.model.*;
import org.songbai.loan.admin.admin.model.vo.AdminDeptVO;
import org.songbai.loan.admin.admin.service.AdminDeptService;
import org.songbai.loan.admin.admin.service.AdminMenuResouceService;
import org.songbai.loan.admin.admin.service.AdminUrlAccessResourceService;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminDeptServiceImpl implements AdminDeptService {
    @Autowired
    AdminDeptDao adminDeptDao;
    @Autowired
    AdminAuthorizationDao adminAuthorizationDao;
    @Autowired
    AdminActorDao adminActorDao;
    @Autowired
    AdminMenuResouceService adminMenuResouceService;
    @Autowired
    AdminDeptResourceDao deptResourceDao;
    @Autowired
    AdminUrlAccessResourceService adminUrlAccessResourceService;
    @Autowired
    AdminSecurityResourceDao adminSecurityResourceDao;


    @Override
    public void updateDept(AdminDeptModel adminDeptModel) {
        AdminDeptModel model = adminDeptDao.selectById(adminDeptModel.getId());
        if (model == null) {
            throw new BusinessException(AdminRespCode.DEPT_NOT_EXISIT);
        }
        AdminDeptModel param = new AdminDeptModel();
        param.setId(adminDeptModel.getId());
        param.setName(adminDeptModel.getName());
//        param.setMinisterId(adminDeptModel.getMinisterId());
        adminDeptDao.updateById(param);

//        if (param.getMinisterId() != null) {
//            adminActorDao.updateUserDeptById(param.getMinisterId(), param.getId(), 1);
//            if (!model.getMinisterId().equals(param.getMinisterId())) {
//                adminActorDao.updateUserDeptById(model.getMinisterId(), param.getId(), 0);
//            }
//        }

    }

    @Override
    public AdminDeptModel findDeptById(Integer id) {
        return adminDeptDao.selectById(id);
    }

    @Override
    public List<AdminDeptModel> findDeptListByType(AdminUserModel userModel, Integer deptType) {
        AdminDeptModel deptModel = adminDeptDao.selectById(userModel.getDeptId());

//        return adminDeptDao.findDeptListByDeptCode(null, userModel.getDataId(), deptType);
        // 这里限制到不猛出现问题，先去掉 。
        return adminDeptDao.findDeptListByDeptCode(deptModel.getDeptCode(), userModel.getDataId(), deptType);
    }

    @Override
    public List<AdminDeptModel> findAllDeptListByType(Integer agencyId, Integer deptType) {
        return adminDeptDao.findDeptListByDeptCode(null, agencyId, deptType);
    }

    @Override
    public List<Integer> findDeptIdsByType(AdminUserModel userModel, Integer deptType) {
        List<AdminDeptModel> list = this.findDeptListByType(userModel, deptType);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.stream().map(AdminDeptModel::getId).collect(Collectors.toList());
    }

    @Override
    public List<Integer> findAllDeptIdsByType(Integer agencyId, Integer deptType) {
        List<AdminDeptModel> list = this.findAllDeptListByType(agencyId, deptType);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.stream().map(AdminDeptModel::getId).collect(Collectors.toList());
    }

    @Override
    public void deleteById(Integer id) {
        AdminDeptModel model = adminDeptDao.selectById(id);
        if (model == null) {
            throw new BusinessException(AdminRespCode.DEPT_NOT_EXISIT);
        }
        Integer count = adminDeptDao.selectUserCountById(id);
        if (count > 0) {
            throw new BusinessException(AdminRespCode.DEPT_HAVE_USER);
        }

        Integer[] ids = {0, 1, 2, 3};
        if (ArrayUtils.contains(ids, id)) {
            throw new BusinessException(AdminRespCode.DEPT_NOT_DELETE);
        }

        if (model.getDeptLevel() == 0) {
            throw new BusinessException(AdminRespCode.DEPT_NOT_DELETE);
        }

        Integer[] types = {2, 3, 4};
        if (ArrayUtils.contains(types, model.getDeptType()) && model.getDeptLevel() == 1) {
            throw new BusinessException(AdminRespCode.DEPT_NOT_DELETE);
        }
        adminDeptDao.deleteById(id);
    }

    @Override
    public AdminDeptModel findDeptPage(AdminUserModel userModel) {
        AdminDeptModel model = adminDeptDao.selectById(userModel.getDeptId());
        this.createChildeDept(model);
        return model;
    }

    private void createChildeDept(AdminDeptModel adminDeptModel) {
        List<AdminDeptModel> list = adminDeptDao.findDeptListByParentId(adminDeptModel.getId());
        if (CollectionUtils.isNotEmpty(list)) {
            adminDeptModel.setChildDept(list);
            for (AdminDeptModel model : list) {
                this.createChildeDept(model);
            }
        }
    }

    @Override
    public List<AdminDeptModel> findDeptList(AdminUserModel userModel) {
        AdminDeptModel model = adminDeptDao.selectById(userModel.getDeptId());

        return adminDeptDao.findDeptListByDeptCode(model.getDeptCode(), userModel.getDataId(), null);
    }

    private List<AdminDeptModel> handleChildDeptList(AdminDeptModel model) {
        return adminDeptDao.findDeptListByParentId(model.getId());

    }

    private void handleChildDept(AdminDeptModel adminDeptModel) {
        List<AdminDeptModel> childList = adminDeptDao.findDeptListByParentId(adminDeptModel.getId());
        if (CollectionUtils.isNotEmpty(childList)) {
            for (AdminDeptModel model : childList) {
                List<AdminDeptModel> sonsList = adminDeptDao.findDeptListByParentId(model.getId());
                if (CollectionUtils.isNotEmpty(sonsList)) {
                    this.handleChildDept(model);
                }
                model.setChildDept(sonsList);
            }
            adminDeptModel.setChildDept(childList);
        }

    }


    @Override
    @Transactional
    public void saveResourceToDeptId(Integer deptId, List<Integer> resourceList, Integer agencyId) {

        if (CollectionUtils.isNotEmpty(resourceList)) {
            List<AdminDeptResourceModel> list = resourceList.stream().map(resourceId -> {
                AdminDeptResourceModel temp = new AdminDeptResourceModel();
                temp.setDeptId(deptId);
                temp.setResourceId(resourceId);
                temp.setAgencyId(agencyId);

                return temp;
            }).collect(Collectors.toList());


            deptResourceDao.deleteResourceByDeptId(deptId);

            deptResourceDao.createAdminDeptResource(list);
        }


    }


    @Override
    public void deleteResourceByDeptId(Integer deptId) {

        deptResourceDao.deleteResourceByDeptId(deptId);
    }

    @Override
    public List<AdminMenuResourceModel> getAllMenuPageUrl(Integer deptId, AdminUserModel userModel) {
        List<AdminMenuResourceModel> topMenus = null;
        AdminDeptModel deptModel = adminDeptDao.selectById(userModel.getDeptId());

        if (userModel.getId() == 0) {//admin
            topMenus = adminMenuResouceService.findMenuResources(0, 0);
        } else if (deptModel.getDeptLevel() == 0) {//代理admin、总部
            topMenus = adminMenuResouceService
                    .findMenuResourceByCategoryAndDeptId(AdminMenuResourceModel.CATEGORY, null, userModel.getDataId(), 1);
        } else {
            topMenus = adminMenuResouceService
                    .findMenuResourceByCategoryAndDeptId(AdminMenuResourceModel.CATEGORY, userModel.getDeptId(), userModel.getDataId(), 0);
        }

        for (AdminMenuResourceModel menuModel : topMenus) {
            menuModel.setChecked(this.isAssignmen(menuModel.getId(), deptId));
            this.handleChildMenu(menuModel, deptId, userModel);
        }
        return topMenus;
    }

    private void handleChildMenu(AdminMenuResourceModel menuModel, Integer deptId, AdminUserModel userModel) {
        List<AdminMenuResourceModel> childMenus = this.findResourceByParentId(menuModel.getId(), deptId, userModel);
        AdminDeptModel deptModel = adminDeptDao.selectById(userModel.getDeptId());
        for (AdminMenuResourceModel childMenu : childMenus) {
            List<AdminSecurityResourceModel> pageElementResourceModels = null;
            if (userModel.getId() == 0) {//总部、代理admin
                pageElementResourceModels = adminSecurityResourceDao
                        .getAllByMenuId(childMenu.getId(), AdminPageElementResourceModel.CATEGORY, 0);
            } else if (deptModel.getDeptLevel() == 0) {
                pageElementResourceModels = adminSecurityResourceDao
                        .getAllByMenuIdByCategoryAndDeptId(AdminPageElementResourceModel.CATEGORY, null, userModel.getDataId(), 1, childMenu.getId());

            } else {
                pageElementResourceModels = adminSecurityResourceDao
                        .getAllByMenuIdByCategoryAndDeptId(AdminPageElementResourceModel.CATEGORY, userModel.getDeptId(), userModel.getDataId(), 0, childMenu.getId());
            }

            for (AdminSecurityResourceModel resourceModel : pageElementResourceModels) {
                resourceModel.setChecked(this.isAssignmen(resourceModel.getId(), deptId));
            }
            childMenu.setPageElements(pageElementResourceModels);

            childMenu.setChecked(this.isAssignmen(childMenu.getId(), deptId));
            List<AdminMenuResourceModel> sonMenus = this.findResourceByParentId(childMenu.getId(), deptId, userModel);
            // 递归
            if (CollectionUtils.isNotEmpty(sonMenus)) {
                for (AdminMenuResourceModel sonMenu : sonMenus) {
                    this.handleChildMenu(sonMenu, deptId, userModel);
                }
                childMenu.setChildren(sonMenus);
            }
        }

        menuModel.setChildren(childMenus);

    }

    public List<AdminMenuResourceModel> findResourceByParentId(Integer parentId, Integer deptId, AdminUserModel userModel) {
        List<AdminMenuResourceModel> result = null;
        AdminDeptModel deptModel = adminDeptDao.selectById(userModel.getDeptId());

        if (userModel.getId().equals(0)) {
            result = adminSecurityResourceDao.getChildMenu(parentId, 1, AdminMenuResourceModel.CATEGORY);
        } else if (deptModel.getDeptLevel() == 0) {//总部、代理
            result = deptResourceDao
                    .findResourceByParentId(parentId, userModel.getDataId(), null, 1, AdminMenuResourceModel.CATEGORY);
        } else {
            result = deptResourceDao
                    .findResourceByParentId(parentId, userModel.getDataId(), userModel.getDeptId(), 0, AdminMenuResourceModel.CATEGORY);
        }
        for (AdminMenuResourceModel menuResourceModel : result) {
            List<AdminMenuResourceModel> childList = this.findResourceByParentId(menuResourceModel.getId(), deptId, userModel);
            if (CollectionUtils.isNotEmpty(childList)) {
                menuResourceModel.setChildren(this.findResourceByParentId(menuResourceModel.getId(), deptId, userModel));
            }
        }
        return result;
    }

    @Override
    public AdminDeptModel getDeptByParentId(Integer parentId) {
        return adminDeptDao.getDeptByParentId(parentId);
    }

    @Override
    public List<AdminRoleModel> findDeptRoleList(Integer deptId, Integer agencyId) {
        return adminDeptDao.findDeptRoleList(deptId, agencyId);
    }

    @Override
    public List<AdminDeptVO> findDeptListByParentId(Integer parentId, Integer agencyId) {
        List<AdminDeptModel> list = adminDeptDao.findDeptListByParentId(parentId);
        List<AdminDeptVO> result = new ArrayList<>();
        list.forEach(e -> {
            AdminDeptVO vo = new AdminDeptVO();
            BeanUtil.copyNotNullProperties(e, vo);
            if (e.getParentId() != null) {
                AdminDeptModel model = adminDeptDao.selectById(e.getParentId());
                if (model != null) vo.setParentDeptName(model.getName());
            }
            result.add(vo);
        });
        return result;
    }

    private boolean isAssignmen(Integer id, Integer deptId) {
        AdminDeptResourceModel model = deptResourceDao.getInfoByDeptIdAndId(id, deptId, 0, null);
        return model != null;
    }
}
