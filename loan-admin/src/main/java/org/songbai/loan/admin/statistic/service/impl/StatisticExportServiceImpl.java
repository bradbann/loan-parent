package org.songbai.loan.admin.statistic.service.impl;

import org.apache.commons.lang.StringUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.excel.ExcelNewHelper;
import org.songbai.cloud.basics.utils.excel.ExcelWriteBuilder;
import org.songbai.loan.admin.statistic.dao.*;
import org.songbai.loan.admin.statistic.model.po.ChannelStatisPo;
import org.songbai.loan.admin.statistic.model.po.ReviewStatisPo;
import org.songbai.loan.admin.statistic.model.po.StatisticPayPO;
import org.songbai.loan.admin.statistic.model.po.StatisticUserPO;
import org.songbai.loan.admin.statistic.model.vo.*;
import org.songbai.loan.admin.statistic.service.StatisticExportService;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.statistic.RepayStatisticModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StatisticExportServiceImpl implements StatisticExportService {
    @Autowired
    ExcelNewHelper excelNewHelper;
    @Autowired
    ReviewOrderDao reviewOrderDao;
    @Autowired
    StatisticServiceImpl statisticService;
    @Autowired
    StatisticDao statisticDao;
    @Autowired
    private PayStatisticDao payStatisticDao;
    @Autowired
    private RepayStatisticDao repayStatisticDao;
    @Autowired
    private UserStatisticDao userStatisticDao;
    @Autowired
    private ComAgencyService comAgencyService;
    @Autowired
    private UserActionStatisticDao userActionStatisticDao;

    @Override
    public void exportReviewStatis(ReviewStatisPo po, HttpServletResponse response) {
        Integer count = reviewOrderDao.getAgencyReviewCount(po);

        if (count == 0) {
            throw new BusinessException(AdminRespCode.INNER_JSON_ERROR);
        }
        String excelTitle = "信审";
        String excelName = "统计";
        if (po.getIsVest() == CommonConst.YES) excelName = "马甲统计";

        if (po.getIsChannelOrder() == CommonConst.YES) excelTitle = "渠道订单";
        else if (po.getIsProduct() == CommonConst.YES) excelTitle = "标的";

        ExcelWriteBuilder excelWriteBuilder = excelNewHelper.createExcelWriteBuilder(excelTitle + excelName);
        excelWriteBuilder.addHeaderColumn("提单日期", "calcDate");
        if (po.getAgencyId() == null)
            excelWriteBuilder.addHeaderColumn("代理名称", "agencyName");
        if (po.getIsVest() == CommonConst.YES)
            excelWriteBuilder.addHeaderColumn("马甲名称", "vestName");
        if (po.getIsProduct() == CommonConst.YES) {
            excelWriteBuilder
                    .addHeaderColumn("标的名称", "productName")
                    .addHeaderColumn("标的分组", "productGroupName");
        }
        if (po.getIsChannelOrder() == CommonConst.YES) {
            excelWriteBuilder
                    .addHeaderColumn("渠道名称", "channelCode");
        }
        excelWriteBuilder
                .addHeaderColumn("订单总数", "orderCount")
                .addHeaderColumn("风控通过量", "machineToTransCount")
                .addHeaderColumn("风控通过率", "machineToTransRate")
                .addHeaderColumn("风控拒绝量", "machineFailCount")
                .addHeaderColumn("风控拒绝率", "machineFailRate")
                .addHeaderColumn("复审量", "machineSuccCount")
                .addHeaderColumn("复审率", "machineSuccRate")
                .addHeaderColumn("复审通过量", "reviewSuccCount")
                .addHeaderColumn("复审通过率", "reviewSuccRate")
                .addHeaderColumn("复审拒绝量", "reviewFailCount")
                .addHeaderColumn("复审拒绝率", "reviewFailRate")
                .addHeaderColumn("总通过订单量", "succCount")
                .addHeaderColumn("总通过率", "succRate");
        if (po.getIsChannelOrder() == CommonConst.YES || po.getIsProduct() == CommonConst.YES) {
            excelWriteBuilder
                    .addHeaderColumn("超期订单数", "expireCount")
                    .addHeaderColumn("首逾订单量", "firstOverdueCount")
                    .addHeaderColumn("首逾率", "firstOverdueRate")
                    .addHeaderColumn("在逾订单量", "inOverdueCount")
                    .addHeaderColumn("在逾率", "inOverdueRate");
        }
        int totalRow = 1000;
        // 默认查询
        po.setPage(0);
        po.setPageSize(totalRow);
        po.initLimit();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (true) {
            List<OrderReviewVo> list = reviewOrderDao.findReviewGroupList(po);
            list = statisticService.handleReviewList(list, po);

            for (OrderReviewVo vo : list) {
                Map<String, Object> obj = new ConcurrentHashMap<>();

                obj.put("calcDate", vo.getStatisDate());
                obj.put("orderCount", vo.getOrderCount());
                obj.put("machineToTransCount", vo.getMachineToTransCount());
                obj.put("machineToTransRate", vo.getMachineToTransRate() + "%");
                obj.put("machineFailCount", vo.getMachineFailCount());
                obj.put("machineFailRate", vo.getMachineFailRate() + "%");
                obj.put("machineSuccCount", vo.getMachineSuccCount());
                obj.put("machineSuccRate", vo.getMachineSuccRate() + "%");
                obj.put("reviewSuccCount", vo.getReviewSuccCount());
                obj.put("reviewSuccRate", vo.getReviewSuccRate() + "%");
                obj.put("reviewFailCount", vo.getReviewFailCount());
                obj.put("reviewFailRate", vo.getReviewFailRate() + "%");
                obj.put("succCount", vo.getSuccCount());
                obj.put("succRate", vo.getSuccRate() + "%");

                if (po.getAgencyId() == null)
                    obj.put("agencyName", ifnull(vo.getAgencyName()));
                if (po.getIsVest() == CommonConst.YES)
                    obj.put("vestName", ifnull(vo.getVestName()));
                if (po.getIsChannelOrder() == CommonConst.YES || po.getIsProduct() == CommonConst.YES) {
                    obj.put("expireCount", vo.getExpireCount());
                    obj.put("firstOverdueCount", vo.getFirstOverdueCount());
                    obj.put("firstOverdueRate", vo.getFirstOverdueRate() + "%");
                    obj.put("inOverdueCount", vo.getInOverdueCount());
                    obj.put("inOverdueRate", vo.getInOverdueRate() + "%");
                }
                if (po.getIsChannelOrder() == CommonConst.YES) {
                    obj.put("channelCode", vo.getChannelCode());

                    AgencyChannelModel channel = comAgencyService.findChannelNameByAgencyIdAndChannelCode(vo.getAgencyId(), vo.getChannelCode());
                    if (channel != null&&StringUtil.isNotEmpty(channel.getChannelName())) {
                        obj.put("channelCode", channel.getChannelName());
                    }
                }
                if (po.getIsProduct() == CommonConst.YES) {
                    obj.put("productName", ifnull(vo.getProductName()));
                    obj.put("productGroupName", ifnull(vo.getProductGroupName()));
                }


                excelWriteBuilder.appendRowData(obj);
            }
            po.initLimit();
            if (list.size() < totalRow) break;
            po.setPage(po.getPage() + 1);
        }

        try {
            excelNewHelper.write2Servlet(response, excelTitle + excelName, excelWriteBuilder);
        } catch (IOException e) {
            throw new BusinessException(AdminRespCode.INNER_RESULT_ERROR);
        }
    }

    @Override
    public void exportActorReviewStatis(ReviewStatisPo po, HttpServletResponse response) {
        Integer count = reviewOrderDao.getActorStatisCount(po);

        if (count == 0) {
            throw new BusinessException(AdminRespCode.INNER_JSON_ERROR);
        }
        String excelTitle = "信审人员";
        String excelName = "统计";
        if (po.getIsVest() == CommonConst.YES) excelName = "马甲统计";


        ExcelWriteBuilder excelWriteBuilder = excelNewHelper.createExcelWriteBuilder(excelTitle + excelName);
        excelWriteBuilder.addHeaderColumn("提单日期", "calcDate");
        if (po.getAgencyId() == null)
            excelWriteBuilder.addHeaderColumn("代理名称", "agencyName");
        if (po.getIsVest() == CommonConst.YES)
            excelWriteBuilder.addHeaderColumn("马甲名称", "vestName");

        excelWriteBuilder
                .addHeaderColumn("审核员", "actorName")
                .addHeaderColumn("审核单量", "reviewCount")
                .addHeaderColumn("通过订单量", "succCount")
                .addHeaderColumn("超期订单量", "expireCount")
                .addHeaderColumn("通过率", "succRate")
                .addHeaderColumn("首逾率", "firstOverdueRate")
                .addHeaderColumn("在逾率", "inOverdueRate");

        int totalRow = 1000;
        // 默认查询
        po.setPage(0);
        po.setPageSize(totalRow);
        po.initLimit();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (true) {
            List<ActorReviewVo> list = reviewOrderDao.findActorStatisGroupList(po);

            list = statisticService.handleActorReviewList(list, po);

            for (ActorReviewVo vo : list) {
                Map<String, Object> obj = new ConcurrentHashMap<>();

                obj.put("calcDate", vo.getStatisDate());
                obj.put("reviewCount", vo.getReviewCount());
                obj.put("succCount", vo.getSuccCount());
                obj.put("expireCount", vo.getExpireCount());
                obj.put("succRate", vo.getSuccRate() + "%");
                obj.put("firstOverdueRate", vo.getFirstOverdueRate() + "%");
                obj.put("inOverdueRate", vo.getInOverdueRate() + "%");
                obj.put("actorName", ifnull(vo.getActorName()));

                if (po.getAgencyId() == null)
                    obj.put("agencyName", ifnull(vo.getAgencyName()));
                if (po.getIsVest() == CommonConst.YES)
                    obj.put("vestName", ifnull(vo.getVestName()));

                excelWriteBuilder.appendRowData(obj);
            }
            po.initLimit();
            if (list.size() < totalRow) break;
            po.setPage(po.getPage() + 1);
        }

        try {
            excelNewHelper.write2Servlet(response, excelTitle + excelName, excelWriteBuilder);
        } catch (IOException e) {
            throw new BusinessException(AdminRespCode.INNER_RESULT_ERROR);
        }
    }

    @Override
    public void exportChannelStatis(ChannelStatisPo po, HttpServletResponse response) {
        Integer count = statisticDao.queryUserStatisCount(po);

        if (count == 0) {
            throw new BusinessException(AdminRespCode.INNER_JSON_ERROR);
        }
        String excelTitle = "渠道";
        String excelName = "统计";
        if (po.getIsVest() == CommonConst.YES) excelName = "马甲统计";


        ExcelWriteBuilder excelWriteBuilder = excelNewHelper.createExcelWriteBuilder(excelTitle + excelName);
        excelWriteBuilder.addHeaderColumn("提单日期", "calcDate");
        if (po.getAgencyId() == null)
            excelWriteBuilder.addHeaderColumn("代理名称", "agencyName");
        if (po.getIsVest() == CommonConst.YES)
            excelWriteBuilder.addHeaderColumn("马甲名称", "vestName");

        excelWriteBuilder
                .addHeaderColumn("渠道名称", "channelCode")
                .addHeaderColumn("uv", "uvCount")
                .addHeaderColumn("总注册数", "registerCount")
                .addHeaderColumn("登录人数", "loginCount")
                .addHeaderColumn("总提单数", "orderCount")
                .addHeaderColumn("提单率", "orderRate")
                .addHeaderColumn("下款量", "payCount")
                .addHeaderColumn("下款率", "payRate");

        int totalRow = 1000;
        // 默认查询
        po.setPage(0);
        po.setPageSize(totalRow);
        po.initLimit();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (true) {
            List<UserStatisVo> list = statisticDao.findUserStatisList(po);

            list = statisticService.handleChannelList(list, po);

            for (UserStatisVo vo : list) {
                Map<String, Object> obj = new ConcurrentHashMap<>();

                obj.put("calcDate", vo.getStatisDate());
                obj.put("channelCode", vo.getChannelCode());
                obj.put("uvCount", vo.getUvCount());
                obj.put("registerCount", vo.getRegisterCount());
                obj.put("loginCount", vo.getLoginCount());
                obj.put("orderCount", vo.getOrderCount());
                obj.put("orderRate", vo.getOrderRate() + "%");
                obj.put("payCount", vo.getPayCount());
                obj.put("payRate", vo.getPayRate() + "%");

                if (po.getAgencyId() == null)
                    obj.put("agencyName", ifnull(vo.getAgencyName()));
                if (po.getIsVest() == CommonConst.YES)
                    obj.put("vestName", ifnull(vo.getVestName()));

                if (StringUtil.isNotEmpty(vo.getChannelCode())) {

                    AgencyChannelModel channel = comAgencyService.findChannelNameByAgencyIdAndChannelCode(vo.getAgencyId(), vo.getChannelCode());
                    if (channel != null && StringUtil.isNotEmpty(channel.getChannelName())) {
                        vo.setChannelCode(channel.getChannelName());
                    }
                }
                excelWriteBuilder.appendRowData(obj);
            }
            po.initLimit();
            if (list.size() < totalRow) break;
            po.setPage(po.getPage() + 1);
        }

        try {
            excelNewHelper.write2Servlet(response, excelTitle + excelName, excelWriteBuilder);
        } catch (IOException e) {
            throw new BusinessException(AdminRespCode.INNER_RESULT_ERROR);
        }
    }

    @Override
    public void statisticRepaymentStatis(StatisticPayPO po, Integer currentAgencyId, HttpServletResponse response) {

        po.setStartDate(StringUtil.trimToNull(po.getStartDate()));
        po.setEndDate(StringUtil.trimToNull(po.getEndDate()));

        int count = repayStatisticDao.findStatisticRepaymentCount(po);
        if (count <= 0) {
            throw new BusinessException(AdminRespCode.INNER_JSON_ERROR);
        }

        String excelName = CommonConst.YES == po.getIsVest() ? "还款统计(马甲)" : "还款统计";

        ExcelWriteBuilder builder = excelNewHelper.createExcelWriteBuilder(excelName);
        builder.addHeaderColumn("应还日期", "repayDate");

        if (currentAgencyId == 0) {
            builder.addHeaderColumn("代理名称", "agencyName");
        }
        if (po.getIsVest() == CommonConst.YES) {
            builder.addHeaderColumn("马甲名称", "vestName");
        }


        builder.addHeaderColumn("应还订单", "orderCount")
                .addHeaderColumn("提前还款", "earlyCount")
                .addHeaderColumn("正常还款", "normalCount")
                .addHeaderColumn("逾期还款", "overdueRepayCount")
                .addHeaderColumn("逾期中", "overduePayCount")
                .addHeaderColumn("坏账", "repayFailCount")

                .addHeaderColumn("应还金额", "repayAmount")
                .addHeaderColumn("实还金额", "realRepayAmount")
                .addHeaderColumn("放款金额/成本", "payAmount")

                .addHeaderColumn("逾期费", "exceedAmount")
                .addHeaderColumn("减免金额", "deductAmount")
                .addHeaderColumn("待还金额", "leftAmount")

                .addHeaderColumn("首逾率", "firstOverdueRate")
                .addHeaderColumn("逾期率", "overdueRate")

                .addHeaderColumn("回收率1天", "chaseOneRate")
                .addHeaderColumn("回收率3天", "chaseThreeRate")
                .addHeaderColumn("回收率7天", "chaseSevenRate")
                .addHeaderColumn("回收率15天", "chaseFifteenRate");


        int size = 500;
        PageRow page = new PageRow();
        page.setPageSize(size);
        page.initLimit();


        while (true) {
            List<RepayStatisticModel> list = repayStatisticDao.findStatisticRepaymentList(po, page);

            list.forEach(e -> {
                Map<String, Object> data = new HashMap<>();

                data.put("repayDate", e.getRepayDate().toString());

                if (currentAgencyId == 0) {
                    AgencyModel agency = comAgencyService.findAgencyById(e.getAgencyId());
                    if (agency != null) {
                        data.put("agencyName", ifnull(agency.getAgencyName()));
                    }
                }
                if (po.getIsVest() == CommonConst.YES) {
                    if (e.getVestId() != null) {
                        AppVestModel vest = comAgencyService.getVestInfoByVestId(e.getVestId());
                        if (vest != null) {
                            data.put("vestName", ifnull(vest.getName()));
                        }
                    }
                }
                data.put("orderCount", e.getOrderCount());
                data.put("earlyCount", e.getEarlyCount());
                data.put("normalCount", e.getNormalCount());
                data.put("overdueRepayCount", e.getOverdueRepayCount());
                data.put("overduePayCount", e.getOverduePayCount());
                data.put("repayFailCount", e.getRepayFailCount());

                data.put("repayAmount", FormatUtil.formatDouble2(e.getRepayAmount()));
                data.put("realRepayAmount", FormatUtil.formatDouble2(e.getRealRepayAmount()));
                data.put("payAmount", FormatUtil.formatDouble2(e.getPayAmount()));

                data.put("exceedAmount", FormatUtil.formatDouble2(e.getExceedAmount()));
                data.put("deductAmount", FormatUtil.formatDouble2(e.getDeductAmount()));
                data.put("leftAmount", FormatUtil.formatDouble2(e.getLeftAmount()));


                if (e.getOrderCount() > 0) {
                    Double orderCount = e.getOrderCount() / 100D;

                    data.put("firstOverdueRate", FormatUtil.formatDouble2(e.getFirstOverdueCount() / orderCount) + "%");
                    data.put("overdueRate", FormatUtil.formatDouble2(e.getOverduePayCount() / orderCount) + "%");

                } else {
                    data.put("firstOverdueRate", "0.00%");
                    data.put("overdueRate", "0.00%");
                }

                Double firstOverdueCount = e.getFirstOverdueCount() / 100D;
                if (firstOverdueCount > 0) {

                    data.put("chaseOneRate", FormatUtil.formatDouble2(e.getChaseOneCount() / firstOverdueCount) + "%");
                    data.put("chaseThreeRate", FormatUtil.formatDouble2(e.getChaseThreeCount() / firstOverdueCount) + "%");
                    data.put("chaseSevenRate", FormatUtil.formatDouble2(e.getChaseSevenCount() / firstOverdueCount) + "%");
                    data.put("chaseFifteenRate", FormatUtil.formatDouble2(e.getChaseFifteenCount() / firstOverdueCount) + "%");
                } else {
                    data.put("chaseOneRate", "0.00%");
                    data.put("chaseThreeRate", "0.00%");
                    data.put("chaseSevenRate", "0.00%");
                    data.put("chaseFifteenRate", "0.00%");

                }
                builder.appendRowData(data);
            });

            if (list.size() < size) {
                break;
            }
            page.setPage(page.getPage() + 1);
            page.initLimit();
        }
        try {
            excelNewHelper.write2Servlet(response, excelName, builder);
        } catch (IOException e) {
            throw new BusinessException(AdminRespCode.INNER_RESULT_ERROR);
        }
    }

    @Override
    public void statisticPaymentStatis(StatisticPayPO po, Integer currentAgencyId, HttpServletResponse response) {

        po.setStartDate(StringUtil.trimToNull(po.getStartDate()));
        po.setEndDate(StringUtil.trimToNull(po.getEndDate()));

        int count = payStatisticDao.findStatisticPaymentCount(po);

        if (count <= 0) {
            throw new BusinessException(AdminRespCode.INNER_JSON_ERROR);
        }

        String excelName = CommonConst.YES == po.getIsVest() ? "放款统计(马甲)" : "放款统计";

        ExcelWriteBuilder builder = excelNewHelper.createExcelWriteBuilder(excelName);
        builder.addHeaderColumn("放款日期", "payDate");

        if (currentAgencyId == 0) {
            builder.addHeaderColumn("代理名称", "agencyName");
        }
        if (po.getIsVest() == CommonConst.YES) {
            builder.addHeaderColumn("马甲名称", "vestName");
        }
        builder.addHeaderColumn("放款笔数", "payCount")
                .addHeaderColumn("放款金额(元)", "payAmount")
                .addHeaderColumn("首借人数", "firstLoanCount")
                .addHeaderColumn("首借金额(元)", "firstLoanAmount")
                .addHeaderColumn("复借人数", "againLoanCount")
                .addHeaderColumn("复借金额(元)", "againLoanAmount")
                .addHeaderColumn("综合费用(元)", "stampTaxAmount");

        int size = 500;
        PageRow page = new PageRow();
        page.setPageSize(size);

        page.initLimit();

        while (true) {
            List<StatisticPayVO> list = payStatisticDao.findStatisticPaymentList(po, page);

            list.forEach(e -> {
                Map<String, Object> data = new HashMap<>();

                data.put("payDate", e.getPayDate().toString());

                if (currentAgencyId == 0) {
                    AgencyModel agency = comAgencyService.findAgencyById(e.getAgencyId());
                    if (agency != null) {
                        data.put("agencyName", ifnull(agency.getAgencyName()));
                    }
                }
                if (po.getIsVest() == CommonConst.YES) {
                    if (e.getVestId() != null) {
                        AppVestModel vest = comAgencyService.getVestInfoByVestId(e.getVestId());
                        if (vest != null) {
                            data.put("vestName", ifnull(vest.getName()));
                        }
                    }
                }
                data.put("payCount", e.getPayCount());
                data.put("payAmount", FormatUtil.formatDouble2(e.getPayAmount()));
                data.put("firstLoanCount", e.getFirstLoanCount());
                data.put("firstLoanAmount", FormatUtil.formatDouble2(e.getFirstLoanAmount()));
                data.put("againLoanCount", e.getAgainLoanCount());
                data.put("againLoanAmount", FormatUtil.formatDouble2(e.getAgainLoanAmount()));
                data.put("stampTaxAmount", FormatUtil.formatDouble2(e.getStampTaxAmount()));

                builder.appendRowData(data);
            });

            if (list.size() < size) {
                break;
            }
            page.setPage(page.getPage() + 1);
            page.initLimit();
        }
        try {
            excelNewHelper.write2Servlet(response, excelName, builder);
        } catch (IOException e) {
            throw new BusinessException(AdminRespCode.INNER_RESULT_ERROR);
        }

    }

    @Override
    public void statisticUserStatis(StatisticUserPO po, Integer agencyId, HttpServletResponse response) {
        po.setStartDate(StringUtil.trimToNull(po.getStartDate()));
        po.setEndDate(StringUtil.trimToNull(po.getEndDate()));
        int count = userStatisticDao.findStatisticUserCount(po);
        if (count <= 0) {
            throw new BusinessException(AdminRespCode.INNER_JSON_ERROR);
        }

        String excelName = CommonConst.YES == po.getIsVest() ? "用户注册统计(马甲)" : "用户注册统计";

        ExcelWriteBuilder builder = excelNewHelper.createExcelWriteBuilder(excelName);
        builder.addHeaderColumn("注册日期", "statisticDate");

        if (agencyId == 0) {
            builder.addHeaderColumn("代理名称", "agencyName");
        }
        if (po.getIsVest() == CommonConst.YES) {
            builder.addHeaderColumn("马甲名称", "vestName");
        }
        builder.addHeaderColumn("注册人数", "registerCount")
                .addHeaderColumn("实名认证", "idcardCount")
                .addHeaderColumn("活体认证", "faceCount")
                .addHeaderColumn("个人信息认证", "infoCount")
                .addHeaderColumn("运营商认证", "phoneCount")
                .addHeaderColumn("淘宝认证", "aliCount")
                .addHeaderColumn("绑卡人数", "bankCount")
                .addHeaderColumn("提单数量", "newCount");


        int size = 500;
        PageRow page = new PageRow();
        page.setPageSize(size);

        page.initLimit();

        while (true) {
            List<StatisticUserVO> list = userStatisticDao.findStatisticUserList(po, page);
            list.forEach(e -> {
                Map<String, Object> data = new HashMap<>();


                data.put("statisticDate", e.getStatisticDate().toString());

                if (agencyId == 0) {
                    AgencyModel agency = comAgencyService.findAgencyById(e.getAgencyId());
                    if (agency != null) {
                        data.put("agencyName", ifnull(agency.getAgencyName()));
                    }
                }
                if (po.getIsVest() == CommonConst.YES) {
                    if (e.getVestId() != null) {
                        AppVestModel vest = comAgencyService.getVestInfoByVestId(e.getVestId());
                        if (vest != null) {
                            data.put("vestName", ifnull(vest.getName()));
                        }
                    }
                }
                data.put("registerCount", e.getRegisterCount());
                data.put("idcardCount", e.getIdcardCount());
                data.put("faceCount", e.getFaceCount());
                data.put("infoCount", e.getInfoCount());
                data.put("phoneCount", e.getPhoneCount());
                data.put("aliCount", e.getAliCount());
                data.put("bankCount", e.getBankCount());
                data.put("newCount", e.getNewCount());

                builder.appendRowData(data);

            });

            if (list.size() < size) {
                break;
            }
            page.setPage(page.getPage() + 1);
            page.initLimit();
        }
        try {
            excelNewHelper.write2Servlet(response, excelName, builder);
        } catch (IOException e) {
            throw new BusinessException(AdminRespCode.INNER_RESULT_ERROR);
        }

    }

    @Override
    public void statisticActionUserStatis(StatisticUserPO po, Integer agencyId, HttpServletResponse response) {
        po.setStartDate(StringUtil.trimToNull(po.getStartDate()));
        po.setEndDate(StringUtil.trimToNull(po.getEndDate()));
        int count = userActionStatisticDao.findStatisticUserCount(po);
        if (count <= 0) {
            throw new BusinessException(AdminRespCode.INNER_JSON_ERROR);
        }

        String excelName = CommonConst.YES == po.getIsVest() ? "用户行为统计(马甲)" : "用户行为统计";

        ExcelWriteBuilder builder = excelNewHelper.createExcelWriteBuilder(excelName);
        builder.addHeaderColumn("统计日期", "statisticDate");

        if (agencyId == 0) {
            builder.addHeaderColumn("代理名称", "agencyName");
        }
        if (po.getIsVest() == CommonConst.YES) {
            builder.addHeaderColumn("马甲名称", "vestName");
        }
        builder.addHeaderColumn("注册人数", "registerCount")
                .addHeaderColumn("实名认证", "idcardCount")
                .addHeaderColumn("活体认证", "faceCount")
                .addHeaderColumn("个人信息认证", "infoCount")
                .addHeaderColumn("运营商认证", "phoneCount")
                .addHeaderColumn("淘宝认证", "aliCount")
                .addHeaderColumn("绑卡人数", "bankCount")
                .addHeaderColumn("新客提单", "newCount")
                .addHeaderColumn("老客提单", "oldCount")
                .addHeaderColumn("总提单", "orderCount");


        int size = 500;
        PageRow page = new PageRow();
        page.setPageSize(size);

        page.initLimit();

        while (true) {
            List<StatisticUserVO> list = userActionStatisticDao.findStatisticUserList(po, page);
            list.forEach(e -> {
                Map<String, Object> data = new HashMap<>();


                data.put("statisticDate", e.getStatisticDate().toString());

                if (agencyId == 0) {
                    AgencyModel agency = comAgencyService.findAgencyById(e.getAgencyId());
                    if (agency != null) {
                        data.put("agencyName", ifnull(agency.getAgencyName()));
                    }
                }
                if (po.getIsVest() == CommonConst.YES) {
                    if (e.getVestId() != null) {
                        AppVestModel vest = comAgencyService.getVestInfoByVestId(e.getVestId());
                        if (vest != null) {
                            data.put("vestName", ifnull(vest.getName()));
                        }
                    }
                }
                data.put("registerCount", e.getRegisterCount());
                data.put("idcardCount", e.getIdcardCount());
                data.put("faceCount", e.getFaceCount());
                data.put("infoCount", e.getInfoCount());
                data.put("phoneCount", e.getPhoneCount());
                data.put("aliCount", e.getAliCount());
                data.put("bankCount", e.getBankCount());
                data.put("newCount", e.getNewCount());
                data.put("oldCount", e.getOldCount());
                data.put("orderCount", e.getOrderCount());

                builder.appendRowData(data);

            });

            if (list.size() < size) {
                break;
            }
            page.setPage(page.getPage() + 1);
            page.initLimit();
        }
        try {
            excelNewHelper.write2Servlet(response, excelName, builder);
        } catch (IOException e) {
            throw new BusinessException(AdminRespCode.INNER_RESULT_ERROR);
        }
    }

    private String ifnull(String name) {
        if (StringUtils.isEmpty(name)) return "--";
        return name;
    }
}
