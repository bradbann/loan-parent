package org.songbai.loan.sms.jms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.sms.SmsConstant;
import org.songbai.loan.model.sms.SmsSender;
import org.songbai.loan.sms.dao.SmsDao;
import org.songbai.loan.sms.service.SmsSyncUpAbstractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Author: qmw
 * Date: 2019/1/21 4:02 PM
 * 短信上行日志添加到意见反馈
 */
@Component
public class SmsLogListener {

    @Resource(name = "paopaoSyncUp")
    SmsSyncUpAbstractService paopaoSyncUpService;

    @Autowired
    private SmsDao smsDao;
    private static final Logger logger = LoggerFactory.getLogger(SmsLogListener.class);

    @JmsListener(destination = JmsDest.MSG_SYNC_UP)
    public void msgSyncUp() {
        logger.info("开始同步泡泡云上行短信...");

        List<SmsSender> list = smsDao.findSmsSenderByStart();

        for (SmsSender sender : list) {

            SmsConstant.SenderType senderType = SmsConstant.SenderType.parse(sender.getType());
            if (senderType == null) {
                logger.info("短信发送器类型没有找到：{}", sender);
                continue;
            }
            logger.info("开始同步泡泡云上行短信...sender={}",sender);
            switch (senderType) {
                case SMS_SENDER_TYPE_ALI:
                    logger.info("同步上行短信:阿里大鱼尚未配置");
                    break;
                case SMS_SENDER_TYPE_YUNXIN:
                    logger.info("同步上行短信:云信尚未配置");
                    break;
                case SMS_SENDER_TYPE_JUHE:
                    logger.info("同步上行短信:聚合尚未配置");
                    break;
                case SMS_SENDER_TYPE_TLSG:
                    logger.info("同步上行短信:TLSG尚未配置");
                    break;
                case SMS_SENDER_TYPE_CHUANGLAN:
                    logger.info("同步上行短信:创蓝253尚未配置");
                    break;
                case SMS_SENDER_TYPE_PAOPAO:
                    logger.info("同步上行短信:泡泡云");
                    paopaoSyncUpService.msgSyncUp(sender);
                    break;
                default:
                    logger.info("同步上行短信: 没有找到短信渠道类型： " + sender.getType());
            }

        }
    }
}