package org.songbai.loan.admin.admin.service.impl;

import org.apache.commons.lang.StringUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.loan.admin.admin.dao.AdminActorDao;
import org.songbai.loan.admin.admin.dao.AdminAuthorizationDao;
import org.songbai.loan.admin.admin.model.AdminActorModel;
import org.songbai.loan.admin.admin.model.AdminAuthorizationModel;
import org.songbai.loan.admin.admin.model.AdminDeptModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminDeptService;
import org.songbai.loan.admin.admin.service.AdminUserService;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class AdminUserServiceimpl implements AdminUserService {
    @Autowired
    AdminActorDao adminActorDao;
    @Autowired
    AdminAuthorizationDao adminAuthorizationDao;
    @Autowired
    RedisTemplate<String, String> redisTemplate;
    @Autowired
    AdminDeptService adminDeptService;
    @Autowired
    AdminUserHelper adminUserHelper;

    @Override
    public void createAdminUser(AdminUserModel adminUserModel, Integer departmentId) {
        /**
         * 页面在提交保存之前已经对User的登录名做了唯一验证
         */
//        adminUserModel.setPassword(adminUserModel.getDefaultPassword());
        adminUserModel.setPassword(adminUserModel.createPassWord(adminUserModel.getPhone()));
        if (departmentId != null) {
            adminActorDao.addDepartmentActor(departmentId, adminUserModel.getId());
        }
        adminActorDao.createAdminUser(adminUserModel);
    }

    @Override
    public void grantAuthoritysToActor(Integer actorId, List<Integer> authorityIdList, Integer dataId) {

        for (Integer authorityId : authorityIdList) {
            AdminAuthorizationModel authorizationMoudel = new AdminAuthorizationModel();
            authorizationMoudel.setActorId(actorId);
            authorizationMoudel.setAuthorityId(authorityId);
            authorizationMoudel.setDataId(dataId);
            adminAuthorizationDao.createAuthorization(authorizationMoudel);
        }
    }

    @Override
    public Page<AdminUserModel> pagingquerByDepartmentId(Integer departmentId, Integer pageIndex, Integer pageSize) {
        Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0;
        List<AdminUserModel> userList = adminActorDao.pagingqueryByDepartmentId(departmentId, limit, pageSize);
        Integer totalCount = adminActorDao.pagingqueryByDepartmentId_count(departmentId);

        Page<AdminUserModel> result = new Page<>(pageIndex, pageSize, totalCount);
        result.setData(userList);

        return result;
    }

    @Override
    public void deleteUserRelation(Integer departmentId, String userIds) {
        String[] userIdArray = userIds.split(",");

        List<Integer> userIdList = new ArrayList<Integer>();
        for (int i = 0; i < userIdArray.length; i++) {
            userIdList.add(Integer.valueOf(userIdArray[i]));
        }
        if (userIdList.size() > 0) {
            adminActorDao.deleteUserRelation(departmentId, userIdList);
        }
    }

    @Override
    public Page<AdminUserModel> pagingqueryNotRelationUsers(Integer pageIndex, Integer pageSize, Integer dataId) {
        Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0;

        List<AdminUserModel> userModels = adminActorDao.pagingqueryNotRelationUsers(limit, pageSize, dataId);
        Integer totalCount = adminActorDao.pagingqueryNotRelationUsers_count(dataId);

        Page<AdminUserModel> page = new Page<AdminUserModel>(pageIndex, pageSize, totalCount);
        page.setData(userModels);

        return page;
    }

    @Override
    public void createUserRelation(Integer departmentId, String userIds) {
        String[] userIdArray = userIds.split(",");

        for (int i = 0; i < userIdArray.length; i++) {
            adminActorDao.addDepartmentActor(departmentId, Integer.valueOf(userIdArray[i]));
        }
    }

    @Override
    public void updateAdminUserExceptPassword(AdminUserModel adminUserModel) {
        if (adminUserModel.getIsManager().equals(1)) {
            AdminUserModel model = adminActorDao.getDeptManagerByDeptId(adminUserModel.getDeptId());
            if (model != null && !model.getId().equals(adminUserModel.getId())) {
                throw new BusinessException(AdminRespCode.DEPT_HAVA_MANAGER);
            }
        }
        adminActorDao.updateAdminUserExceptPassword(adminUserModel);

    }

    @Override
    public void deleteAdminUser(Integer id, Integer dataId) {
        adminActorDao.deletAdminUser(id, dataId);

    }

    @Override
    public void deleteAdminUsers(List<Integer> ids, Integer dataId) {

        for (Integer id : ids) {
            // 删除用户 删除与用户关联的角色信息
            adminAuthorizationDao.deleteAuthorizationByActorIdAuthorityIds(id, null, null);
            // 删除用户
            adminActorDao.deletAdminUser(id, dataId);
        }
    }

    @Override
    public AdminUserModel getUser(Integer id) {
        return adminActorDao.getAdminUser(id);
    }

    @Override
    public Page<AdminActorModel> getUserList(Integer pageIndex, Integer size, String userAccount, String name,
                                             String email, Boolean disable, String phone, AdminUserModel userModel) {
        Integer limit = pageIndex > 0 ? pageIndex * size : 0;

        List<Integer> deptIds = adminDeptService.findDeptIdsByType(userModel, null);
        Integer totalCount = adminActorDao.getUserBy_count(userAccount, name, email, disable, phone,
                AdminUserModel.CATEGORY, userModel.getDataId(), deptIds);
        List<AdminActorModel> userModels = new ArrayList<>();
        // 渠道自建用户
        if (totalCount > 0) {
            userModels = adminActorDao.getUsersBy(limit, size, userAccount, name, email, disable,
                    phone, AdminUserModel.CATEGORY, userModel.getDataId(), deptIds);
        }

        Page<AdminActorModel> pageResult = new Page<AdminActorModel>(pageIndex, size, totalCount);
        pageResult.setData(userModels);
        return pageResult;
    }

    /**
     * 重置密码后限制时间和计数重置为0
     */
    @Override
    public void changePassword(Integer id, String newPassword) {
        // AdminUserModel userModel = adminActorDao.getAdminUser(id);
        //
        // if (userModel != null && userModel.getPassword().equals(new
        // AdminUserModel().handlePassword(oldPassword))) {
        // adminActorDao.updateAdminUserPassword(id, new
        // AdminUserModel().handlePassword(newPassword));
        // }
        adminActorDao.updateAdminUserPassword(id, newPassword);

    }

    @Override
    public void disbaleOrActivationUser(List<Integer> ids, boolean disbale) {
        adminActorDao.disbaleOrActivationUser(ids, disbale);

    }

    @Override
    public AdminUserModel getUserByUserAccountPassword(String userAccount, String password, Integer dataId,
                                                       String agencyCode) {
        if (password != null) {
            password = new AdminUserModel().handlePassword(password);
        }
        return adminActorDao.getUserByUserAccountPassword(userAccount, password, dataId, agencyCode);
    }

    @Override
    public boolean hasUserAccount(String userAccount, Integer dataId) {

        return adminActorDao.getUserBy_count(userAccount, null, null, null, null, null, dataId, null) > 0;

    }

    @Override
    public boolean hasUserPhone(String phone, Integer dataId) {

        return adminActorDao.getUserBy_count(null, null, null, null, phone, null, dataId, null) > 0;

    }

    @Override
    public AdminUserModel getAdminUserByAgencyId(Integer agencyId) {
        return adminActorDao.getAdminUserByAgencyId(agencyId);
    }

    @Override
    public void addDepartmentActor(Integer departmentId, Integer actorId) {
        adminActorDao.addDepartmentActor(departmentId, actorId);

    }

    @Override
    public Page<AdminUserModel> pagingqueryByDepartmentId(Integer departmentId, Integer pageIndex, Integer pageSize) {
        Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0;
        List<AdminUserModel> userList = adminActorDao.pagingqueryByDepartmentId(departmentId, limit, pageSize);
        Integer totalCount = adminActorDao.pagingqueryByDepartmentId_count(departmentId);
        Page<AdminUserModel> result = new Page<AdminUserModel>(pageIndex, pageSize, totalCount);
        result.setData(userList);
        return result;
    }

    @Override
    public Page<AdminUserModel> pageQueryActorByRoleId(Integer roleId, Integer dataId, Integer pageIndex,
                                                       Integer pageSize) {
        Integer limit = pageIndex > 0 ? pageIndex * pageSize : 0;
        List<AdminUserModel> userList = adminActorDao.pageQueryActorByRoleId(roleId, dataId, limit, pageSize);
        Integer totalCount = adminActorDao.pageQueryActorByRoleId_count(roleId, dataId);
        Page<AdminUserModel> result = new Page<AdminUserModel>(pageIndex, pageSize, totalCount);
        result.setData(userList);
        return result;
    }

    @Override
    public void deleteUserInRole(Integer roleId, Integer actorId, Integer dataId) {
        adminActorDao.deleteUserInRole(roleId, actorId, dataId);
    }

    @Override
    public Integer getChannelLevelByDomain(String str) {

        return adminActorDao.getChannelLevelByDomain(str);
    }

    @Override
    public void userClosePopsRemind(Integer id) {
        adminActorDao.userClosePopsRemind(id);
    }

    @Override
    public List<AdminUserModel> findUserDeptList(AdminUserModel userModel, Integer deptType) {

//        List<AdminDeptModel> list = adminDeptService.findDeptListByType(userModel, deptType);
        List<Integer> deptIds = adminDeptService.findDeptIdsByType(userModel, deptType);// list.stream().map(AdminDeptModel::getId).collect(Collectors.toList());

        return this.findUserListByDeptIds(deptIds, userModel.getDataId());
    }

    @Override
    public Integer getDeptManagerCountByDeptId(Integer deptId) {
        return adminActorDao.getDeptManagerCountByDeptId(deptId);
    }

    @Override
    public List<AdminUserModel> findUserListByDeptIds(List<Integer> deptIds, Integer dataId) {
        List<AdminUserModel> list = adminActorDao.findUserListByDeptIds(deptIds, dataId);
        list.forEach(e -> {
            e.setPassword(null);
            e.setPassEncryptTimes(null);
        });
        return list;
    }

    @Override
    public Ret getUserMession(HttpServletRequest request) {
        Ret ret = Ret.create();
        AdminUserModel user = adminUserHelper.getAdminUser(request);
        ret.put("name", user.getName());
        ret.put("userAccount", user.getUserAccount());
        ret.put("id", user.getDataId());
        ret.put("level", user.getResourceType());
        ret.put("lastLoginTime", user.getLastLoginTime());
        ret.put("userPortrait", user.getUserPortrait());
        ret.put("roleType", user.getRoleType());
        ret.put("deptId", user.getDeptId());
        ret.put("dataId", user.getDataId());
        if (user.getDeptId() != null) {
            String deptName = null;
            AdminDeptModel deptModel = adminDeptService.findDeptById(user.getDeptId());
            if (deptModel != null) deptName = deptModel.getName();

            if (deptModel != null && deptModel.getDeptLevel().equals(2) && deptModel.getParentId() != null) {
                AdminDeptModel parentDept = adminDeptService.findDeptById(deptModel.getParentId());
                if (parentDept != null && StringUtils.isNotBlank(parentDept.getName())) {
                    deptName = parentDept.getName() + "-" + deptName;
                }
            }
            ret.put("deptName", deptName);
        }

        return ret;
    }

    //----------------------------发给管理员的消息提醒-------------------------------------------
    private static String MESSAGE_REMIND_REDIS_KEY = "message:remind";

    private static String RENGONG_KEY = "rengong";

    private static String TRANFER_KEY = "tranfer";

    private static String REALNAME_KEY = "realname";

    private static String FEEDBACK_KEY = "feedback";
}
