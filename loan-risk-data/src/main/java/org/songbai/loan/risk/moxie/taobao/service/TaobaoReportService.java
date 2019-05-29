package org.songbai.loan.risk.moxie.taobao.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.risk.moxie.taobao.api.TaobaoReportClient;
import org.songbai.loan.risk.moxie.taobao.model.TaobaoReportModel;
import org.songbai.loan.risk.moxie.taobao.model.vo.TaobaoReportTask;
import org.songbai.loan.risk.moxie.taobao.mongo.TaobaoReportRepository;
import org.songbai.loan.risk.platform.helper.RiskNotifyJmsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class TaobaoReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaobaoReportService.class);

    @Autowired
    TaobaoReportRepository reportRepository;

    @Autowired
    TaobaoReportClient reportClient;


    @Autowired
    RiskNotifyJmsHelper notifyJmsHelper;

    public void fetchReport(final TaobaoReportTask task) {
        // 这里交给线程池处理，防止下面的业务处理时间太长，导致超时。
        // 超时会导致魔蝎数据进行重试，会收到重复的回调请求

        try {
            if (task.getResult()) {

                String reportData = reportClient.getReport(task.getTaskId());

                saveReportData(task, reportData);

                notifyJmsHelper.notifyVariableExtract(VariableConst.VAR_SOURCE_MOXIE_TAOBAO_REPORT,
                        task.getUserId(), task.getTaskId());

            } else {
                LOGGER.error("carrier report result is false . task:{},message:{}", task.getTaskId(), task.getMessage());

                throw new BusinessException(600, "fetch taobao report  failed,taskId:" + task.getTaskId());
            }


        } catch (Exception e) {
            LOGGER.error("fetchBill failed. task:{}", task.getTaskId(), e);

            throw new BusinessException(600, "fetch taobao report  failed,taskId:" + task.getTaskId());
        }

    }

    private void saveReportData(TaobaoReportTask task, String reportData) {
        TaobaoReportModel reportModel = new TaobaoReportModel();
        try {

            reportModel.setUserId(task.getUserId());
            reportModel.setTaskId(task.getTaskId());
            reportModel.setReportData(reportData);
            reportModel.setMessage(task.getMessage());
            reportModel.setCreateTime(new Date());
            reportModel.setUpdateTime(new Date());

            TaobaoReportModel isExist = reportRepository.getReportData(task.getUserId(), task.getTaskId());
            if (isExist != null) {
                reportRepository.deleteReportData(task.getUserId(), task.getTaskId());
            }
            reportRepository.insert(reportModel);
        } catch (Exception e) {
            LOGGER.error("insert failed. taskId:{}", task.getTaskId(), e);
        }
    }


}
