package org.songbai.loan.sms.service;

import org.songbai.loan.model.agency.ExchangeAdminModel;
import org.songbai.loan.model.sms.EmailNotify;

import java.util.Map;

public interface EmailService {

    /**
     * 发送简单邮件
     *
     * @param sendTo  收件人地址
     * @param title   邮件标题
     * @param content 邮件内容
     */
    public void sendSimpleMail(String sendTo, String title, String content);


//    /**
//     * 发送模板邮件
//     *
//     * @param sendTo      收件人地址
//     * @param title       邮件标题
//     * @param content<key 内容> 邮件内容
//     */
//    public void sendTemplateMail(String sendTo, String title, String content, Map<String, Object> param);

    void sendMailByAgency(String email, String name, String template, EmailNotify notify, ExchangeAdminModel model);
}
