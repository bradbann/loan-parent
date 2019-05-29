package org.songbai.loan.risk.moxie.carrier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.risk.moxie.carrier.api.ReportClient;
import org.songbai.loan.risk.moxie.carrier.billitem.CarrierReportTask;
import org.songbai.loan.risk.moxie.carrier.model.ReportDataModel;
import org.songbai.loan.risk.moxie.carrier.mongo.ReportDataRepository;
import org.songbai.loan.risk.platform.helper.RiskNotifyJmsHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by zengdongping on 17/1/3.
 */
@Service
public class CarrierReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarrierReportService.class);
    @Autowired
    private ReportClient reportClient;

    @Autowired
    private ReportDataRepository reportDataRepository;

    @Autowired
    RiskNotifyJmsHelper notifyJmsHelper;

    public void fetchReport(final CarrierReportTask task) {
        // 这里交给线程池处理，防止下面的业务处理时间太长，导致超时。
        // 超时会导致魔蝎数据进行重试，会收到重复的回调请求

        try {
            if (task.isResult()) {
                String reportData = reportClient.getReportBasic(task.getMobile(), task.getTaskId());
                saveReportData(task, reportData);


                notifyJmsHelper.notifyVariableExtract(VariableConst.VAR_SOURCE_MOXIE_CARRIER_REPORT,
                        task.getUserId(), task.getTaskId());

            } else {
                LOGGER.error("carrier report result is false . task:{},message:{}", task.getTaskId(), task.getMessage());

                throw new BusinessException(600, "fetch carrier report  failed,taskId:" + task.getTaskId());
            }


        } catch (Exception e) {
            LOGGER.error("fetchBill failed. task:{}", task.getTaskId(), e);
            throw new BusinessException(600, "fetch taobao report  failed,taskId:" + task.getTaskId());
        }

    }

    private void saveReportData(CarrierReportTask task, String reportData) {
        ReportDataModel reportEntity = new ReportDataModel();
        try {

            reportEntity.setUserId(task.getUserId());
            reportEntity.setMobile(task.getMobile());
            reportEntity.setTaskId(task.getTaskId());
            reportEntity.setReportData(reportData);
            reportEntity.setName(task.getName());
            reportEntity.setIdcard(task.getIdcard());
            reportEntity.setMessage(task.getMessage());
            reportEntity.setCreateTime(new Date());
            reportEntity.setUpdateTime(new Date());

            ReportDataModel isExist = reportDataRepository.getReportData(task.getUserId(), task.getMobile());
            if (isExist != null) {
                reportDataRepository.deleteReportData(task.getUserId(), task.getMobile());
            }
            reportDataRepository.insert(reportEntity);
        } catch (Exception e) {
            LOGGER.error("insert failed. taskId:{}", task.getTaskId(), e);
        }
    }

}
