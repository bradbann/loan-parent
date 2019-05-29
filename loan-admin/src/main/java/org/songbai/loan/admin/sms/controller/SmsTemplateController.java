package org.songbai.loan.admin.sms.controller;

import org.songbai.cloud.basics.exception.ResolveMsgException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.RespCode;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.sms.model.SmsSenderTemplateVO;
import org.songbai.loan.admin.sms.service.SmsTemplateService;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.sms.SmsTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 短信模板接口
 *
 * @author czh
 */
@Controller
@RequestMapping(value = "/smsTemplate")
public class SmsTemplateController {

    @Autowired
    SmsTemplateService smsTemplateService;
    @Autowired
    private AdminUserHelper adminUserHelper;

    /**
     * 添加短信模板
     *
     * @return
     */
    @RequestMapping(value = "/addTemplate")
    @ResponseBody
    public Response addTemplate(SmsTemplate smsTemplate, HttpServletRequest request) {
        Assert.hasLength(smsTemplate.getSign(), "签名不能为空");
        Assert.hasLength(smsTemplate.getTemplate(), "模板内容不能为空");
        Assert.notNull(smsTemplate.getStatus(), "状态不能为空");
        Assert.notNull(smsTemplate.getSenderId(), "senderId不能为空");
        Assert.notNull(smsTemplate.getType(), "type不能为空");
        Assert.notNull(smsTemplate.getVestId(), "vestId不能为空");

        Integer agencyId = adminUserHelper.getAgencyId(request);

        if (smsTemplate.getAgencyId() != null) {
            if (agencyId != 0) {
                if (!smsTemplate.getAgencyId().equals(agencyId)) {
                    return Response.success();
                }
            }
        } else {
            smsTemplate.setAgencyId(agencyId);
        }


        SmsTemplate t = smsTemplateService.findTemplatByAgencyId(smsTemplate);
        if (t != null) {
            return Response.response(RespCode.SERVER_ERROR, "该类型模板已存在");
        }
        smsTemplateService.addTemplate(smsTemplate);
        return Response.success();

    }

    /**
     * 修改短信模板
     *
     * @return
     */
    @RequestMapping(value = "/updateTemplate")
    @ResponseBody
    public Response updateTemplate(SmsTemplate smsTemplate, HttpServletRequest request) {
        Assert.notNull(smsTemplate.getId(), "需要修改的id不能为空");
        Assert.hasLength(smsTemplate.getSign(), "签名不能为空");
        Assert.hasLength(smsTemplate.getTemplate(), "模板内容不能为空");
        Assert.notNull(smsTemplate.getStatus(), "状态不能为空");
        Assert.notNull(smsTemplate.getSenderId(), "senderId不能为空");
        Assert.notNull(smsTemplate.getType(), "type不能为空");
        Assert.notNull(smsTemplate.getVestId(), "vestId不能为空");

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (smsTemplate.getAgencyId() != null) {
            if (agencyId != 0) {
                if (!smsTemplate.getAgencyId().equals(agencyId)) {
                    return Response.success();
                }
            }
        } else {
            smsTemplate.setAgencyId(agencyId);
        }

        SmsTemplate t = smsTemplateService.findTemplatByAgencyId(smsTemplate);
        if (t != null) {
            return Response.response(RespCode.SERVER_ERROR, "修改类型模板已存在");
        }


        smsTemplateService.updateTemplate(smsTemplate);
        return Response.success();
    }

    /**
     * 获取全部短信模板
     *
     * @return
     */
    @RequestMapping(value = "/getTemplate")
    @ResponseBody
    public Response getTemplate(HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        return Response.success(smsTemplateService.getTemplate(agencyId));

    }

    /**
     * 根据id获取短信模板
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/getTemplateById")
    @ResponseBody
    public Response getTemplateById(Integer id, HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        SmsTemplate smsTemplate = smsTemplateService.getById(id, agencyId);
        return Response.success(smsTemplate);
    }

    /**
     * 分页查询
     *
     * @return
     */
    @RequestMapping(value = "/pagingQuery")
    @ResponseBody
    public Response queryPaging(PageRow page, Integer agencyId, Integer vestId, HttpServletRequest request) {
        page.initLimit();
        Integer currentAgencyId = adminUserHelper.getAgencyId(request);
        if (currentAgencyId != 0) {
            agencyId = currentAgencyId;
        }
        Page<SmsSenderTemplateVO> resultPage = smsTemplateService.pagingTemplateQuery(agencyId, page, vestId);
        return Response.success(resultPage);
    }

    /**
     * 通过改变isDelete来表示是否删除 进行软删除
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/remove")
    @ResponseBody
    public Response remove(Integer id, HttpServletRequest request) {
        if (id == null) {
            throw new ResolveMsgException("common.param.notnull", "id");
        }
        Integer agencyId = adminUserHelper.getAgencyId(request);
        smsTemplateService.remove(id, agencyId);
        return Response.success();
    }

    @RequestMapping(value = "/getChannelTemplate")
    @ResponseBody
    public Response getChannelTemplate(HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        List<SmsTemplate> resultPage = smsTemplateService.getTemplate(agencyId);
        return Response.success(resultPage);
    }

    @GetMapping("/list")
    @ResponseBody
    public Response getList(HttpServletRequest request) {

        List<SmsTemplate> list = smsTemplateService.getList(adminUserHelper.getAgencyId(request));
        return Response.success(list);
    }
}