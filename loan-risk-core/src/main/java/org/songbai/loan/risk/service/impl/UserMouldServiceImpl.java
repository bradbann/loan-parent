package org.songbai.loan.risk.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.songbai.cloud.basics.concurrent.Executors;
import org.songbai.loan.risk.jms.RiskJmsNotifyHelper;
import org.songbai.loan.risk.model.user.UserRiskOrderModel;
import org.songbai.loan.risk.mould.variable.MouldCalc;
import org.songbai.loan.risk.service.RiskOrderService;
import org.songbai.loan.risk.service.UserMouldService;
import org.songbai.loan.risk.vo.RiskResultVO;
import org.songbai.loan.vo.risk.RiskOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author navy
 */
@Service
@Slf4j
public class UserMouldServiceImpl implements UserMouldService {
    @Autowired
    RiskOrderService riskOrderService;

    @Autowired
    RiskJmsNotifyHelper notifyHelper;

    @Autowired
    MouldCalc mouldCalc;

    private ExecutorService executorService = null;
    private LinkedBlockingQueue blockingQueue = null;

    @PostConstruct
    public void init() {
        blockingQueue = new LinkedBlockingQueue();
        executorService = Executors.newFixedThreadPool(2, 5, "mould-async", blockingQueue);
    }

    @PreDestroy
    public void shutdown() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    @Override
    public void calcAsyncMulti(final RiskOrderVO vo) {

        if (blockingQueue.size() >= 20) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                //Ignore
            }
        }

        executorService.submit(() -> {
            try {
                calcAsync(vo);
            } catch (Exception e) {
                log.error("多线程异步处理异常", e);
            }
        });


    }

    @Override
    public void calcAsync(RiskOrderVO vo) {
        log.info("start calc order :{}", vo);
        UserRiskOrderModel riskOrderModel = riskOrderService.selectRiskOrderModel(vo.getThridId(), vo.getOrderNumber());

        if (riskOrderModel == null) {
            riskOrderService.submitOrder(vo.getThridId(), vo.getOrderNumber());
        }

        RiskResultVO resultVO = calc(vo);

        log.info("start calc order {},result:", vo, resultVO);

        if (resultVO.getCode() == RiskResultVO.CODE_WAITDATA) {
            riskOrderService.waitData(resultVO);
        } else if (resultVO.getCode() == RiskResultVO.CODE_FAIL) {
            riskOrderService.authFail(resultVO);
        } else {
            riskOrderService.authFinish(resultVO);
        }

        notifyHelper.notifyOrderRiskResult(resultVO);
    }


    @Override
    public RiskResultVO calc(RiskOrderVO vo) {
//        if (mouldCalc.isAllReady(vo.getThridId())) {
//            return mouldCalc.calc(vo.getThridId());
//        }
        try {
            return mouldCalc.calc(vo);
        } catch (Exception e) {
            log.error("风控失败：" + vo.toString(), e);

            return RiskResultVO.builder()
                    .code(RiskResultVO.CODE_FAIL).msg(e.getMessage())
                    .userId(vo.getThridId())
                    .orderNumber(vo.getOrderNumber())
                    .build();
        }
    }


}
