package org.songbai.loan.admin.sms.controller;

import org.songbai.cloud.basics.mvc.RespCode;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.sms.service.SmsSenderService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.sms.SmsConstant;
import org.songbai.loan.model.sms.SmsSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 短信渠道
 */
@Controller
@RequestMapping(value = "/smsSender")
public class SmsSenderController {

    @Autowired
    SmsSenderService smsSenderService;
    @Autowired
    private AdminUserHelper adminUserHelper;

    /**
     * 获取渠道列表
     *
     * @return
     */
    @RequestMapping(value = "/getSenders")
    @ResponseBody
    public Response getSenders(Integer agencyId, HttpServletRequest request) {
        Integer currentAgencyId = adminUserHelper.getAgencyId(request);
        if (currentAgencyId != 0) {
            //Assert.notNull(agencyId, "添加agencyId不能为空");
            agencyId = currentAgencyId;
        }

        return Response.success(smsSenderService.getSenders(agencyId));

    }

    /**
     * 获取渠道列表
     *
     * @return
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public Response detail(Integer id, HttpServletRequest request) {
        Assert.notNull(id, "添加id不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        return Response.success(smsSenderService.getSenderDetail(id, agencyId));

    }

    @RequestMapping(value = "/addSender")
    @ResponseBody
    public Response addSenderMessage(SmsSender smsSender, HttpServletRequest request) {
        Assert.notNull(smsSender.getType(), "添加类型不能为空");
        Assert.hasLength(smsSender.getAccount(), "添加账户不能为空");
        Assert.hasLength(smsSender.getPassword(), "添加密码不能为空");
        Assert.hasLength(smsSender.getName(), "短信服务商不能为空");
        Assert.notNull(smsSender.getStatus(), "状态不能为空");
        Integer agencyId = adminUserHelper.getAgencyId(request);
        // 验证该渠道是否已存在
        if (smsSenderService.validateSenderExist(agencyId, smsSender.getType())) {
            return Response.response(RespCode.SERVER_ERROR, "已存在相同的短信服务商");
        }
        //if (smsSender.getCategory() != CommonConst.YES) {
        //    SmsSender s = smsSenderService.findSenderHome(agencyId);
        //    if (s == null) {
        //        throw new IllegalArgumentException("主账户未添加或已停用,不能添加子账户");
        //    }
        //    smsSender.setSuperId(s.getId());
        //}
        if (SmsConstant.SenderType.SMS_SENDER_TYPE_PAOPAO.key == smsSender.getType()) {
            Assert.hasLength(smsSender.getData(), "参数配置项不能为空");
        }

        if (smsSender.getAgencyId() != null) {
            if (agencyId != 0) {
                if (!smsSender.getAgencyId().equals(agencyId)) {
                    return Response.success();
                }
            }
        } else {
            smsSender.setAgencyId(agencyId);
        }
        smsSenderService.createSenderMessage(smsSender);
        return Response.success();
    }

    @RequestMapping(value = "/updateSender")
    @ResponseBody
    public Response updateSenderMessage(SmsSender smsSender, HttpServletRequest request) {
        Assert.notNull(smsSender.getId(), "修改id不能为空");
        Assert.hasLength(smsSender.getAccount(), "添加账户不能为空");
        Assert.hasLength(smsSender.getPassword(), "添加密码不能为空");
        Assert.hasLength(smsSender.getName(), "短信服务商不能为空");
        Assert.notNull(smsSender.getStatus(), "状态不能为空");
        Assert.notNull(smsSender.getType(), "类型不能为空");

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (!smsSenderService.validateSenderExist(agencyId, smsSender.getType())) {
            return Response.response(RespCode.SERVER_ERROR, "短信服务商不存在");
        }
        if (smsSender.getStatus() == CommonConst.NO) {
            int count = smsSenderService.findStartTemplateByAgencyId(agencyId, smsSender.getId());
            if (count > 0) {
                return Response.response(RespCode.SERVER_ERROR, "停用失败,服务商下含有启用的短信模板");
            }
        } else {

            Integer count = smsSenderService.findStartSenderByAgencyId(agencyId, smsSender.getId());
            if (count > 0) {
                return Response.response(RespCode.SERVER_ERROR, "启用失败,只能同时启用一个短信服务商");
            }
        }

        if (smsSender.getAgencyId() != null) {
            if (agencyId != 0) {
                if (!smsSender.getAgencyId().equals(agencyId)) {
                    return Response.success();
                }
            }
        } else {
            smsSender.setAgencyId(agencyId);
        }

        smsSenderService.updateSenderMessage(smsSender);
        return Response.success();
    }

    @RequestMapping(value = "/delete")
    @ResponseBody
    public Response delete(Integer id, HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);

        int count = smsSenderService.findStartTemplateByAgencyId(agencyId, id);
        if (count > 0) {
            return Response.response(RespCode.SERVER_ERROR, "删除失败,服务商下含有启用的短信模板");
        }
        smsSenderService.remove(agencyId, id);

        return Response.response(RespCode.SUCCESS, "删除非激活渠道成功");
    }

}
