//package org.songbai.loan.user.user.listener;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.collections.CollectionUtils;
//import org.songbai.cloud.basics.concurrent.Executors;
//import org.songbai.loan.constant.JmsDest;
//import org.songbai.loan.constant.user.FinanceConstant;
//import org.songbai.loan.model.finance.FinanceIOModel;
//import org.songbai.loan.user.finance.dao.FinanceIODao;
//import org.songbai.loan.user.finance.service.RepaymentService;
//import org.songbai.loan.user.finance.service.impl.RepaymentFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jms.annotation.JmsListener;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PostConstruct;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//
///**
// * @author: wjl
// * @date: 2018/12/2 13:17
// * Description: 还款回调主动查询订单状态
// */
//@Component
//@Slf4j
//public class RePayAutoQueryListener {
//
//	@Autowired
//	private FinanceIODao ioDao;
//	@Autowired
//	private RepaymentFactory repaymentFactory;
//
//	private ExecutorService pool;
//	private int max;
//
//	@PostConstruct
//	public void init() {
//		max = FinanceConstant.PayPlatform.values().length - 1;
//		pool = Executors.newFixedThreadPool(1, max, "payQuery");
//	}
//
//	@JmsListener(destination = JmsDest.FINANCE_REPAY_QUERY)
//	public void dealOrder() {
//		log.info("执行》》》》用户还款回调补偿操作《《《《开始");
//		long start = System.currentTimeMillis();
//
//		//TODO 多线程去泡畅捷和易宝
//		for (int i = 0; i < max; i++) {
//			pool.execute(()->{
//
//			});
//		}
//		log.info("执行》》》》用户还款回调补偿操作《《《《结束，共耗时【{}】s", System.currentTimeMillis() - start);
//	}
//
//	private void changJieQuery() {
//		List<Integer> ioIds_changJie = ioDao.selectRepayProcessingOrder(FinanceConstant.PayPlatform.CHANGJIE.code);
//		if (CollectionUtils.isEmpty(ioIds_changJie)) {
//			log.info("没有查询到【畅捷待回调或者代扣】的订单");
//			return;
//		}
//		List<FinanceIOModel> ioModels = ioDao.selectBatchIds(ioIds_changJie);
//		log.info("查询到【畅捷{}条待回调或者代扣】的订单", ioModels.size());
//		ioModels.forEach(ioModel -> {
//			RepaymentService bean = repaymentFactory.getBeanByCode(FinanceConstant.PayPlatform.CHANGJIE.code);
//			bean.payQuery(ioModel);
//		});
//	}
//
//	private void yiBaoQuery() {
//		List<Integer> ioIds_yiBao = ioDao.selectRepayProcessingOrder(FinanceConstant.PayPlatform.YIBAO.code);
//		if (CollectionUtils.isEmpty(ioIds_yiBao)) {
//			log.info("没有查询到【易宝待回调或者代扣】的订单");
//			return;
//		}
//		List<FinanceIOModel> ioModels = ioDao.selectBatchIds(ioIds_yiBao);
//		log.info("查询到【易宝{}条待回调或者代扣】的订单", ioModels.size());
//		ioModels.forEach(ioModel -> {
//			RepaymentService bean = repaymentFactory.getBeanByCode(FinanceConstant.PayPlatform.YIBAO.code);
//			bean.payQuery(ioModel);
//		});
//	}
//
//}
