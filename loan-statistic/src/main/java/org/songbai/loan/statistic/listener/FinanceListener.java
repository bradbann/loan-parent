package org.songbai.loan.statistic.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.model.statistic.dto.PayStatisticDTO;
import org.songbai.loan.model.statistic.dto.RepayStatisticDTO;
import org.songbai.loan.statistic.dao.PayStatisticDao;
import org.songbai.loan.statistic.dao.RepayStatisticDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

/**
 * Author: qmw
 * Date: 2018/11/30 2:28 PM
 * 处理放款还款相关数据
 */
@Component
public class FinanceListener {
    private static final Logger logger = LoggerFactory.getLogger(FinanceListener.class);

    @Autowired
    private PayStatisticDao payStatisticDao;
    @Autowired
    private RepayStatisticDao repayStatisticDao;


    @JmsListener(destination = JmsDest.ORDER_LOAN_PAY)
    public void payProcessor(PayStatisticDTO dto) {
        logger.info("放款统计>>>>,data={}", dto);
        if (dto == null) {
            logger.info("放款统计>>>>,数据为空", dto);
            return;
        }
        if (dto.getAgencyId() == null || dto.getRepayDate() == null || dto.getPayDate() == null || dto.getVestId() == null) {
            logger.info("放款统计>>>>,agencyId和repayDate和payDate和vestId不能为空", dto);
            return;
        }
        Integer result = payStatisticDao.updatePayStatisticByAgencyIdAndPayDate(dto);
        if (result == 0) {
            payStatisticDao.insertPayStatisticByAgencyIdAndPayDate(dto);
        }

        Integer repayResult = repayStatisticDao.updateRepayStatisticByAgencyIdAndPayDate(dto);
        if (repayResult == 0) {
            repayStatisticDao.insertRepayStatisticByAgencyIdAndPayDate(dto);
        }
    }

    /**
     * 坏账/还款/逾期/减免
     */
    @JmsListener(destination = JmsDest.ORDER_CONFIRM_OPT)
    public void repayProcessor(RepayStatisticDTO dto) {
        logger.info("还款统计>>>>,data={}", dto);
        if (dto == null) {
            logger.info("还款统计>>>>,数据为空", dto);
            return;
        }
        if (dto.getAgencyId() == null || dto.getRepayDate() == null || dto.getVestId() == null) {
            logger.info("还款统计>>>>,agencyId和repayDate和vestId不能为空,dto={}", dto);
            return;
        }
        Integer result = repayStatisticDao.updateRepayStatistic(dto);
        if (result == 0) {
            logger.error("还款统计>>>>,更新为0,data={}", dto);
        }
    }
}
