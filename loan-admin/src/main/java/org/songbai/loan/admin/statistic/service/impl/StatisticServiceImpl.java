package org.songbai.loan.admin.statistic.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.BeanUtil;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.channel.dao.ChannelDao;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.admin.order.dao.OrderOptDao;
import org.songbai.loan.admin.order.dao.PaymentFlowDao;
import org.songbai.loan.admin.statistic.dao.*;
import org.songbai.loan.admin.statistic.model.po.ChannelStatisPo;
import org.songbai.loan.admin.statistic.model.po.ReviewStatisPo;
import org.songbai.loan.admin.statistic.model.po.StatisticPayPO;
import org.songbai.loan.admin.statistic.model.po.StatisticUserPO;
import org.songbai.loan.admin.statistic.model.vo.*;
import org.songbai.loan.admin.statistic.service.StatisticService;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.channel.AgencyChannelModel;
import org.songbai.loan.model.loan.ProductGroupModel;
import org.songbai.loan.model.loan.ProductModel;
import org.songbai.loan.model.statistic.RepayStatisticModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.service.user.service.ComProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: qmw
 * Date: 2018/11/21 4:35 PM
 */
@Service
public class StatisticServiceImpl implements StatisticService {
    @Autowired
    ReviewOrderDao reviewOrderDao;
    @Autowired
    OrderDao orderDao;
    @Autowired
    OrderOptDao orderOptDao;
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
    private PaymentFlowDao paymentFlowDao;
    @Autowired
    ComProductService comProductService;
    @Autowired
    private UserActionStatisticDao userActionStatisticDao;

    @Override
    public StatisHomeVO statisticHome(Integer agencyId, String date) {

        StatisHomeVO vo = userActionStatisticDao.findHomeUserStatistic(agencyId, date);

        if (vo == null) {
            vo = new StatisHomeVO();
        }

        StatisHomeVO pvo = paymentFlowDao.findStatisticOrderByAgencyIdAndDate(agencyId, date);
        if (pvo != null) {
            vo.setPayCount(pvo.getPayCount());
            vo.setFirstLoanCount(pvo.getFirstLoanCount());
            vo.setPayAmount(FormatUtil.formatDouble2(pvo.getPayAmount()));
        }

        RepayStatisticModel repayModel = repayStatisticDao.findStatisticRepaymenByAngecyIdAndDate(agencyId, date);
        if (repayModel != null) {
            vo.setOverdueCount(repayModel.getOverduePayCount());
        }

        vo.setDate(date);

        return vo;
    }

    @Override
    public Ret reviewTotalStatis(Integer agencyId) {
//        Map<String, Object> orderOptMap = orderOptDao.queryAgencyGroupReviewOrder(agencyId);
//        Map<String, Object> orderMap = orderDao.queryAgencyGroupCount(agencyId);
        Map<String, Object> totalMap = reviewOrderDao.queryOrderTotalSum(agencyId);
        Ret ret = Ret.create();
        ret.put(totalMap);

        return ret;
    }

    @Override
    public Page<OrderReviewVo> getAgencyReviewPage(ReviewStatisPo po) {
        Integer count = reviewOrderDao.getAgencyReviewCount(po);
        if (count == 0) {
            return new Page<>(po.getPage(), po.getPageSize(), count, new ArrayList<>());
        }
        List<OrderReviewVo> list = reviewOrderDao.findReviewGroupList(po);

        if (po.getIsChannelOrder() == CommonConst.NO && po.getIsProduct() == CommonConst.NO
                && po.getIsVest() == CommonConst.NO) {
            //增加一条汇总数据
            list.add(this.calcReviewTotal(list));
        }

        list = handleReviewList(list, po);


        return new Page<>(po.getPage(), po.getPageSize(), count, list);
    }

    List<OrderReviewVo> handleReviewList(List<OrderReviewVo> list, ReviewStatisPo po) {
        list.forEach(e -> {
            if (po.getIsTotal() == CommonConst.YES
                    && (po.getIsChannelOrder() == CommonConst.YES || po.getIsProduct() == CommonConst.YES
                    || po.getIsVest() == CommonConst.YES)) {
                e.setStatisDate(po.getStartCalcDate() + "到" + po.getEndCalcDate());
            } else
                e.setStatisDate(e.getCalcDate() == null ? "--" : e.getCalcDate().toString());
            e.calcRate(e, po.getGuest());
            if (e.getAgencyId() != null) {
                AgencyModel agencyModel = comAgencyService.findAgencyById(e.getAgencyId());
                if (agencyModel != null) e.setAgencyName(agencyModel.getAgencyName());
            }

            if (e.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(e.getVestId());
                if (vestModel != null) e.setVestName(vestModel.getName());
            }

            if (e.getProductId() != null) {
                ProductModel productModel = comProductService.getProductInfoById(e.getProductId());
                if (productModel != null) e.setProductName(productModel.getName());
            }

            if (e.getProductGroupId() != null) {
                ProductGroupModel groupModel = comProductService.getProductGroupByGroupId(e.getProductGroupId());
                if (groupModel != null) e.setProductGroupName(groupModel.getName());
            }
            if (StringUtil.isNotEmpty(e.getChannelCode())) {
                AgencyChannelModel channel = comAgencyService.findChannelNameByAgencyIdAndChannelCode(e.getAgencyId(), e.getChannelCode());
                if (channel != null&&StringUtil.isNotEmpty(channel.getChannelName())) {
                    e.setChannelCode(channel.getChannelName());
                }
            }
        });

        return list;
    }

    private OrderReviewVo calcReviewTotal(List<OrderReviewVo> list) {
        if (CollectionUtils.isEmpty(list)) return new OrderReviewVo();
        OrderReviewVo model = new OrderReviewVo();
        for (OrderReviewVo vo : list) {
            model.setOrderNewCount(ifnull(model.getOrderNewCount()) + vo.getOrderNewCount());
            model.setOrderOldCount(ifnull(model.getOrderOldCount()) + vo.getOrderOldCount());
            model.setOrderWaitCount(ifnull(model.getOrderWaitCount()) + vo.getOrderWaitCount());
            model.setReviewNewSuccCount(ifnull(model.getReviewNewSuccCount()) + vo.getReviewNewSuccCount());
            model.setReviewOldSuccCount(ifnull(model.getReviewOldSuccCount()) + vo.getReviewOldSuccCount());
            model.setReviewNewFailCount(ifnull(model.getReviewNewFailCount()) + vo.getReviewNewFailCount());
            model.setReviewOldFailCount(ifnull(model.getReviewOldFailCount()) + vo.getReviewOldFailCount());
            model.setExpireNewCount(ifnull(model.getExpireNewCount()) + vo.getExpireNewCount());
            model.setExpireOldCount(ifnull(model.getExpireOldCount()) + vo.getExpireOldCount());
            model.setMachineNewSuccCount(ifnull(model.getMachineNewSuccCount()) + vo.getMachineNewSuccCount());
            model.setMachineOldSuccCount(ifnull(model.getMachineOldSuccCount()) + vo.getMachineOldSuccCount());
            model.setMachineNewFailCount(ifnull(model.getMachineNewFailCount()) + vo.getMachineNewFailCount());
            model.setMachineOldFailCount(ifnull(model.getMachineOldFailCount()) + vo.getMachineOldFailCount());
            model.setMachineToTransNewCount(ifnull(model.getMachineToTransNewCount()) + vo.getMachineToTransNewCount());
            model.setMachineToTransOldCount(ifnull(model.getMachineToTransOldCount()) + vo.getMachineToTransOldCount());
            model.setFirstOverdueNewCount(ifnull(model.getFirstOverdueNewCount()) + vo.getFirstOverdueNewCount());
            model.setFirstOverdueOldCount(ifnull(model.getFirstOverdueOldCount()) + vo.getFirstOverdueOldCount());
            model.setInOverdueNewCount(ifnull(model.getInOverdueNewCount()) + vo.getInOverdueNewCount());
            model.setInOverdueOldCount(ifnull(model.getInOverdueOldCount()) + vo.getInOverdueOldCount());
        }
        return model;
    }

    private Integer ifnull(Integer count) {
        return count == null ? 0 : count;
    }

    @Override
    public Page<ActorReviewVo> getActorReviewStatisPage(ReviewStatisPo po) {
        Integer count = reviewOrderDao.getActorStatisCount(po);
        if (count == 0) {
            return new Page<>(po.getPage(), po.getPageSize(), count, new ArrayList<>());
        }
        List<ActorReviewVo> result = reviewOrderDao.findActorStatisGroupList(po);

        result = handleActorReviewList(result, po);

        return new Page<>(po.getPage(), po.getPageSize(), count, result);
    }

    List<ActorReviewVo> handleActorReviewList(List<ActorReviewVo> list, ReviewStatisPo po) {
        List<ActorReviewVo> result = new ArrayList<>();
        list.forEach(e -> {
            ActorReviewVo model = e.calcRate(e, po.getGuest());
            if (po.getIsTotal() == CommonConst.YES) {
                model.setStatisDate(po.getStartCalcDate() + "到" + po.getEndCalcDate());
            } else
                model.setStatisDate(e.getCalcDate() == null ? "--" : e.getCalcDate().toString());
            if (model.getAgencyId() != null) {
                AgencyModel agencyModel = comAgencyService.findAgencyById(model.getAgencyId());
                if (agencyModel != null) model.setAgencyName(agencyModel.getAgencyName());
            }
            if (model.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(model.getVestId());
                if (vestModel != null) model.setVestName(vestModel.getName());
            }
            result.add(model);
        });
        return result;
    }

    @Override
    public Page<UserStatisVo> getChannelStatisPage(ChannelStatisPo po) {
        Integer count = statisticDao.queryUserStatisCount(po);
        if (count == 0) return new Page<>(po.getPage(), po.getPageSize(), count, new ArrayList<>());
        List<UserStatisVo> list = statisticDao.findUserStatisList(po);
        return new Page<>(po.getPage(), po.getPageSize(), count, handleChannelList(list, po));
    }

    List<UserStatisVo> handleChannelList(List<UserStatisVo> list, ChannelStatisPo po) {

        list.forEach(e -> {
            if (po.getIsTotal() == CommonConst.YES) {
                e.setStatisDate(po.getStartCalcDate() + "到" + po.getEndCalcDate());
            } else
                e.setStatisDate(e.getStatisticDate() == null ? "--" : e.getStatisticDate().toString());
            e.calcRate(e);
            if (e.getAgencyId() != null) {
                AgencyModel agencyModel = comAgencyService.findAgencyById(e.getAgencyId());
                if (agencyModel != null) e.setAgencyName(agencyModel.getAgencyName());
            }

            if (e.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(e.getVestId());
                if (vestModel != null) e.setVestName(vestModel.getName());
            }
            if (StringUtil.isNotEmpty(e.getChannelCode())) {

                AgencyChannelModel channel = comAgencyService.findChannelNameByAgencyIdAndChannelCode(e.getAgencyId(), e.getChannelCode());
                if (channel != null&&StringUtil.isNotEmpty(channel.getChannelName())) {
                    e.setChannelCode(channel.getChannelName());
                }
            }
        });
        return list;
    }

    @Override
    public Page<StatisticRepayVO> statisticRepayment(StatisticPayPO po, PageRow pageRow) {
        po.setStartDate(StringUtil.trimToNull(po.getStartDate()));
        po.setEndDate(StringUtil.trimToNull(po.getEndDate()));

        int count = repayStatisticDao.findStatisticRepaymentCount(po);
        if (count <= 0) {
            return new Page<>(pageRow.getPage(), pageRow.getPageSize(), 0, new ArrayList<>());
        }
        List<RepayStatisticModel> list = repayStatisticDao.findStatisticRepaymentList(po, pageRow);

        List<StatisticRepayVO> collect = list.stream().map(model -> {
            StatisticRepayVO vo = new StatisticRepayVO();
            BeanUtil.copyNotNullProperties(model, vo);
            vo.setRepayAmount(FormatUtil.formatDouble2(model.getRepayAmount()));
            vo.setRealRepayAmount(FormatUtil.formatDouble2(model.getRealRepayAmount()));
            vo.setPayAmount(FormatUtil.formatDouble2(model.getPayAmount()));
            vo.setExceedAmount(FormatUtil.formatDouble2(model.getExceedAmount()));
            vo.setDeductAmount(FormatUtil.formatDouble2(model.getDeductAmount()));
            vo.setLeftAmount(FormatUtil.formatDouble2(model.getLeftAmount()));
            if (model.getOrderCount() > 0) {
                Double orderCount = model.getOrderCount() / 100D;
                vo.setFirstOverdueRate(FormatUtil.formatDouble2((model.getOverdueRepayCount() + model.getOverduePayCount() + model.getRepayFailCount()) / orderCount));
                vo.setOverdueRate(FormatUtil.formatDouble2((model.getOverduePayCount() + model.getRepayFailCount()) / orderCount));

            }
            if (model.getFirstOverdueCount() > 0) {
                Double firstOverdueCount = model.getFirstOverdueCount() / 100D;
                vo.setChaseOneRate(FormatUtil.formatDouble2(model.getChaseOneCount() / firstOverdueCount));
                vo.setChaseThreeRate(FormatUtil.formatDouble2(model.getChaseThreeCount() / firstOverdueCount));
                vo.setChaseSevenRate(FormatUtil.formatDouble2(model.getChaseSevenCount() / firstOverdueCount));
                vo.setChaseFifteenRate(FormatUtil.formatDouble2(model.getChaseFifteenCount() / firstOverdueCount));
            }

            if (po.getIsVest() == CommonConst.YES) {
                if (vo.getVestId() != null) {
                    AppVestModel vestModel = comAgencyService.getVestInfoByVestId(vo.getVestId());
                    if (vestModel != null) {
                        vo.setVestName(vestModel.getName());
                    }
                }
            }

            AgencyModel agency = comAgencyService.findAgencyById(vo.getAgencyId());
            if (agency != null) {
                vo.setAgencyName(agency.getAgencyName());
            }
            vo.setAgencyId(null);

            return vo;

        }).collect(Collectors.toList());

        return new Page<>(pageRow.getPage(), pageRow.getPageSize(), count, collect);
    }

    @Override
    public Page<StatisticUserVO> statisticUser(StatisticUserPO po, PageRow pageRow) {
        po.setStartDate(StringUtil.trimToNull(po.getStartDate()));
        po.setEndDate(StringUtil.trimToNull(po.getEndDate()));
        int count = userStatisticDao.findStatisticUserCount(po);
        if (count <= 0) {
            return new Page<>(pageRow.getPage(), pageRow.getPageSize(), 0, new ArrayList<>());
        }
        List<StatisticUserVO> list = userStatisticDao.findStatisticUserList(po, pageRow);
        for (StatisticUserVO vo : list) {
            if (po.getIsVest() == CommonConst.YES) {
                if (vo.getVestId() != null) {
                    AppVestModel vestModel = comAgencyService.getVestInfoByVestId(vo.getVestId());
                    if (vestModel != null) {
                        vo.setVestName(vestModel.getName());
                    }
                }
            }
            AgencyModel agency = comAgencyService.findAgencyById(vo.getAgencyId());
            if (agency != null) {
                vo.setAgencyName(agency.getAgencyName());
            }
            vo.setAgencyId(null);
        }

        return new Page<>(pageRow.getPage(), pageRow.getPageSize(), count, list);
    }

    @Override
    public Page<StatisticUserVO> statisticActionUser(StatisticUserPO po, PageRow pageRow) {
        po.setStartDate(StringUtil.trimToNull(po.getStartDate()));
        po.setEndDate(StringUtil.trimToNull(po.getEndDate()));
        int count = userActionStatisticDao.findStatisticUserCount(po);
        if (count <= 0) {
            return new Page<>(pageRow.getPage(), pageRow.getPageSize(), 0, new ArrayList<>());
        }
        List<StatisticUserVO> list = userActionStatisticDao.findStatisticUserList(po, pageRow);
        for (StatisticUserVO vo : list) {
            AgencyModel agency = comAgencyService.findAgencyById(vo.getAgencyId());
            if (agency != null) {
                vo.setAgencyName(agency.getAgencyName());
            }
            if (po.getIsVest() == CommonConst.YES) {
                if (vo.getVestId() != null) {
                    AppVestModel vestModel = comAgencyService.getVestInfoByVestId(vo.getVestId());
                    if (vestModel != null) {
                        vo.setVestName(vestModel.getName());
                    }
                }
            }

            vo.setAgencyId(null);
        }

        return new Page<>(pageRow.getPage(), pageRow.getPageSize(), count, list);
    }

    @Override
    public Page<StatisticPayVO> statisticPayment(StatisticPayPO po, PageRow pageRow) {
        po.setStartDate(StringUtil.trimToNull(po.getStartDate()));
        po.setEndDate(StringUtil.trimToNull(po.getEndDate()));

        int count = payStatisticDao.findStatisticPaymentCount(po);
        if (count <= 0) {
            return new Page<>(pageRow.getPage(), pageRow.getPageSize(), 0, new ArrayList<>());
        }

        List<StatisticPayVO> list = payStatisticDao.findStatisticPaymentList(po, pageRow);
        for (StatisticPayVO vo : list) {
            vo.setLoanAmount(FormatUtil.formatDouble2(vo.getLoanAmount()));
            vo.setPayAmount(FormatUtil.formatDouble2(vo.getPayAmount()));
            vo.setFirstLoanAmount(FormatUtil.formatDouble2(vo.getFirstLoanAmount()));
            vo.setFirstPayAmount(FormatUtil.formatDouble2(vo.getFirstPayAmount()));
            vo.setAgainLoanAmount(FormatUtil.formatDouble2(vo.getAgainLoanAmount()));
            vo.setAgainPayAmount(FormatUtil.formatDouble2(vo.getAgainPayAmount()));
            vo.setStampTaxAmount(FormatUtil.formatDouble2(vo.getStampTaxAmount()));
            if (po.getIsVest() == CommonConst.YES) {
                if (vo.getVestId() != null) {
                    AppVestModel vestModel = comAgencyService.getVestInfoByVestId(vo.getVestId());
                    if (vestModel != null) {
                        vo.setVestName(vestModel.getName());
                    }
                }
            }
            AgencyModel agency = comAgencyService.findAgencyById(vo.getAgencyId());
            if (agency != null) {
                vo.setAgencyName(agency.getAgencyName());
            }
            vo.setAgencyId(null);
        }

        return new Page<>(pageRow.getPage(), pageRow.getPageSize(), count, list);
    }


}
