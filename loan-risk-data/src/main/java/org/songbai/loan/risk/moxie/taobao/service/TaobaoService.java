package org.songbai.loan.risk.moxie.taobao.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.loan.risk.moxie.taobao.api.TaobaoClient;
import org.songbai.loan.risk.moxie.taobao.model.*;
import org.songbai.loan.risk.moxie.taobao.model.dto.*;
import org.songbai.loan.risk.moxie.taobao.model.vo.TaobaoTask;
import org.songbai.loan.risk.moxie.taobao.mongo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaobaoService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaobaoService.class);

    @Autowired
    private TaobaoClient taobaoClient;
    @Autowired
    private TaobaoUserInfoRepository userInfoDao;
    @Autowired
    private DeliverAddressRepository deliverAddressDao;
    @Autowired
    private RecentAddressRepository recentAddressDao;
    @Autowired
    private TaobaoTradeInfoRepository taobaoTradeInfoDao;
    @Autowired
    private TaobaoSuborderRepository taobaoSuborderDao;

    @Autowired
    private TaobaoAlipayWealthRepository taobaoAlipayWealthDao;


    /**
     * fetchBill:处理邮箱下面的所有账单 <br/>
     * 在收到魔蝎的邮箱账单回调时执行<br/>
     * for(保存账单){ 保存单条账单 －保存消费记录 －保存分期记录 }<br/>
     *
     * @param task
     * @since JDK 1.6
     */
    public void fetchBill(final TaobaoTask task) {
        // 这里交给线程池处理，防止下面的业务处理时间太长，导致超时。
        // 超时会导致魔蝎数据进行重试，会收到重复的回调请求

        try {
            //查询用户的基本信息
//            				saveUserInfo(task);
//            				//保存收货地址
//            				saveDeliverAddress(task);
//            				//保存最近订单的收货地址
//            				saveRecentAddress(task);
//            				//保存订单和商品
//            				saveTradeDetails(task);

            saveData(task);

        } catch (Exception e) {
            LOGGER.error("fetchBill failed. account:{}", task.getAccount(), e);
        }

    }


    private void saveData(TaobaoTask task) {
        try {
            TaobaoData taobaoData = taobaoClient.getData(task.getTaskId());
            if (taobaoData != null) {
                LOGGER.info("start deal taobao data......");
                //保存基本信息
                saveTaobaoUserInfo(task, taobaoData);

                //保存收货地址
                saveTaobaoDeliverAddress(task, taobaoData);

                //保存最近交易记录的收货地址
                saveRecentDeliverAddress(task, taobaoData);

                //保存淘宝对应支付宝信息
                saveTaobaoAlipayWealth(task, taobaoData);


                saveTaobaoTradeDetails(task, taobaoData);
                LOGGER.info("end deal taobao data......");
            }


        } catch (Exception e) {
            LOGGER.error("save taobao data error", e);
        }
    }

    private void saveTaobaoTradeDetails(TaobaoTask task, TaobaoData taobaoData) {
        List<TradeDetails> tradeDetailsList = taobaoData.getTradedetails();
        for (TradeDetails tradeDetails : tradeDetailsList) {
            TradeDetailModel newModel = new TradeDetailModel();
            BeanUtils.copyProperties(tradeDetails, newModel);
            newModel.setUserId(task.getUserId());
            newModel.setUpdateTime(new Date());

            //保存消费记录
            TradeDetailModel isExist = taobaoTradeInfoDao.getTradeDetailEntity(task.getUserId(), task.getMappingId(), tradeDetails.getTradeId());
            if (isExist == null) {
                newModel.setCreateTime(new Date());
                taobaoTradeInfoDao.insert(newModel);
            } else {
                newModel.setId(isExist.getId());
                taobaoTradeInfoDao.save(newModel);
            }

            //保存商品信息
            List<TaobaoSubOrder> subOrders = tradeDetails.getSubOrders();
            if (subOrders != null && !subOrders.isEmpty()) {
                taobaoSuborderDao.deleteSubOrder(task.getUserId(), tradeDetails.getMappingId(), tradeDetails.getTradeId());
                for (TaobaoSubOrder taobaoSubOrder : subOrders) {
                    SubOrderModel subOrderEntity = new SubOrderModel();
                    BeanUtils.copyProperties(taobaoSubOrder, subOrderEntity);
                    subOrderEntity.setUserId(task.getUserId());
                    subOrderEntity.setUpdateTime(new Date());
                    subOrderEntity.setCreateTime(new Date());
                    taobaoSuborderDao.insert(subOrderEntity);
                }
            }

        }

    }

    private void saveTaobaoAlipayWealth(TaobaoTask task, TaobaoData taobaoData) {
        if (taobaoData.getTaobaoAlipayWealth() != null) {
            TaobaoAlipayWealth taobaoAlipayWealth = taobaoData.getTaobaoAlipayWealth();
            TaobaoAlipayWealthModel taobaoAlipayWealthModel = new TaobaoAlipayWealthModel();
            BeanUtils.copyProperties(taobaoAlipayWealth, taobaoAlipayWealthModel);
            taobaoAlipayWealthModel.setUserId(task.getUserId());
            taobaoAlipayWealthModel.setCreateTime(new Date());

            TaobaoAlipayWealthModel isExistAw = taobaoAlipayWealthDao.getAlipayWealth(task.getUserId(), task.getMappingId());
            if (isExistAw == null) {
                taobaoAlipayWealthDao.insert(taobaoAlipayWealthModel);
            } else {
                taobaoAlipayWealthModel.setId(isExistAw.getId());
                taobaoAlipayWealthModel.setUpdateTime(new Date());
                taobaoAlipayWealthDao.save(taobaoAlipayWealthModel);
            }
        }
    }

    private void saveRecentDeliverAddress(TaobaoTask task, TaobaoData taobaoData) {
        List<RecentDeliverAddress> recentDeliverAddressList = taobaoData.getRecentdeliveraddress();
        if (recentDeliverAddressList != null && !recentDeliverAddressList.isEmpty()) {

            for (RecentDeliverAddress recentDeliverAddress : recentDeliverAddressList) {
                RecentAddressModel basicEntity = new RecentAddressModel();
                BeanUtils.copyProperties(recentDeliverAddress, basicEntity);
                basicEntity.setUserId(task.getUserId());
                basicEntity.setMappingId(task.getMappingId());
                RecentAddressModel isExist = recentAddressDao.getRecentAddress(task.getUserId(), task.getMappingId(), recentDeliverAddress.getTradeId());
                if (isExist == null) {
                    basicEntity.setCreateTime(new Date());
                    recentAddressDao.insert(basicEntity);
                } else {
                    basicEntity.setId(isExist.getId());
                    basicEntity.setCreateTime(isExist.getCreateTime());
                    basicEntity.setUpdateTime(new Date());
                    recentAddressDao.save(basicEntity);
                }
            }
        }
    }

    private void saveTaobaoDeliverAddress(TaobaoTask task, TaobaoData taobaoData) {
        List<DeliverAddress> deliverAddressList = taobaoData.getDeliveraddress();
        if (deliverAddressList != null && !deliverAddressList.isEmpty()) {
            deliverAddressDao.deleteDeliverAddress(task.getUserId(), task.getMappingId());
            for (DeliverAddress deliverAddress : deliverAddressList) {

                DeliverAddressModel basicEntity = new DeliverAddressModel();
                BeanUtils.copyProperties(deliverAddress, basicEntity);
                basicEntity.setUserId(task.getUserId());
                basicEntity.setCreateTime(new Date());
                basicEntity.setUpdateTime(new Date());

                deliverAddressDao.insert(basicEntity);
            }
        }
    }

    private void saveTaobaoUserInfo(TaobaoTask task, TaobaoData taobaoData) {
        if (taobaoData.getUserinfo() != null) {
            TaobaoUserInfo taobaoUserInfo = taobaoData.getUserinfo();
            UserInfoModel updateModel = new UserInfoModel();
            BeanUtils.copyProperties(taobaoUserInfo, updateModel);
            updateModel.setUserId(task.getUserId());
            updateModel.setUpdateTime(new Date());

            UserInfoModel oldUserInfo = userInfoDao.getUserInfo(task.getUserId(), task.getMappingId());
            if (oldUserInfo == null) {
                updateModel.setCreateTime(new Date());
                userInfoDao.insert(updateModel);
            } else {
                userInfoDao.save(updateModel);

                updateModel.setId(oldUserInfo.getId());
            }
        }
    }


}
