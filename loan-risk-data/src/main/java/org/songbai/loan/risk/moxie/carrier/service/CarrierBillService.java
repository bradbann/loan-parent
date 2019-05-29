package org.songbai.loan.risk.moxie.carrier.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.risk.moxie.carrier.api.CarrierClient;
import org.songbai.loan.risk.moxie.carrier.billitem.CarrierBillTask;
import org.songbai.loan.risk.moxie.carrier.dto.union.*;
import org.songbai.loan.risk.moxie.carrier.model.*;
import org.songbai.loan.risk.moxie.carrier.mongo.*;
import org.songbai.loan.risk.moxie.taobao.util.DateUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 运营商处理类
 * 手机号是基本单位
 * 1、基本信息
 * 2、套餐记录
 * 3、账单记录
 * 4、通话详情
 * 5、短信详情
 * 6、充值记录
 * 7、亲情网记录
 * ClassName: CarrierBillService
 * date: 2016年7月20日 下午5:35:35
 */
@Service
public class CarrierBillService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CarrierBillService.class);

    @Autowired
    private CarrierClient carrierClient;

    @Autowired
    private MobileBasicRepository mobileBasicRepository;

    @Autowired
    private PackageUsageRepository usageRepository;

    @Autowired
    private MobileBillRepository billRepository;

    @Autowired
    private MobileVoiceCallRepository voiceCallRepository;

    @Autowired
    private MobileSmsRepository smsRepository;

    @Autowired
    private MobileNetRepository netRepository;

    @Autowired
    private MobileRechargeRepository rechargeRepository;
    @Autowired
    private FamilyMemberRepository memberRepository;

    @Autowired
    private MonthInfoRepository monthInfoRepository;

    @Autowired
    private MonthInfoItemRepository monthInfoItemDao;


    public void fetchBill(final CarrierBillTask task) {
        // 这里交给线程池处理，防止下面的业务处理时间太长，导致超时。
        // 超时会导致魔蝎数据进行重试，会收到重复的回调请求
        try {
//							MobileBasic mobilebasic = carrierClient.getMobileBasic(task.getMobile(),task.getTaskId());
            UnionDataV3 mxData = carrierClient.getMxData(task.getMobile(), task.getTaskId());
            if (mxData != null) {
                LOGGER.info("开始-处理用户数据: {}", task.getTaskId());
                //1、处理基本信息
                saveMobileBasic(task, mxData);
                //后面的查询最近6个月的数据,这里根据自己的业务来
                String fromMonth = DateUtil.getStrFromDate(DateUtil.addMonth(DateUtil.getCurrentDate(), -6), "yyyy-MM");
                String toMonth = DateUtil.getStrFromDate(DateUtil.getCurrentDate(), "yyyy-MM");
                //2、套餐记录(一般情况下，第一次爬取只会有当月的套餐。如果历史爬取过，会有历史的)
                savePackageUsage(task, mxData.getPackages());

                //6、充值记录
                saveRecharge(task, mxData.getRecharges());
                //7、亲情网
                saveFamily(task, mxData.getFamilies());
                //3、账单记录
                saveBill(task, mxData.getBills());
                //4、通话详情
                saveCall(task, mxData.getCalls());
                //5、短信详情
                saveSms(task, mxData.getSmses());
                //5、流量
                saveNet(task, mxData.getNets());

                //保存语音月份信息
                saveMonth(task, mxData.getMonthInfo());

                LOGGER.info("结束-处理用户数据: {}", task.getTaskId());
            }

        } catch (Exception e) {
            LOGGER.error("fetchBill failed. task:{}", task.getTaskId(), e);
        }
    }

    private void saveMonth(CarrierBillTask task, UnionMonthInfo monthInfo) {
        if (null != monthInfo) {
            MonthInfoModel monthInfoEntity = new MonthInfoModel();
            BeanUtils.copyProperties(monthInfo, monthInfoEntity);
            monthInfoEntity.setUserId(task.getUserId());
            monthInfoEntity.setUpdateTime(new Date());
            //先删除
            monthInfoRepository.deleteMonthInfo(task.getUserId(), monthInfo.getMobile());
            try {
                monthInfoEntity.setCreateTime(new Date());
                monthInfoRepository.insert(monthInfoEntity);
            } catch (Exception e) {
                LOGGER.error("saveBill failed. taskId:{}", task.getTaskId(), e);
            }

            List<MonthItemModel> itemModelList = monthInfo.getMonthList().keySet().stream()
                    .map(key -> {
                        MonthItemModel itemEntity = new MonthItemModel();
                        itemEntity.setUserId(task.getUserId());
                        itemEntity.setMobile(task.getMobile());

                        itemEntity.setMonth(key);
                        itemEntity.setValue(monthInfo.getMonthList().get(key));

                        itemEntity.setCreateTime(new Date());
                        itemEntity.setUpdateTime(new Date());

                        return itemEntity;
                    }).collect(Collectors.toList());

            monthInfoItemDao.insert(itemModelList);
        }
    }

    private void saveMobileBasic(CarrierBillTask task, UnionDataV3 mxData) {
        if (mxData == null) {
            return;
        }


        try {
            MobileBasicModel basicEntity = new MobileBasicModel();
            BeanUtils.copyProperties(mxData, basicEntity);
            basicEntity.setUserId(task.getUserId());
            if (mxData.getOpenTime() != null) {
                basicEntity.setOpenTime(DateUtil.getDateFromString(mxData.getOpenTime(), "yyyy-MM-dd"));
            }

            basicEntity.setUpdateTime(new Date());
            MobileBasicModel isExist = mobileBasicRepository.getMobileBasic(task.getUserId(), task.getMobile());
            if (isExist != null) {
                basicEntity.setId(isExist.getId());
                basicEntity.setCreateTime(isExist.getCreateTime());

                mobileBasicRepository.save(basicEntity);
            } else {
                mobileBasicRepository.insert(basicEntity);
            }
        } catch (Exception e) {
            LOGGER.error("insert error,task:{}", task.getTaskId(), e);
        }
    }

    private void savePackageUsage(CarrierBillTask task, List<UnionPackageUsage> packageUsages) {
        try {

            if (packageUsages != null && !packageUsages.isEmpty()) {
                //删除这个月的此用户此手机号的套餐信息
                usageRepository.deletePackageUsage(task.getUserId(), task.getMobile());

                for (UnionPackageUsage packageUsage : packageUsages) {
                    if (packageUsage.getItems() != null && !packageUsage.getItems().isEmpty()) {


                        List<PackageUsageModel> packageuseList = packageUsage.getItems().stream()
                                .filter(Objects::nonNull)
                                .map(v -> {
                                    PackageUsageModel usageModel = new PackageUsageModel();
                                    BeanUtils.copyProperties(v, usageModel);
                                    usageModel.setMobile(task.getMobile());
                                    usageModel.setUserId(task.getUserId());
//							        packageUsageEntity.setBillMonth(packageUsage.getBillMonth());
                                    usageModel.setBillStartDate(packageUsage.getBillStartDate());
                                    usageModel.setBillEndDate(packageUsage.getBillEndDate());
                                    usageModel.setCreateTime(new Date());
                                    usageModel.setUpdateTime(new Date());
                                    return usageModel;
                                }).collect(Collectors.toList());

                        usageRepository.insert(packageuseList);
                    }
                }

            }

        } catch (Exception e) {
            LOGGER.error("savePackageUsage failed. taskId:{}", task.getTaskId(), e);
        }

    }


    private void saveBill(CarrierBillTask task, List<UnionBill> bills) {
        try {
            if (bills != null && !bills.isEmpty()) {
                for (UnionBill mobileBill : bills) {
                    MobileBillModel mobileBillEntity = new MobileBillModel();
                    BeanUtils.copyProperties(mobileBill, mobileBillEntity);
                    mobileBillEntity.setUserId(task.getUserId());
                    mobileBillEntity.setMobile(task.getMobile());
                    mobileBillEntity.setUpdateTime(new Date());

                    //userId+mobile+billMonth做为唯一约束
                    MobileBillModel isExist = billRepository.getMobileBill(task.getUserId(), task.getMobile(), mobileBill.getBillMonth());
                    if (isExist != null) {
                        mobileBillEntity.setId(isExist.getId());
                        mobileBillEntity.setCreateTime(isExist.getCreateTime());
                        billRepository.save(mobileBillEntity);
                    } else {
                        billRepository.insert(mobileBillEntity);
                    }
                }

            }
        } catch (Exception e) {
            LOGGER.error("saveBill failed. taskId:{}", task.getTaskId(), e);
        }

    }


    private void saveCall(CarrierBillTask task, List<UnionVoiceCall> calls) {
        try {

            if (calls != null && !calls.isEmpty()) {
                for (UnionVoiceCall unionVoiceCall : calls) {
                    if (unionVoiceCall != null && unionVoiceCall.getVoiceCallItems() != null && !unionVoiceCall.getVoiceCallItems().isEmpty()) {
                        voiceCallRepository.deleteMobileVoiceCall(task.getUserId(), task.getMobile(), unionVoiceCall.getBillMonth());

                        List<MobileVoiceCallModel> mobileVoiceCalls = unionVoiceCall.getVoiceCallItems().stream()
                                .filter(Objects::nonNull)
                                .map(v -> {
                                    MobileVoiceCallModel callModel = new MobileVoiceCallModel();
                                    BeanUtils.copyProperties(v, callModel);
                                    callModel.setUserId(task.getUserId());
                                    callModel.setMobile(task.getMobile());
                                    callModel.setBillMonth(unionVoiceCall.getBillMonth());
                                    callModel.setCreateTime(new Date());
                                    callModel.setUpdateTime(new Date());
                                    return callModel;
                                }).collect(Collectors.toList());

                        voiceCallRepository.insert(mobileVoiceCalls);
                    }

                }

            }

        } catch (Exception e) {
            LOGGER.error("saveCall failed. taskId:{}", task.getTaskId(), e);
        }
    }


    private void saveSms(CarrierBillTask task, List<UnionShortMessage> shortMessages) {
        try {
            if (shortMessages == null || shortMessages.isEmpty()) {
                return;
            }
            for (UnionShortMessage mobileSmsDetail : shortMessages) {
                if (mobileSmsDetail != null && mobileSmsDetail.getShortMessageItems() != null && !mobileSmsDetail.getShortMessageItems().isEmpty()) {

                    smsRepository.deleteMobileSms(task.getUserId(), task.getMobile(), mobileSmsDetail.getBillMonth());

                    List<MobileSmsModel> smsList = mobileSmsDetail.getShortMessageItems().stream()
                            .filter(Objects::nonNull)
                            .map(v -> {
                                MobileSmsModel mobileSmsEntity = new MobileSmsModel();
                                BeanUtils.copyProperties(v, mobileSmsEntity);
                                mobileSmsEntity.setUserId(task.getUserId());
                                mobileSmsEntity.setMobile(task.getMobile());
                                mobileSmsEntity.setBillMonth(mobileSmsDetail.getBillMonth());

                                mobileSmsEntity.setCreateTime(new Date());
                                mobileSmsEntity.setUpdateTime(new Date());

                                return mobileSmsEntity;
                            }).collect(Collectors.toList());

                    smsRepository.insert(smsList);
                }

            }


        } catch (Exception e) {
            LOGGER.error("saveSms failed. taskId:{}", task.getTaskId(), e);
        }
    }

    private void saveNet(CarrierBillTask task, List<UnionNetFlow> nets) {
        try {
            if (nets == null || nets.isEmpty()) {
                return;
            }

            for (UnionNetFlow netFlow : nets) {
                if (netFlow == null || netFlow.getNetFlowItems() == null || netFlow.getNetFlowItems().isEmpty()) {
                    continue;
                }

                netRepository.deleteMobileNetFlow(task.getUserId(), task.getMobile(), netFlow.getBillMonth());
                List<NetFlowModel> netflows = netFlow.getNetFlowItems().stream()
                        .filter(Objects::nonNull)
                        .map(v -> {
                            NetFlowModel netFlowEntity = new NetFlowModel();
                            BeanUtils.copyProperties(v, netFlowEntity);
                            netFlowEntity.setUserId(task.getUserId());
                            netFlowEntity.setMobile(task.getMobile());
                            netFlowEntity.setBillMonth(netFlow.getBillMonth());
                            netFlowEntity.setCreateTime(new Date());
                            netFlowEntity.setUpdateTime(new Date());
                            return netFlowEntity;
                        }).collect(Collectors.toList());

                netRepository.insert(netflows);


            }


        } catch (Exception e) {
            LOGGER.error("saveNet failed. taskId:{}", task.getTaskId(), e);
        }

    }

    private void saveRecharge(CarrierBillTask task, List<UnionRecharge> mobileRecharges) {
        try {

            if (mobileRecharges != null && !mobileRecharges.isEmpty()) {
                rechargeRepository.deleteMobileRecharge(task.getUserId(), task.getMobile());
                List<MobileRechargeModel> rechargeList = mobileRecharges.stream()
                        .filter(Objects::nonNull)
                        .map(v -> {
                            MobileRechargeModel mobileRechargeEntity = new MobileRechargeModel();
                            BeanUtils.copyProperties(v, mobileRechargeEntity);
                            mobileRechargeEntity.setUserId(task.getUserId());
                            mobileRechargeEntity.setMobile(task.getMobile());
                            mobileRechargeEntity.setCreateTime(new Date());
                            mobileRechargeEntity.setUpdateTime(new Date());

                            return mobileRechargeEntity;
                        }).collect(Collectors.toList());

                rechargeRepository.insert(rechargeList);
            }


        } catch (Exception e) {
            LOGGER.error("saveRecharge failed. taskId:{}", task.getTaskId(), e);
        }
    }


    /**
     * saveFamily:保存和此手机号关联的亲情网号码 <br/>
     *
     * @param task
     */
    private void saveFamily(CarrierBillTask task, List<UnionFamilyNet> familyNets) {
        try {
            if (familyNets == null || familyNets.isEmpty()) {
                return;
            }

            for (UnionFamilyNet familyNet : familyNets) {
                if (familyNet.getFamilyMembers() == null || familyNet.getFamilyMembers().isEmpty()) {
                    continue;
                }

                //删除这一组亲情网
                memberRepository.deleteFamilyMember(task.getUserId(), task.getMobile(), familyNet.getFamilyNetNum());
                //添加亲情网

                List<FamilyMemberModel> list = familyNet.getFamilyMembers().stream()
                        .filter(Objects::nonNull)
                        .filter(v -> v.getLongNumber() != null && v.getShortNumber() != null && v.getMemberType() != null)
                        .map(v -> {
                            FamilyMemberModel familyMemberEntity = new FamilyMemberModel();
                            BeanUtils.copyProperties(v, familyMemberEntity);
                            familyMemberEntity.setUserId(task.getUserId());
                            familyMemberEntity.setMobile(task.getMobile());
                            familyMemberEntity.setFamilyNetNum(familyNet.getFamilyNetNum());
                            familyMemberEntity.setCreateTime(new Date());
                            familyMemberEntity.setUpdateTime(new Date());
                            return familyMemberEntity;
                        }).collect(Collectors.toList());

                memberRepository.insert(list);
            }
        } catch (Exception e) {
            LOGGER.error("saveRecharge failed. taskId:{}", task.getTaskId(), e);
        }
    }


}
