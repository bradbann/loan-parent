package org.songbai.loan.risk.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.loan.risk.dao.UserRiskOrderDao;
import org.songbai.loan.risk.model.user.UserRiskOrderModel;
import org.songbai.loan.risk.service.RiskOrderService;
import org.songbai.loan.risk.vo.RiskResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class RiskOrderServiceImpl implements RiskOrderService {


    @Autowired
    private UserRiskOrderDao userRiskOrderDao;

    @Autowired
    private SpringProperties springProperties;


    @Override
    public void submitOrder(String userId, String orderNumber) {


        UserRiskOrderModel model = new UserRiskOrderModel();

        model.setUserId(userId);
        model.setOrderNumber(orderNumber);
        model.setStatus(0);

        userRiskOrderDao.insert(model);
    }

    @Override
    public UserRiskOrderModel selectRiskOrderModel(String userId, String orderNumber) {
        UserRiskOrderModel model = new UserRiskOrderModel();

        model.setUserId(userId);
        model.setOrderNumber(orderNumber);
//        model.setStatus(0);
        return userRiskOrderDao.selectOneLastRiskOrder(userId, orderNumber);

//        return userRiskOrderDao.selectOne(model);
    }

    @Override
    public void authFinish(RiskResultVO resultVO) {

        UserRiskOrderModel query = selectRiskOrderModel(resultVO.getUserId(), resultVO.getOrderNumber());

        UserRiskOrderModel update = new UserRiskOrderModel();

        update.setId(query.getId());

        update.setStatus(1);
        update.setRemark(resultVO.getMsg());
        update.setRiskResult(resultVO.getRiskResult());
        update.setRiskResultList(JSONObject.toJSONString(resultVO.getRiskResultList()));
        update.setScoring(resultVO.getScoring());
        if (query.getMouldId() == null)
            update.setMouldId(resultVO.getMouldId());

        String resultMsg = StringUtils.join(resultVO.getRiskResultMsg(), ";");

        if (StringUtils.isNotEmpty(resultMsg)) {
            update.setRiskResultMsg(resultMsg.getBytes().length > 800 ? resultMsg.substring(0, 400) : resultMsg);
        }

        userRiskOrderDao.updateById(update);
    }

    @Override
    public void waitData(RiskResultVO resultVO) {

        UserRiskOrderModel query = selectRiskOrderModel(resultVO.getUserId(), resultVO.getOrderNumber());

        UserRiskOrderModel update = new UserRiskOrderModel();

        update.setId(query.getId());

        update.setStatus(2);
        update.setRemark(resultVO.getMsg());
        if (query.getMouldId() == null) {
            update.setMouldId(resultVO.getMouldId());
        }

        userRiskOrderDao.updateById(update);
    }

    @Override
    public void authFail(RiskResultVO resultVO) {

        UserRiskOrderModel query = selectRiskOrderModel(resultVO.getUserId(), resultVO.getOrderNumber());

        UserRiskOrderModel update = new UserRiskOrderModel();

        update.setId(query.getId());

        update.setStatus(3);
        update.setRemark(resultVO.getMsg());
        if (query.getMouldId() == null)
            update.setMouldId(resultVO.getMouldId());

        userRiskOrderDao.updateById(update);
    }


    @Override
    public List<UserRiskOrderModel> selectUserRiskOrderModel() {
        Integer interval = springProperties.getInteger("risk.order.waitdata.check", 2);


        return userRiskOrderDao.selectUserRiskOrderModel(interval);
    }

}

