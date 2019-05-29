package org.songbai.loan.admin.user.controller;

import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.helper.upload.AliyunOssHelper;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.admin.support.AgencySecurityHelper;
import org.songbai.loan.admin.user.dao.UserBankCardDao;
import org.songbai.loan.admin.user.model.UserQueryVo;
import org.songbai.loan.admin.user.service.UserReportService;
import org.songbai.loan.admin.user.service.UserService;
import org.songbai.loan.config.Accessible;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 后台用户列表
 *
 * @author wjl
 * @date 2018年10月30日 10:38:53
 * @description
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AdminUserHelper adminUserHelper;
    @Autowired
    private AgencySecurityHelper agencySecurityHelper;
    @Autowired
    private AliyunOssHelper help;
    @Autowired
    private UserReportService userReportService;
    @Autowired
    private UserBankCardDao bankCardDao;

    @GetMapping("/list")
    public Response userList(UserQueryVo model, HttpServletRequest request) {

        model.setPage(model.getPage() == null ? 0 : model.getPage());
        model.setPageSize(model.getPageSize() == null ? 20 : model.getPageSize());
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            model.setAgencyId(agencyId);
        }
        return Response.success(userService.userList(model));
    }

    @GetMapping("/detail")
    public Response userDetail(String userId, HttpServletRequest request) {
        Assert.hasText(userId, "id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        return Response.success(userService.userDetail(userId, agencyId));
    }

    @Accessible(onlyAgency = true)
    @PostMapping("/deAuth")
    public Response deAuth(String userId, HttpServletRequest request) {
        Assert.hasText(userId, "id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        //更改auth表
        userService.deAuth(userId, agencyId);
        return Response.success();
    }

    @GetMapping("/img")
    public void img(HttpServletResponse response, String url) {
        try {
            help.innerGetFileStream(response, url);
        } catch (Exception ex) {
            throw new BusinessException(UserRespCode.SERVER_ERROR, null, ex);
        }
    }

    @GetMapping("/getUserReport")
    public Response getUserReport(String userId, HttpServletRequest request) {
        Assert.hasText(userId, "id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);


        if (agencySecurityHelper.checkIsPingtai(request)) {
            return Response.success(userReportService.getReport(userId, null));
        } else {
            return Response.success(userReportService.getReport(userId, agencyId));
        }
    }


    @GetMapping("/getUserContact")
    public Response getUserContact(String userId, HttpServletRequest request) {
        Assert.hasText(userId, "id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId == 0) agencyId = null;

        return Response.success(userReportService.getUserContact(userId, agencyId));

    }

    @GetMapping("getCarrierReport")
    public Response getCarrierReport(String userId, HttpServletRequest request) {
        Assert.hasText(userId, "id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId == 0) agencyId = null;


        return Response.success(userReportService.getCarrierReport(userId, agencyId));
    }

    @GetMapping("getTaobaoReport")
    public Response getTaobaoReport(String userId, HttpServletRequest request) {
        Assert.hasText(userId, "id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId == 0) agencyId = null;


        return Response.success(userReportService.getTaobaoReport(userId, agencyId));
    }

    @GetMapping("getTaobaoAddr")
    public Response getTaobaoAddr(String userId, HttpServletRequest request) {
        Assert.hasText(userId, "id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId == 0) agencyId = null;

        return Response.success(userReportService.getTaobaoAddr(userId, agencyId));
    }

    @GetMapping("getTaobaoTrade")
    public Response getTaobaoTrader(String userId, HttpServletRequest request) {
        Assert.hasText(userId, "id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId == 0) agencyId = null;

        return Response.success(userReportService.getTaobaoTrade(userId, agencyId));
    }


    /**
     * 更新用户身份证信息
     */
    @Accessible(onlyAgency = true)
    @PostMapping("/updateUserIdCardInfo")
    public Response updateUserIdCardInfo(UserInfoModel userInfo, String thirdId, HttpServletRequest request) {
        Assert.hasText(thirdId, "用户id不能为空");
        Assert.notNull(userInfo, "参数不能为空");
        Assert.hasText(userInfo.getName(), "姓名不能为空");
        Assert.hasText(userInfo.getIdcardNum(), "身份证号码不能为空");
        Assert.hasText(userInfo.getIdcardAddress(), "身份证地址不能为空");
        Assert.hasText(userInfo.getValidation(), "身份证有效期不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        userService.updateUserIdCardInfo(userInfo, thirdId, agencyId);
        return Response.success();
    }

    /**
     * 开户行纠正
     */
    @Accessible(onlyAgency = true)
    @PostMapping("/updateUserBankCard")
    public Response updateUserBankCard(Integer id, String bankCode, String bankName) {
        UserBankCardModel userBankCardModel = bankCardDao.selectById(id);
        if (userBankCardModel == null) {
            return Response.success();
        }
        UserBankCardModel update = new UserBankCardModel();
        update.setId(id);
        update.setBankCode(bankCode);
        update.setBankName(bankName);
        String icon = bankCardDao.getIconByBankCode(bankCode);
        update.setIcon(icon);
        bankCardDao.updateById(update);
        return Response.success();
    }

    /**
     * 用户注销
     */
    @Accessible(onlyAgency = true)
    @PostMapping("/logOffUser")
    public Response logOffUser(String userId, HttpServletRequest request) {
        Assert.hasLength(userId, LocaleKit.get("common.param.notnull", "userId"));
        Integer agencyId = adminUserHelper.getAgencyId(request);
        userService.logOffUser(userId, agencyId);
        return Response.success();
    }

    /**
     * 用户借贷记录
     */
    @GetMapping("findUserOrderHistList")
    public Response findUserOrderHistList(String userId, HttpServletRequest request) {
        Assert.hasText(userId, "userId不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 18;
        if (agencyId == 0) agencyId = null;

        return Response.success(userService.findUserOrderHistList(userId, agencyId));
    }

}
