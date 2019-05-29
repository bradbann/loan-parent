//package org.songbai.loan.admin.sms.controller;
//
//import org.songbai.cloud.basics.mvc.Page;
//import org.songbai.cloud.basics.mvc.RespCode;
//import org.songbai.cloud.basics.mvc.Response;
//import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
//import org.songbai.loan.admin.admin.support.AdminUserHelper;
//import org.songbai.loan.admin.sms.model.SmsSenderTemplateVO;
//import org.songbai.loan.admin.sms.service.SmsSenderTemplateService;
//import org.songbai.loan.constant.CommonConst;
//import org.songbai.loan.model.sms.SmsSenderTemplate;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.util.Assert;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.servlet.http.HttpServletRequest;
//
///**
// * 短信模板设置
// *
// * @author czh
// */
//@Controller
//@RequestMapping(value = "/smsSenderTemplate")
//public class SmsSenderTemplateController {
//
//    @Autowired
//    SmsSenderTemplateService smsSenderTemplateService;
//    @Autowired
//    private AdminUserHelper adminUserHelper;
//
//    /**
//     * 添加短信渠道
//     */
//
//    @RequestMapping(value = "/addSenderTemplate")
//    @ResponseBody
//    public Response addSenderTemplate(Integer templateId, String extraParam, String name, String sign, Integer senderId, HttpServletRequest request) {
//        Assert.notNull(templateId, LocaleKit.get("common.param.notnull", "templateId"));
//        Assert.notNull(senderId, LocaleKit.get("common.param.notnull", "senderId"));
//        Integer agencyId = adminUserHelper.getAgencyId(request);
//
//        SmsSenderTemplate senderTemplate = new SmsSenderTemplate();
//        if (smsSenderTemplateService.hasSenderTemplate(agencyId,templateId, senderId)) {
//            return Response.response(RespCode.SERVER_ERROR, "已存在该发送渠道下该短信模板");
//        }
//
//
//        senderTemplate.setAgencyId(agencyId);
//        senderTemplate.setTemplateId(templateId);
//        senderTemplate.setName(name);
//        senderTemplate.setSign(sign);
//        senderTemplate.setExtraParam(extraParam);
//        senderTemplate.setSenderId(senderId);
//        senderTemplate.setDeleted(CommonConst.DELETED_NO);
//        senderTemplate.setStatus(CommonConst.STATUS_VALID);
//        smsSenderTemplateService.addSenderTemplate(senderTemplate);
//        return Response.success();
//
//
//    }
//
//    /**
//     * 修改渠道
//     */
//
//    @RequestMapping(value = "/updateSenderTemplate")
//    @ResponseBody
//    public Response updateSenderTemplate(Integer id, String name, String sign, String extraParam, Integer status, HttpServletRequest request) {
//        Assert.notNull(id, "修改的短信渠道id不能为空");
//        Integer agencyId = adminUserHelper.getAgencyId(request);
//
//        SmsSenderTemplate senderTemplate = new SmsSenderTemplate();
//        senderTemplate.setId(id);
//        senderTemplate.setAgencyId(agencyId);
//        senderTemplate.setName(name);
//        senderTemplate.setSign(sign);
//        senderTemplate.setExtraParam(extraParam);
//        senderTemplate.setDeleted(CommonConst.DELETED_NO);
//        senderTemplate.setStatus(status);
//        smsSenderTemplateService.updateSenderTemplate(senderTemplate);
//        return Response.success();
//    }
//
//    /**
//     * 获取全部短信渠道
//     *
//     * @return
//     */
//
//    @RequestMapping(value = "/getSenderTemplate")
//    @ResponseBody
//    public Response getSenderTemplate(HttpServletRequest request) {
//        Integer agencyId = adminUserHelper.getAgencyId(request);
//        return Response.success(smsSenderTemplateService.getSenderTemplate(agencyId));
//
//    }
//
//    /**
//     * 根据id获取短信渠道
//     *
//     * @param id
//     * @return
//     */
//    @RequestMapping(value = "/getSenderTemplateById")
//    @ResponseBody
//    public Response getSenderTemplateById(Integer id, HttpServletRequest request) {
//        Integer agencyId = adminUserHelper.getAgencyId(request);
//        SmsSenderTemplate senderTemplate = smsSenderTemplateService.getById(agencyId, id);
//        return Response.success(senderTemplate);
//
//    }
//
//    /**
//     * 分页查询
//     *
//     * @param page
//     * @param pageSize
//     * @return
//     */
//    @RequestMapping(value = "/pagingQuery")
//    @ResponseBody
//    public Response querypaging(Integer templateId, Integer type, Integer page, Integer pageSize,HttpServletRequest request) {
//
//        page = page == null ? 0 : page;
//        pageSize = pageSize == null ? Page.DEFAULE_PAGESIZE : pageSize;
//
//        Integer agencyId = adminUserHelper.getAgencyId(request);
//
//        Page<SmsSenderTemplateVO> resultPage = smsSenderTemplateService.pagingQuery(agencyId,templateId,
//                type, page, pageSize);
//        return Response.success(resultPage);
//
//    }
//
//    /**
//     * 通过改变isDelete来表示是否删除 进行软删除
//     *
//     * @param id
//     * @return
//     */
//    @RequestMapping(value = "/remove")
//    @ResponseBody
//    public Response remove(Integer id,HttpServletRequest request) {
//        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Assert.notNull(id, "需要删除的id不能为空");
//        SmsSenderTemplate senderTemplate = new SmsSenderTemplate();
//        senderTemplate.setId(id);
//        smsSenderTemplateService.remove(id,agencyId);
//        return Response.success();
//    }
//
//}