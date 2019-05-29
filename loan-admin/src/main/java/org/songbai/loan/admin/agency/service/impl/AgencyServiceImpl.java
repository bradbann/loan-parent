package org.songbai.loan.admin.agency.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.admin.dao.*;
import org.songbai.loan.admin.admin.model.*;
import org.songbai.loan.admin.admin.service.AdminMenuResouceService;
import org.songbai.loan.admin.agency.dao.AgencyDao;
import org.songbai.loan.admin.agency.dao.AgencyHostDao;
import org.songbai.loan.admin.agency.po.AgencyPo;
import org.songbai.loan.admin.agency.service.AgencyService;
import org.songbai.loan.admin.channel.dao.ChannelDao;
import org.songbai.loan.admin.version.dao.AdminVestDao;
import org.songbai.loan.common.helper.IpAddressHelper;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.rediskey.AdminRedisKey;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.model.agency.AgencyHostModel;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.user.dao.ComUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class AgencyServiceImpl implements AgencyService {
    private static Logger logger = LoggerFactory.getLogger(AgencyServiceImpl.class);
    @Autowired
    AgencyDao agencyDao;
    @Autowired
    AdminActorDao adminActorDao;
    @Autowired
    SpringProperties properties;
    @Autowired
    IpAddressHelper ipAddressHelper;
    @Autowired
    ComUserDao comUserDao;
    @Autowired
    AdminDeptDao adminDeptDao;
    @Autowired
    AgencyHostDao agencyHostDao;
    @Autowired
    AdminDeptResourceDao adminDeptResourceDao;
    @Autowired
    AdminResourceAssignmentDao adminResourceAssignmentDao;
    @Autowired
    AdminMenuResouceService adminMenuResouceService;
    @Autowired
    AdminSecurityResourceDao adminSecurityResourceDao;
    @Autowired
    ChannelDao channelDao;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    AdminVestDao adminVestDao;

    @Override
    @Transactional
    public void addAgency(AgencyModel agencyModel, String createOwner, Integer ownerId, HttpServletRequest request) {
//        UserModel tmp = comUserDao.findUserInfoByAccount(StringUtil.trimToNull(phone), null);
//        if (tmp != null) {
//            throw new BusinessException(UserRespCode.PHONE_EXISTS);
//        }
//        UserModel user = createUser(phone, teleCode, request);
//
//        agencyModel.setUserId(user.getId());

        checkAgencyUrlRepeat(agencyModel.getAgencyUrl());
        if (agencyModel.getMouldId() == null) {
            agencyModel.setMouldId(properties.getInteger("amdin.agency.risk.mould"));
        }
        if (agencyModel.getOldGuestMouldId() == null) {
            agencyModel.setOldGuestMouldId(properties.getInteger("amdin.agency.risk.mould"));
        }

        String[] hosts = agencyModel.getAgencyUrl().split(",");
        if (hosts.length == 0) {
            throw new BusinessException(AdminRespCode.DOMAIN_ERROR_INPUT);
        }

        //判断域名是否冲突
        Integer hostCount = agencyDao.countDomainNoCurrentUser(hosts);
        if (hostCount != null && hostCount > 0) {
            throw new BusinessException(AdminRespCode.DOMAIN_DUPLICATION);
        }
        agencyDao.insert(agencyModel);

        //代理域名插入
        insertAgencyHost(hosts, agencyModel.getId());

        //创建默认总部部门
        Integer parentDeptId = createAgencyDept(agencyModel.getId(), ownerId);

        // 创建admin管理员
        createAgencyAdminUser(agencyModel, parentDeptId, createOwner, ownerId);

        //创建默认代理权限
        createAgencyAuth(agencyModel.getId());

        //创建默认马甲
        Integer vestId = createAgencyVest(agencyModel.getId());

        //创建代理渠道
        createAgencyChannel(agencyModel.getId(), vestId);

        agencyModel.setDeleted(parentDeptId);
//        agencyModel.setRelationCode(relationCode + agencyModel.getId() + "#");
        String idMd5 = AdminUserModel.handlePassword(String.valueOf(agencyModel.getId()));
        agencyModel.setIdMd5(idMd5.substring(8, 24));
        agencyDao.updateAgency(agencyModel);
    }

    private Integer createAgencyVest(Integer agencyId) {
        AppVestModel vestModel = adminVestDao.selectById(0);
        vestModel.setId(null);
        vestModel.setAgencyId(agencyId);
        vestModel.setCreateTime(null);
        vestModel.setRefuseJumpUrl(null);
        vestModel.setRefuseStatus(null);
        vestModel.setPactId(null);

        adminVestDao.insert(vestModel);
        return vestModel.getId();
    }

    private void createAgencyChannel(Integer agencyId, Integer vestId) {
        AgencyChannelModel channelModel = channelDao.selectById(0);
        channelModel.setId(null);
        channelModel.setAgencyId(agencyId);
        channelModel.setCreateTime(null);
        channelModel.setUpdateTime(null);
        channelModel.setVestId(vestId);

        channelDao.insert(channelModel);
    }

    private void createAgencyAuth(Integer agencyId) {
        AdminAuthorityModel authorityModel = adminActorDao.queryDefaultAgencyAuthority();
//        adminActorDao.createAuthorization(admin.getId(), authorityModel.getId(), admin.getDataId());
        Set<Integer> resourceIds = adminResourceAssignmentDao.findResourceByAuthorityId(authorityModel.getId());
        for (Integer resourceId : resourceIds) {
            AdminDeptResourceModel resourceModel = new AdminDeptResourceModel();
            resourceModel.setAgencyId(agencyId);
            resourceModel.setResourceId(resourceId);
            resourceModel.setType(1);
            adminDeptResourceDao.insert(resourceModel);
        }
    }

    private void createAgencyAdminUser(AgencyModel agencyModel, Integer parentDeptId, String createOwner, Integer ownerId) {
        AdminUserModel admin = new AdminUserModel();
        admin.setDataId(agencyModel.getId());
        admin.setName(agencyModel.getAgencyName() + "管理员");
        admin.setUserPortrait(agencyModel.getAgencyIcon());
        admin.setPhone(agencyModel.getLinkPhone());
        admin.setUserAccount("admin");
//        admin.setUserAccount(agencyModel.getLinkPhone());
        admin.setPassword(admin.createPassWord(agencyModel.getLinkPhone()));
        admin.setRoleType(1);
        admin.setResourceType(1);
        admin.setIsManager(1);
        admin.setDeptId(parentDeptId);
        admin.setCreateOwner(createOwner);
        admin.setCreateOwnerId(ownerId);
        admin.setIsValidate(CommonConst.YES);
        adminActorDao.createAdminUser(admin);
    }

    private Integer createAgencyDept(Integer agencyId, Integer ownerId) {
        AdminDeptModel deptModel = new AdminDeptModel();
        deptModel.setName("总部");
        deptModel.setAgencyId(agencyId);
        deptModel.setDeptLevel(0);
//        deptModel.setMinisterId(admin.getId());
        deptModel.setCreateId(ownerId);
        adminDeptDao.insert(deptModel);
        deptModel.setDeptCode(deptModel.getId() + "#");
        adminDeptDao.updateById(deptModel);

        //创建信审、财务、催收部门
        List<AdminDeptModel> deptList = adminDeptDao.queryDefaultDeptList();
        for (AdminDeptModel dept : deptList) {
            dept.setId(null);
            dept.setParentId(deptModel.getId());
            dept.setAgencyId(agencyId);
//            dept.setMinisterId(null);
            dept.setCreateId(null);
            dept.setCreateTime(null);
            adminDeptDao.insert(dept);
            dept.setDeptCode(deptModel.getDeptCode() + dept.getId() + "#");
            adminDeptDao.updateById(dept);
        }
        return deptModel.getId();
    }

    private void checkAgencyUrlRepeat(String agencyUrl) {
        String[] list = agencyUrl.split(",");
        Set<String> result = new HashSet<>();
        for (String str : list) {
            if (StringUtils.isEmpty(str)) continue;
            if (result.contains(str)) {
                throw new BusinessException(AdminRespCode.DOMAIN_DUPLICATION);
            } else {
                result.add(str);
            }
        }
    }

    @Override
    @Transactional
    public void updateAgency(Integer superId, AgencyModel agencyModel) {
        AgencyModel oldAgencyModel = agencyDao.findById(agencyModel.getId());
        if (superId != 0) {
            if (oldAgencyModel == null || !superId.equals(oldAgencyModel.getId())) {
                throw new BusinessException(AdminRespCode.HAS_NOT_AGENCY);
            }
        }
        AgencyModel update = new AgencyModel();
        update.setId(oldAgencyModel.getId());
        update.setAlipayStatus(agencyModel.getAlipayStatus());
        update.setAlipayUrl(agencyModel.getAlipayUrl());
        update.setWepayStatus(agencyModel.getWepayStatus());
        update.setWepayUrl(agencyModel.getWepayUrl());
        update.setH5Status(agencyModel.getH5Status());
        update.setH5Url(agencyModel.getH5Url());
        update.setAutoPay(agencyModel.getAutoPay());
        update.setAgencyName(agencyModel.getAgencyName());
        update.setAgencyIcon(agencyModel.getAgencyIcon());
        update.setLinkMan(agencyModel.getLinkMan());
        update.setLinkPhone(agencyModel.getLinkPhone());
        update.setBadDebt(agencyModel.getBadDebt());
        update.setJhpayKey(agencyModel.getJhpayKey());
        update.setJhpayMerid(agencyModel.getJhpayMerid());
        update.setMouldId(agencyModel.getMouldId());
        update.setOldGuestMouldId(agencyModel.getOldGuestMouldId());

        agencyDao.updateById(update);

        if (StringUtil.isNotEmpty(agencyModel.getAgencyUrl())) {
            agencyHostDao.deleteByAgencyId(agencyModel.getId());
            checkAgencyUrlRepeat(agencyModel.getAgencyUrl());
            String[] hosts = agencyModel.getAgencyUrl().split(",");
            if (hosts.length == 0) {
                throw new BusinessException(AdminRespCode.DOMAIN_ERROR_INPUT);
            }
            // 判断域名是否冲突
            Integer hostCount = agencyDao.countDomainNoCurrentUser(hosts);
            if (hostCount != null && hostCount > 0) {
                throw new BusinessException(AdminRespCode.DOMAIN_DUPLICATION);
            }
            // 代理域名插入
            insertAgencyHost(hosts, agencyModel.getId());
        }

        if (!oldAgencyModel.getAgencyName().equals(agencyModel.getAgencyName())) {
            AdminUserModel admin = new AdminUserModel();
            admin.setDataId(oldAgencyModel.getId());
            admin.setName(agencyModel.getAgencyName() + "管理员");
            adminActorDao.updateAdminUserName(admin);
        }
        redisTemplate.opsForHash().getOperations().delete(AdminRedisKey.AGENCY_INFO + superId);
    }

    private void insertAgencyHost(String[] hosts, Integer agencyId) {
        for (String s : hosts) {
            if (StringUtils.isNotEmpty(s)) {
                AgencyHostModel model = new AgencyHostModel();
                model.setAgencyId(agencyId);
                model.setHost(s);
                agencyHostDao.insert(model);
            }
        }
    }

    @Override
    public AgencyModel findById(Integer id) {
        return agencyDao.findById(id);
    }


    @Override
    public void resetPassword(Integer id, Integer superId) {
        AgencyModel agencyModel = agencyDao.findById(id);
        if (agencyModel == null || !superId.equals(agencyModel.getSuperId())) {
            throw new BusinessException(AdminRespCode.HAS_NOT_AGENCY);
        }
        AdminUserModel admin = new AdminUserModel();
        admin.setDataId(agencyModel.getId());
        admin.setPassword(admin.getDefaultPassword());
        adminActorDao.resetAgencyPassword(admin);
    }

    @Override
    @Transactional
    public void disabledAgencyAccount(List<Integer> ids, Integer disable, Integer superId) {
        EntityWrapper<AgencyModel> ew = new EntityWrapper<>();
        AgencyModel update = new AgencyModel();
        if (superId.equals(0)) {
            agencyDao.disabledAgencyAccount(ids, disable, null);
            ew.in("id", ids);
            if (CommonConst.STATUS_VALID == disable) {
                update.setStatus(CommonConst.STATUS_INVALID);

            } else {
                update.setStatus(CommonConst.STATUS_VALID);
            }
            agencyDao.update(update, ew);
        } else {
            List<Integer> agencyIds = new ArrayList<>();
            agencyDao.disabledAgencyAccount(ids, disable, superId);
            for (Integer id : ids) {
                AgencyModel model = agencyDao.findById(id);
                if (model != null && !superId.equals(model.getSuperId())) {
                    agencyIds.add(id);
                }
            }
            if (!CollectionUtils.isEmpty(agencyIds)) {
                ew.in("id", agencyIds);

                if (CommonConst.STATUS_VALID == disable) {
                    update.setStatus(CommonConst.STATUS_INVALID);

                } else {
                    update.setStatus(CommonConst.STATUS_VALID);
                }
                agencyDao.update(update, ew);
            }
        }
    }

    @Override
    public void updateShareStatus(String ids, Integer shareStatus) {
        String[] idArr = ids.split(",");
        agencyDao.updateShareStatus(idArr, shareStatus);
    }


    @Override
    public Page<AgencyModel> list(AgencyPo po, Integer superAgencyId, Integer page, Integer pageSize) {

//        agencyRelationCode = StringUtil.trimToNull(agencyRelationCode);
        if (superAgencyId != 0) {
            po.setAgencyId(superAgencyId);
        }
        Integer count = agencyDao.agencyListCount(po);
        if (count <= 0) return new Page<>(page, pageSize, 0, new ArrayList<>());
        Integer offset = page * pageSize;
        List<AgencyModel> list = agencyDao.agencyList(po, offset, pageSize);
//        urlWrapper(list);
        return new Page<>(page, pageSize, count, list);
    }


    @Override
    public void disabledSubAgency(Integer id, Integer status, Integer agencyId) {
        AgencyModel oldAgencyModel = agencyDao.findById(id);
        if (oldAgencyModel == null || agencyId.equals(oldAgencyModel.getSuperId())) {
            throw new BusinessException(AdminRespCode.HAS_NOT_AGENCY);
        }
        agencyDao.disabledSubAgencyAccount(id, status);

        AgencyModel update = new AgencyModel();
        update.setId(id);
        update.setStatus(status);
        agencyDao.updateById(update);
    }


    @Override
    public List<AgencyModel> listAll(AdminUserModel userModel) {
        Integer agencyId = null;
        if (userModel.getDataId() != 0) {
            agencyId = userModel.getDataId();
        }
        List<AgencyModel> agencyModels = agencyDao.selectAllByAgencyId(agencyId);
        return agencyModels;
    }

    @Override
    public AgencyModel findAgencyById(Integer id, AdminUserModel userModel) {
        if (userModel.getDataId() != 0) {
            id = userModel.getDataId();
        }
        return agencyDao.findById(id);
    }

    @Override
    public void deleteResourceByAgencyId(Integer agencyId) {
        agencyDao.deleteResourceByAgencyId(agencyId);
    }

    @Override
    @Transactional
    public void saveResourceToAgencyId(Integer agencyId, List<Integer> resourceIds) {
        if (resourceIds.size() > 0) {
            List<AdminDeptResourceModel> list = resourceIds.stream().map(resourceId -> {
                AdminDeptResourceModel temp = new AdminDeptResourceModel();
                temp.setResourceId(resourceId);
                temp.setAgencyId(agencyId);
                temp.setType(1);
                return temp;
            }).collect(Collectors.toList());


            agencyDao.deleteResourceByAgencyId(agencyId);

            agencyDao.createAdminDeptResource(list);
        }
    }

    @Override
    public List<AdminMenuResourceModel> getAllMenuPageUrl(Integer agencyId) {
        List<AdminMenuResourceModel> topMenus = adminMenuResouceService.findMenuResources(0, 0);

        for (AdminMenuResourceModel menuModel : topMenus) {
            menuModel.setChecked(this.isAssignmen(menuModel.getId(), agencyId));
            this.handleChildMenu(menuModel, agencyId);
        }
        return topMenus;
    }

    @Override
    public AgencyModel findAgencyByAgencyId(Integer agencyId) {
        return agencyDao.selectById(agencyId);
    }

    private void handleChildMenu(AdminMenuResourceModel menuModel, Integer agencyId) {
        List<AdminMenuResourceModel> childMenus = this.findResourceByParentId(menuModel.getId());
        for (AdminMenuResourceModel childMenu : childMenus) {
            List<AdminSecurityResourceModel> pageElementResourceModels = null;

            pageElementResourceModels = adminSecurityResourceDao
                    .getAllByMenuId(childMenu.getId(), AdminPageElementResourceModel.CATEGORY, 0);

            for (AdminSecurityResourceModel resourceModel : pageElementResourceModels) {
                resourceModel.setChecked(this.isAssignmen(resourceModel.getId(), agencyId));
            }
            childMenu.setPageElements(pageElementResourceModels);

            childMenu.setChecked(this.isAssignmen(childMenu.getId(), agencyId));
            List<AdminMenuResourceModel> sonMenus = this.findResourceByParentId(childMenu.getId());
            // 递归
            if (sonMenus != null && sonMenus.size() > 0) {
                for (AdminMenuResourceModel sonMenu : sonMenus) {
                    this.handleChildMenu(sonMenu, agencyId);
                }
                childMenu.setChildren(sonMenus);
            }
        }

        menuModel.setChildren(childMenus);

    }

    private List<AdminMenuResourceModel> findResourceByParentId(Integer parentId) {
        List<AdminMenuResourceModel> result = adminSecurityResourceDao.getChildMenu(parentId, 1, AdminMenuResourceModel.CATEGORY);

        for (AdminMenuResourceModel menuResourceModel : result) {
            if (this.findResourceByParentId(menuResourceModel.getId()) != null
                    && this.findResourceByParentId(menuResourceModel.getId()).size() > 0) {
                menuResourceModel.setChildren(this.findResourceByParentId(menuResourceModel.getId()));
            }
        }
        return result;
    }

    private boolean isAssignmen(Integer id, Integer agencyId) {
        AdminDeptResourceModel model = adminDeptResourceDao.getInfoByDeptIdAndId(id, null, 1, agencyId);
        return model != null;
    }


}
