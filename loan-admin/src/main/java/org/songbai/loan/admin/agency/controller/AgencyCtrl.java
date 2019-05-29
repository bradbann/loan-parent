package org.songbai.loan.admin.agency.controller;

import org.apache.commons.lang.StringUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.admin.support.AgencySecurityHelper;
import org.songbai.loan.admin.agency.helper.AgencyAssertCtrlHelper;
import org.songbai.loan.admin.agency.po.AgencyPo;
import org.songbai.loan.admin.agency.service.AgencyService;
import org.songbai.loan.config.Accessible;
import org.songbai.loan.constant.rediskey.AdminRedisKey;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/agency")
public class AgencyCtrl {

    @Autowired
    private AgencyService agencyService;
    @Autowired
    private AgencyAssertCtrlHelper agencyAssertCtrlHelper;
    @Autowired
    AgencySecurityHelper securityHelper;
    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    RedisTemplate<Object, String> redisTemplate;
    @Autowired
    ComAgencyService comAgencyService;


    @PostMapping("/add")
    @Accessible(platform = true)
    public Response addAgency(AgencyModel agencyModel, HttpServletRequest request) {
        agencyAssertCtrlHelper.checkAgencyAdd(agencyModel);

//        AdminUserModel userModel = new AdminUserModel();
//        userModel.setDataId(0);
//        userModel.setId(0);
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        Integer agencyId = userModel.getDataId();
        if (agencyId != 0) {
            throw new BusinessException(AdminRespCode.ACCESS_ADMIN);
        }
        String createOwner = userModel.getName();
        Integer ownerId = userModel.getId();
        // 根据登录用户 获取代理的信息
        AgencyModel superAgency = adminUserHelper.getAgency(request);
        agencyModel.setSuperId(agencyId);
        agencyModel.setAgencyLevel(1);
        agencyService.addAgency(agencyModel, createOwner, ownerId, request);
        return Response.success();
    }


    @PostMapping("/updateAgency")
    public Response update(AgencyModel update, HttpServletRequest request) {
        Assert.notNull(update.getId(), LocaleKit.get("common.param.notnull", "id"));
        //Assert.notNull(update.getAgencyUrl(), LocaleKit.get("common.param.notnull", "agencyUrl"));

        update.setAgencyCode(null);//账号不能设置
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        agencyService.updateAgency(userModel.getDataId(), update);
        redisTemplate.opsForHash().getOperations().delete(AdminRedisKey.AGENCY_INFO);
        return Response.success();

    }


    //代理商密码重置
    @PostMapping("/resetPassword")
    @Accessible(platform = true)
    public Response resetPassword(Integer id, HttpServletRequest request) {
        Assert.notNull(id, LocaleKit.get("common.param.notnull", "id"));
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        Integer agencyId = userModel.getDataId();
        agencyService.resetPassword(id, agencyId);
        redisTemplate.opsForHash().getOperations().delete(AdminRedisKey.AGENCY_INFO);

        return Response.success();
    }


    @GetMapping("/pageAgency")
    public Response pageAgency(AgencyPo po, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "20") Integer pageSize, HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
//        AdminUserModel userModel = new AdminUserModel();
//        userModel.setDataId(0);
//        userModel.setId(0);

        Page<AgencyModel> model = agencyService.list(po, userModel.getDataId(), page, pageSize);
        return Response.success(model);
    }

    @GetMapping("/list")
    public Response list(HttpServletRequest request) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        List<AgencyModel> model = agencyService.listAll(userModel);
        return Response.success(model);
    }

    @GetMapping("/findAgencyById")
    public Response findAgencyById(Integer id, HttpServletRequest request) {
        Assert.notNull(id, "id不能为空");
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);

        return Response.success(agencyService.findAgencyById(id, userModel));
    }

    @GetMapping("/findAgency")
    public Response findAgency(HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        return Response.success(agencyService.findAgencyByAgencyId(agencyId));
    }


    /**
     * 禁用代理账号
     */
    @PostMapping("/disabledAgencyAccount")
    @Accessible(platform = true)
    public Response disabledAgencyAccount(@RequestParam("ids") List<Integer> ids, Integer status, HttpServletRequest request) {
        if (CollectionUtils.isEmpty(ids)) {
            Assert.notNull(ids, LocaleKit.get("common.param.notnull", "ids"));
        }
        Assert.notNull(status, LocaleKit.get("common.param.notnull", "status"));

        Integer agencyId = adminUserHelper.getAgencyId(request);
        agencyService.disabledAgencyAccount(ids, status, agencyId);
        redisTemplate.opsForHash().getOperations().delete(AdminRedisKey.AGENCY_INFO);
        return Response.success();
    }

    /**
     * 禁用二级代理账号
     */
    @PostMapping("/disabledSubAgency")
    @Accessible(platform = true)
    public Response disabledSubAgency(Integer id, Integer status, HttpServletRequest request) {
        Assert.notNull(id, LocaleKit.get("common.param.notnull", "id"));
        Assert.notNull(status, LocaleKit.get("common.param.notnull", "status"));

        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        agencyService.disabledSubAgency(id, status, userModel.getDataId());
        redisTemplate.opsForHash().getOperations().delete(AdminRedisKey.AGENCY_INFO);
        return Response.success();
    }

    /**
     * 修改代理商分享功能
     */
    @PostMapping("/updateShareStatus")
    @ResponseBody
    @Accessible(platform = true)
    public Response updateShareStatus(String ids, Integer shareStatus, HttpServletRequest request) {
        if (StringUtils.isBlank(ids) || shareStatus == null || (shareStatus != 0 && shareStatus != 1)) {
            return Response.response(701, "参数错误");
        }
        AdminUserModel admin = adminUserHelper.getAdminUser(request);
        if (admin.getDataId() != 0) {
            return Response.response(701, "只有平台账号下才能操作");
        }
        agencyService.updateShareStatus(ids, shareStatus);
        return Response.success();
    }

    /**
     * 代理授权
     */
    @PostMapping(value = "/grantResourcesToAgency")
    @Accessible(platform = true)
    public Response grantResourcesToAgency(Integer agencyId, String securityResourceIds) {
        Assert.notNull(agencyId, "代理不能为空！");
        if (securityResourceIds.equals("")) {
            agencyService.deleteResourceByAgencyId(agencyId);
        } else {

            Integer[] resourceIds = StringUtil.split2Int(securityResourceIds);

            agencyService.saveResourceToAgencyId(agencyId, Arrays.asList(resourceIds));
        }
        return Response.success();
    }

    @GetMapping(value = "/getAllMenuPageUrl")
    @Accessible(platform = true)
    public Response getAllMenuPageUrl(Integer agencyId) {
        Assert.notNull(agencyId, "代理id不能为空！");
        return Response.success(agencyService.getAllMenuPageUrl(agencyId));
    }

    @RequestMapping(value = "/safe_getAgencyName")
    @ResponseBody
    public Response getAgencyName(HttpServletRequest request) {
        AgencyModel agencyModel = comAgencyService.getAgencyInfoByHotst(request);

        if (agencyModel != null) {
            return Response.success(agencyModel.getAgencyName());
        }

        return Response.success();
    }

}
