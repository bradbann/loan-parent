package org.songbai.loan.admin.chase.service.impl;

import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.chase.dao.ChaseDebtDao;
import org.songbai.loan.admin.chase.dao.ChaseFeedDao;
import org.songbai.loan.admin.chase.po.ChaseFeedPo;
import org.songbai.loan.admin.chase.service.ChaseFeedService;
import org.songbai.loan.admin.chase.vo.ChaseFeedVo;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.chase.ChaseFeedModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class ChaseFeedServiceImpl implements ChaseFeedService {
    @Autowired
    ChaseFeedDao chaseFeedDao;
    @Autowired
    ChaseDebtDao chaseDebtDao;
    @Autowired
    OrderDao orderDao;
    @Autowired
    ComAgencyService comAgencyService;


    @Override
    @Transactional
    public void addChaseFeedBack(ChaseFeedModel chaseFeedModel) {
        OrderModel order = chaseDebtDao.getOrderByChaseId(chaseFeedModel.getChaseId());
        if (order == null) {
            throw new BusinessException(AdminRespCode.ORDER_NOT_EXISIT);
        }

        chaseFeedModel.setOrderNumber(order.getOrderNumber());
        chaseFeedModel.setUserId(order.getUserId());
        chaseFeedModel.setDeptId(order.getChaseDeptId());

        chaseFeedDao.insert(chaseFeedModel);

        OrderModel model = new OrderModel();
        model.setLastFeedType(chaseFeedModel.getFeedType());
        model.setId(order.getId());
        orderDao.updateById(model);

    }

    @Override
    public Page<ChaseFeedVo> getChaseFeedPage(ChaseFeedPo po, List<Integer> deptIds) {
        Integer count = chaseFeedDao.queryFeedCount(po, deptIds);
        if (count == 0) {
            return new Page<>(po.getPage(), po.getPageSize(), count, new ArrayList<>());
        }
        List<ChaseFeedVo> list = chaseFeedDao.findChaseFeedList(po, deptIds);
        list.forEach(e -> {
            AgencyModel agency = comAgencyService.findAgencyById(e.getAgencyId());
            if (agency != null) e.setAgencyName(agency.getAgencyName());
        });
        return new Page<>(po.getPage(), po.getPageSize(), count, list);
    }

    @Override
    public List<ChaseFeedVo> getChaseListByChaseId(String chaseId, Integer agencyId) {
        return chaseFeedDao.findChaseListByChaseId(chaseId, agencyId);
    }
}
