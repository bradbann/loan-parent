//package org.songbai.loan.admin.sms.controller;
//
//
//import org.apache.commons.lang3.StringUtils;
//import org.songbai.cloud.basics.exception.ResolveMsgException;
//import org.songbai.cloud.basics.mvc.Page;
//import org.songbai.cloud.basics.mvc.Response;
//import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
//import org.songbai.loan.constant.CommonConst;
//import org.songbai.loan.model.sms.EmailTemplateModel;
//import org.songbai.loan.admin.sms.service.EmailTemplateService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.util.Assert;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping(value = "/emailTemplate")
//public class EmailTemplateController {
//
//
//    @Autowired
//    EmailTemplateService emailTemplateService;
//
//    /**
//     * 添加短信模板
//     *
//     * @param name
//     * @param template
//     * @return
//     */
//
//    @RequestMapping(value = "/addTemplate")
//    @ResponseBody
//    public Response addTemplate(String name, String template, Integer type, String teleCode) {
//
//        EmailTemplateModel smsTemplate = new EmailTemplateModel();
//        if (StringUtils.isBlank(name)) {
//            throw new ResolveMsgException("common.param.notnull", "name");
//        }
//        if (StringUtils.isBlank(template)) {
//            throw new ResolveMsgException("common.param.notnull", "template");
//        }
//        if (type == null) {
//            throw new ResolveMsgException("common.param.notnull", "type");
//        }
//        if (emailTemplateService.hasEmailTemplate(type, teleCode)) {
//            throw new ResolveMsgException("common.param.repeat", "name");
//        }
//        smsTemplate.setName(name);
//        smsTemplate.setTemplate(template);
//        smsTemplate.setDeleted(CommonConst.DELETED_NO);
//        smsTemplate.setType(type);
//        smsTemplate.setTeleCode(teleCode);
//
//        emailTemplateService.addTemplate(smsTemplate);
//        return Response.success();
//
//    }
//
//    /**
//     * 修改短信模板
//     *
//     * @param id
//     * @param name
//     * @param template
//     * @return
//     */
//    @RequestMapping(value = "/updateTemplate")
//    @ResponseBody
//    public Response updateTemplate(Integer id, String name, String template, Integer type, String teleCode) {
//        Assert.notNull(id, LocaleKit.get("common.param.notnull", "id"));
//        EmailTemplateModel smsTemplate = new EmailTemplateModel();
//        smsTemplate.setId(id);
//
//        if (StringUtils.isBlank(name)) {
//            throw new ResolveMsgException("common.param.notnull", "name");
//        }
//        if (StringUtils.isBlank(template)) {
//            throw new ResolveMsgException("common.param.notnull", "template");
//        }
//        if (type == null) {
//            throw new ResolveMsgException("common.param.notnull", "type");
//        }
//        if (StringUtils.isBlank(teleCode)) {
//            throw new ResolveMsgException("common.param.notnull", "teleCode");
//        }
//        if (emailTemplateService.hasEmailTemplate(type, teleCode)) {
//            EmailTemplateModel temp = emailTemplateService.findTemplate(type, teleCode);
//            if (temp != null && !temp.getId().equals(id)) {
//                throw new ResolveMsgException("common.param.repeat", "type,teleCode");
//            }
//        }
//        smsTemplate.setName(name);
//        smsTemplate.setTemplate(template);
//        smsTemplate.setDeleted(CommonConst.DELETED_NO);
//        smsTemplate.setType(type);
//        smsTemplate.setTeleCode(teleCode);
//        emailTemplateService.updateTemplate(smsTemplate);
//        return Response.success();
//
//    }
//
//    /**
//     * 获取全部短信模板
//     *
//     * @return
//     */
//    @RequestMapping(value = "/getTemplate")
//    @ResponseBody
//    public Response getTemplate() {
//        return Response.success(emailTemplateService.getTemplate());
//
//    }
//
//    /**
//     * 根据id获取短信模板
//     *
//     * @param id
//     * @return
//     */
//    @RequestMapping(value = "/getTemplateById")
//    @ResponseBody
//    public Response getTemplateById(Integer id) {
//        EmailTemplateModel smsTemplate = emailTemplateService.getEmailTemplateById(id);
//        return Response.success(smsTemplate);
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
//    public Response queryPaging(String name, String template, Integer type, String teleCode, Integer page, Integer pageSize) {
//        page = page == null ? 0 : page;
//        pageSize = pageSize == null ? Page.DEFAULE_PAGESIZE : pageSize;
//
//        Page<EmailTemplateModel> resultPage = emailTemplateService.pagingQuery(name, template, type, teleCode, page, pageSize);
//        return Response.success(resultPage);
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
//    public Response remove(Integer id) {
//        if (id == null) {
//            throw new ResolveMsgException("common.param.notnull", "id");
//        }
//        emailTemplateService.removeEmailTemplate(id);
//        return Response.success();
//    }
//
//}
