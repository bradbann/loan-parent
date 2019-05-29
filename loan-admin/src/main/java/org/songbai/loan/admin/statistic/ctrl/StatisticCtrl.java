package org.songbai.loan.admin.statistic.ctrl;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.statistic.model.po.ChannelStatisPo;
import org.songbai.loan.admin.statistic.model.po.ReviewStatisPo;
import org.songbai.loan.admin.statistic.model.po.StatisticPayPO;
import org.songbai.loan.admin.statistic.model.po.StatisticUserPO;
import org.songbai.loan.admin.statistic.model.vo.StatisHomeVO;
import org.songbai.loan.admin.statistic.model.vo.StatisticPayVO;
import org.songbai.loan.admin.statistic.model.vo.StatisticRepayVO;
import org.songbai.loan.admin.statistic.model.vo.StatisticUserVO;
import org.songbai.loan.admin.statistic.service.StatisticExportService;
import org.songbai.loan.admin.statistic.service.StatisticService;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.constant.CommonConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: qmw
 * Date: 2018/11/21 4:27 PM
 */
@RestController
@RequestMapping("/statistic")
public class StatisticCtrl {
    @Autowired
    private AdminUserHelper adminUserHelper;
    @Autowired
    private StatisticService statisticService;
    @Autowired
    StatisticExportService statisticExportService;


    /**
     * 首页统计
     */
    @GetMapping(value = "/home")
    @ResponseBody
    public Response statisticHome(Integer agencyId, String date, HttpServletRequest request) {
        Assert.hasLength(date, "date不能为空");
        Integer currentAgencyId = adminUserHelper.getAgencyId(request);
        if (currentAgencyId != 0) {
            agencyId = currentAgencyId;
        }else {
            agencyId = null;
        }
        StatisHomeVO vo = statisticService.statisticHome(agencyId, date);
        return Response.success(vo);
    }

    /**
     * 还款统计
     */
    @GetMapping(value = "/repay")
    @ResponseBody
    public Response statisticPay(StatisticPayPO po, HttpServletRequest request, PageRow pageRow) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        pageRow.initLimit();
        Page<StatisticRepayVO> p = statisticService.statisticRepayment(po, pageRow);
        return Response.success(p);
    }

    /**
     * 放款统计
     */
    @GetMapping(value = "/pay")
    @ResponseBody
    public Response statisticRepay(StatisticPayPO po, HttpServletRequest request, PageRow pageRow) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        pageRow.initLimit();


        Page<StatisticPayVO> p = statisticService.statisticPayment(po, pageRow);
        return Response.success(p);
    }

    /**
     * 用户注册统计
     */
    @GetMapping(value = "/user")
    @ResponseBody
    public Response statisticUser(StatisticUserPO po, HttpServletRequest request, PageRow pageRow) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        pageRow.initLimit();
        Page<StatisticUserVO> p = statisticService.statisticUser(po, pageRow);
        return Response.success(p);
    }

    /**
     * 用户行为统计
     */
    @GetMapping(value = "/actionuser")
    @ResponseBody
    public Response actionUser(StatisticUserPO po, HttpServletRequest request, PageRow pageRow) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        pageRow.initLimit();
        Page<StatisticUserVO> p = statisticService.statisticActionUser(po, pageRow);
        return Response.success(p);
    }


    /**
     * 信审统计 总订单量
     */
    @GetMapping(value = "/reviewTotalStatis")
    public Response reviewTotalStatis(Integer agencyId, HttpServletRequest request) {

        Integer currentAgencyId = adminUserHelper.getAgencyId(request);
        if (currentAgencyId != 0) {
            agencyId = currentAgencyId;
        }

        return Response.success(statisticService.reviewTotalStatis(agencyId));
    }

    /**
     * 信审统计--信审列表
     */
    @GetMapping(value = "/getAgencyReviewPage")
    public Response getAgencyReviewPage(ReviewStatisPo po, HttpServletRequest request) {
        checkReviewBaseParam(po);

        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        po.setIsChannelOrder(CommonConst.NO);
        po.setIsProduct(CommonConst.NO);

        po.initLimit();
        return Response.success(statisticService.getAgencyReviewPage(po));
    }

    @GetMapping(value = "/exportReviewStatis")
    public Response exportReviewStatis(ReviewStatisPo po, HttpServletRequest request, HttpServletResponse response) {
        checkReviewBaseParam(po);

        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }

        po.setIsChannelOrder(CommonConst.NO);
        po.setIsProduct(CommonConst.NO);

        statisticExportService.exportReviewStatis(po, response);
        return Response.success();
    }

    /**
     * 信审人员统计--列表
     */
    @GetMapping(value = "/getActorReviewStatisPage")
    public Response getActorReviewStatisPage(ReviewStatisPo po, HttpServletRequest request) {
        checkReviewBaseParam(po);

        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        po.initLimit();

        return Response.success(statisticService.getActorReviewStatisPage(po));
    }

    /**
     * 信审人员统计--导出
     */
    @GetMapping(value = "/exportActorReviewStatis")
    public Response exportActorReviewStatis(ReviewStatisPo po, HttpServletRequest request, HttpServletResponse response) {
        checkReviewBaseParam(po);

        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        statisticExportService.exportActorReviewStatis(po, response);
        return Response.success();
    }

    /**
     * 渠道统计--列表
     */
    @GetMapping(value = "/getChannelStatisPage")
    public Response getChannelStatisPage(ChannelStatisPo po, HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        po.initLimit();

        return Response.success(statisticService.getChannelStatisPage(po));
    }

    /**
     * 渠道统计导出
     */
    @GetMapping(value = "/exportChannelStatis")
    public Response exportChannelStatis(ChannelStatisPo po, HttpServletRequest request, HttpServletResponse response) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }

        statisticExportService.exportChannelStatis(po, response);
        return Response.success();
    }


    /**
     * 渠道订单统计
     */
    @GetMapping(value = "/findChannelOrderPage")
    public Response findChannelOrderPage(ReviewStatisPo po, HttpServletRequest request) {
        checkReviewBaseParam(po);

        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        po.initLimit();
        po.setIsChannelOrder(CommonConst.YES);
        po.setIsProduct(CommonConst.NO);

        return Response.success(statisticService.getAgencyReviewPage(po));
    }

    @GetMapping(value = "/exportChannelOrderStatis")
    public Response exportChannelOrderStatis(ReviewStatisPo po, HttpServletRequest request, HttpServletResponse response) {
        checkReviewBaseParam(po);

        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }

        po.setIsChannelOrder(CommonConst.YES);
        po.setIsProduct(CommonConst.NO);

        statisticExportService.exportReviewStatis(po, response);
        return Response.success();
    }

    @GetMapping(value = "/findProductStatisPage")
    public Response findProductStatisPage(ReviewStatisPo po, HttpServletRequest request) {
        checkReviewBaseParam(po);

        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        po.initLimit();
        po.setIsProduct(CommonConst.YES);
        po.setIsChannelOrder(CommonConst.NO);

        return Response.success(statisticService.getAgencyReviewPage(po));
    }

    @GetMapping(value = "/exportProductStatis")
    public Response exportProductStatis(ReviewStatisPo po, HttpServletRequest request, HttpServletResponse response) {
        checkReviewBaseParam(po);

        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }

        po.setIsProduct(CommonConst.YES);
        po.setIsChannelOrder(CommonConst.NO);

        statisticExportService.exportReviewStatis(po, response);
        return Response.success();
    }


    /**
     * 还款统计-导出
     */
    @GetMapping(value = "/exportRepay")
    @ResponseBody
    public Response exportRepay(StatisticPayPO po, HttpServletResponse response, HttpServletRequest request) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        statisticExportService.statisticRepaymentStatis(po, agencyId, response);
        return Response.success();
    }
    /**
     * 放款统计-导出
     */
    @GetMapping(value = "/exportPay")
    @ResponseBody
    public Response exportPay(StatisticPayPO po, HttpServletResponse response, HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        statisticExportService.statisticPaymentStatis(po, agencyId, response);
        return Response.success();
    }


    /**
     * 用户注册统计--导出
     */
    @GetMapping(value = "/exportUser")
    @ResponseBody
    public Response exportUser(StatisticUserPO po, HttpServletRequest request, HttpServletResponse response) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        statisticExportService.statisticUserStatis(po, agencyId, response);
        return Response.success();
    }

    /**
     * 用户行为统计--导出
     */
    @GetMapping(value = "/exportActionUser")
    @ResponseBody
    public Response exportActionUser(StatisticUserPO po, HttpServletRequest request, HttpServletResponse response) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) {
            po.setAgencyId(agencyId);
        }
        statisticExportService.statisticActionUserStatis(po, agencyId, response);
        return Response.success();
    }

    private void checkReviewBaseParam(ReviewStatisPo po) {
        if (po.getIsTotal() != null && po.getIsTotal() == CommonConst.YES) {
            Assert.notNull(po.getStartCalcDate(), "开始日期不能为空");
            Assert.notNull(po.getEndCalcDate(), "结束日期不能为空");
        }
    }

}
