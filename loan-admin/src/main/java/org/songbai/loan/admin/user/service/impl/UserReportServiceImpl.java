package org.songbai.loan.admin.user.service.impl;

import org.apache.commons.collections.MapUtils;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.BeanUtil;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.user.dao.UserInfoDao;
import org.songbai.loan.admin.user.dao.UserReportDao;
import org.songbai.loan.admin.user.model.AddressVO;
import org.songbai.loan.admin.user.model.TradeVO;
import org.songbai.loan.admin.user.model.UserReportVO;
import org.songbai.loan.admin.user.mongo.UserConcatRepository;
import org.songbai.loan.admin.user.service.UserReportService;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.constant.risk.VariableConst;
import org.songbai.loan.model.user.UserContactModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.risk.moxie.carrier.model.ReportDataModel;
import org.songbai.loan.risk.moxie.carrier.mongo.ReportDataRepository;
import org.songbai.loan.risk.moxie.magic.model.MagicReportModel;
import org.songbai.loan.risk.moxie.magic.mongo.MagicReportRepository;
import org.songbai.loan.risk.moxie.taobao.model.DeliverAddressModel;
import org.songbai.loan.risk.moxie.taobao.model.SubOrderModel;
import org.songbai.loan.risk.moxie.taobao.model.TaobaoReportModel;
import org.songbai.loan.risk.moxie.taobao.model.TradeDetailModel;
import org.songbai.loan.risk.moxie.taobao.mongo.DeliverAddressRepository;
import org.songbai.loan.risk.moxie.taobao.mongo.TaobaoReportRepository;
import org.songbai.loan.risk.moxie.taobao.mongo.TaobaoSuborderRepository;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class UserReportServiceImpl implements UserReportService {

    @Autowired
    ComUserService userService;

    @Autowired
    UserReportDao userReportDao;

    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    TaobaoReportRepository taobaoReportRepository;
    @Autowired
    ReportDataRepository reportDataRepository;
    @Autowired
    MagicReportRepository magicReportRepository;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private DeliverAddressRepository deliverAddressRepository;
    @Autowired
    private UserInfoDao userInfoDao;
    @Autowired
    private UserConcatRepository userConcatRepository;
    @Autowired
    private TaobaoSuborderRepository suborderRepository;

    @Override
    public UserReportVO getReport(String thirdId, Integer agencyId) {
        UserModel userModel = comUserService.selectUserModelByThridId(thirdId);
        if (userModel == null) {
            return null;
        }

        if (agencyId != null) {
            if (!userModel.getAgencyId().equals(agencyId)) {
                return null;
            }
        }

        UserReportVO reportVO = new UserReportVO();


        getMoxieReport(userModel, reportVO);

        ReportDataModel reportDataModel = getCarrierReport(userModel);

        if (reportDataModel != null) {
            reportVO.setCarrierReportMessage(reportDataModel.getMessage());
        }

        TaobaoReportModel taobaoReport = getTaobaoReport(userModel);

        if (taobaoReport != null) {
            reportVO.setTaobaoReportMessage(taobaoReport.getMessage());
        }


        return reportVO;
    }


    @Override
    public List<UserContactModel> getUserContact(String thirdId, Integer agencyId) {

        UserModel userModel = comUserService.selectUserModelByThridId(thirdId);
        if (userModel == null) {
            return null;
        }


        if (agencyId != null) {
            if (!userModel.getAgencyId().equals(agencyId)) {
                return null;
            }
        }

        return mongoTemplate.find(Query.query(Criteria.where("userId").is(userModel.getId())), UserContactModel.class);
    }


    @Override
    public ReportDataModel getCarrierReport(String thirdId, Integer agencyId) {

        UserModel userModel = comUserService.selectUserModelByThridId(thirdId);
        if (userModel == null) {
            return null;
        }


        if (agencyId != null) {
            if (!userModel.getAgencyId().equals(agencyId)) {
                return null;
            }
        }

        return getCarrierReport(userModel);
    }


    @Override
    public TaobaoReportModel getTaobaoReport(String thirdId, Integer agencyId) {

        UserModel userModel = comUserService.selectUserModelByThridId(thirdId);
        if (userModel == null) {
            return null;
        }


        if (agencyId != null) {
            if (!userModel.getAgencyId().equals(agencyId)) {
                return null;
            }
        }

        return getTaobaoReport(userModel);
    }

    @Override
    public List<AddressVO> getTaobaoAddr(String thirdId, Integer agencyId) {
        UserModel userModel = comUserService.selectUserModelByThridId(thirdId);
        if (userModel == null) {
            return null;
        }

        if (agencyId != null) {
            if (!userModel.getAgencyId().equals(agencyId)) {
                return null;
            }
        }

        List<UserContactModel> contacts = userConcatRepository.findByUserId(userModel.getId());

        Map<String, String> map = contacts.stream().collect(Collectors.toMap(UserContactModel::getPhone, UserContactModel::getName, (k1, k2) -> k1));
        map.put(userModel.getPhone(), "本人");

        UserInfoModel userInfoModel = userInfoDao.selectById(userModel.getId());
        List<DeliverAddressModel> address = deliverAddressRepository.findByUserId(thirdId);
        return address.stream().map(a -> {
            AddressVO vo = new AddressVO();
            BeanUtil.copyNotNullProperties(a, vo);
            if (userInfoModel != null) {
                if (StringUtil.isNotEmpty(userInfoModel.getFirstPhone())) {
                    if (a.getPhoneNumber().equals(userInfoModel.getFirstPhone())) {
                        vo.setKinsfolkName(userInfoModel.getFirstContact());
                    }
                } else if (StringUtil.isNotEmpty(userInfoModel.getOtherPhone())) {
                    if (a.getPhoneNumber().equals(userInfoModel.getOtherPhone())) {
                        vo.setKinsfolkName(userInfoModel.getOtherContact());
                    }
                }
            }
            String name = map.get(a.getPhoneNumber());
            if (StringUtil.isNotEmpty(name)) {
                vo.setConcatName(name);
            }
            return vo;
        }).collect(Collectors.toList());

    }

    @Override
    public   List<TradeVO> getTaobaoTrade(String thirdId, Integer agencyId) {
        UserModel userModel = comUserService.selectUserModelByThridId(thirdId);
        if (userModel == null) {
            return null;
        }
        if (agencyId != null) {
            if (!userModel.getAgencyId().equals(agencyId)) {
                return null;
            }
        }

        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(thirdId));

        query.limit(100);// 从skip开始,取多少条记录
        query.with(new Sort(Sort.Direction.DESC, "tradeCreateTime"));
        List<TradeDetailModel> models = mongoTemplate.find(query, TradeDetailModel.class);
        return models.stream().map(d -> {
            TradeVO vo = new TradeVO();
            BeanUtil.copyNotNullProperties(d, vo);

            vo.setTradeFee(FormatUtil.formatDouble2(vo.getActualFee()/100D));

            vo.setActualFee(null);

            List<SubOrderModel> subs = suborderRepository.findByTradeId(vo.getTradeId());
            if (!subs.isEmpty()) {
                SubOrderModel subOrderModel = subs.get(0);
                vo.setCnameLevel1(subOrderModel.getCnameLevel1());
                vo.setItemName(subOrderModel.getItemName());
            }
            vo.setTradeId(null);
            return vo;

        }).collect(Collectors.toList());

    }


    private void getMoxieReport(UserModel userModel, UserReportVO reportVO) {

        UserInfoModel model = userService.selectUserInfoByThridId(userModel.getThirdId());
        if(model == null){
            return;
        }

        MagicReportModel magicReport = magicReportRepository.getMagicReportModelByIdcard(model.getIdcardNum());

        if (magicReport == null) {
            return;
        }

        reportVO.setMoxieReportData(magicReport.getData());
    }

    private ReportDataModel getCarrierReport(UserModel userModel) {
        Map<String, Object> mbMap = userReportDao.selectUserDataTask(userModel.getThirdId(), VariableConst.VAR_SOURCE_MOXIE_CARRIER_REPORT);
        if (mbMap == null) {
            return null;
        }

        ReportDataModel reportDataModel = reportDataRepository.getReportDataModelByUserIdAndTaskId(userModel.getThirdId(), MapUtils.getString(mbMap, "task_id"));

        if (reportDataModel == null) {
            return reportDataModel;
        }
//        reportVO.setCarrierReportData(reportDataModel.getReportData());
//        reportVO.setCarrierReportMessage(reportDataModel.getMessage());

        return reportDataModel;
    }


    private TaobaoReportModel getTaobaoReport(UserModel userModel) {
        Map<String, Object> tbMap = userReportDao.selectUserDataTask(userModel.getThirdId(), VariableConst.VAR_SOURCE_MOXIE_TAOBAO_REPORT);
        if (tbMap == null) {
            return null;
        }
        TaobaoReportModel model = taobaoReportRepository.getReportData(userModel.getThirdId(), MapUtils.getString(tbMap, "task_id"));
        if (model == null) {
            return null;
        }
        return model;

    }


}
