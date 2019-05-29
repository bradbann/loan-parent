package org.songbai.loan.user.finance.listener;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.user.finance.dao.FinanceIODao;
import org.songbai.loan.user.finance.service.JhPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 聚合支付自动查询订单状态
 */
@Component
public class JhPayOrderListener {
    private static final Logger logger = LoggerFactory.getLogger(JhPayService.class);

    @Autowired
    FinanceIODao financeIODao;
    @Autowired
    JhPayService jhPayService;
    @Autowired
    SpringProperties properties;

    @JmsListener(destination = JmsDest.JH_AUTO_QUERY)
    public void autoQuery() {
//        logger.info(">>>>JhPayService autoQuery is start");
        int page = 0, pageSize = 50;
        Integer dealSize = 0;
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime begin = now.minusMinutes(properties.getInteger("user:jh:pay:query:begin",60));
        LocalDateTime end = now.minusMinutes(properties.getInteger("user:jh:pay:query:end",2));
        while (true) {
            List<FinanceIOModel> ioList = financeIODao.selectNotConfirmOrderByJh(page, pageSize,begin,end);
            if (CollectionUtils.isEmpty(ioList)) {
//                logger.info("JhPayService autoQuery list is empty");
                break;
            }
            for (FinanceIOModel model : ioList) {
                jhPayService.dealJhOrder(model);
            }
            dealSize = dealSize + ioList.size();
            if (ioList.size() < pageSize) break;
            page++;
        }

//        logger.info(">>>>JhPayService autoQuery is end");
        logger.info("jhPayService autoQuery dealSize={}", dealSize);
    }

}
