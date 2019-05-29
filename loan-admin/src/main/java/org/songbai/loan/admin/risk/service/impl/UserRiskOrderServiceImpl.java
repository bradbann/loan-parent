package org.songbai.loan.admin.risk.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.loan.admin.risk.dao.RiskMouldDao;
import org.songbai.loan.admin.risk.dao.UserRiskOrderDao;
import org.songbai.loan.admin.risk.model.UserRiskOrderVO;
import org.songbai.loan.admin.risk.model.po.RiskOrderPO;
import org.songbai.loan.admin.risk.service.UserRiskOrderService;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.risk.model.mould.RiskMouldModel;
import org.songbai.loan.risk.model.user.RiskUserMouldCatalogModel;
import org.songbai.loan.risk.model.user.UserRiskOrderModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Service
public class UserRiskOrderServiceImpl implements UserRiskOrderService {

    @Autowired
    private UserRiskOrderDao userRiskOrderDao;


    @Autowired
    MongoTemplate mongoTemplate;

    @Autowired
    ComUserService userService;

    @Autowired
    RiskMouldDao riskMouldDao;


    @Override
    public Page<UserRiskOrderVO> selectRiskOrderList(RiskOrderPO po) {

        Pagination pagination = new Pagination();

        pagination.setCurrent(po.getPage() == null ? 1 : po.getPage() + 1);
        pagination.setSize(po.getPageSize() == null ? Page.DEFAULE_PAGESIZE : po.getPageSize());


        UserRiskOrderModel query = new UserRiskOrderModel();

        query.setMouldId(po.getMouldId());
        query.setOrderNumber(po.getOrderNumber());
        query.setStatus(po.getStatus());


        EntityWrapper entityWrapper = new EntityWrapper<>(query);

        entityWrapper.orderBy("id", false);

        List<UserRiskOrderModel> list = userRiskOrderDao.selectPage(pagination, entityWrapper);


        List<UserRiskOrderVO> resultList = new ArrayList<>();

        HashMap<Integer, String> mouldNameMap = new HashMap<>();
        for (UserRiskOrderModel model : list) {

            UserRiskOrderVO vo = new UserRiskOrderVO();

            BeanUtils.copyProperties(model, vo);

            UserModel infoModel = userService.selectUserModelByThridId(model.getUserId());

            if (infoModel != null) {
                vo.setUserName(infoModel.getName());
            }


            if (mouldNameMap.containsKey(model.getMouldId())) {
                vo.setMouldName(mouldNameMap.get(model.getMouldId()));
            } else {

                RiskMouldModel mouldModel = riskMouldDao.getRiskById(model.getMouldId());

                if (mouldModel != null) {
                    mouldNameMap.put(mouldModel.getId(), mouldModel.getName());
                    vo.setMouldName(mouldModel.getName());
                }

            }

            resultList.add(vo);
        }


        return new Page<>(pagination.getCurrent() - 1, pagination.getSize(), pagination.getTotal(), resultList);
    }

    @Override
    public List<RiskUserMouldCatalogModel> selectMouldCatalog(String userId, String orderNumber) {

        Query query = Query.query(Criteria.where("userId").is(userId).and("orderNumber").is(orderNumber));

        query.with(new Sort(Sort.Direction.ASC, "catalog"));

        return mongoTemplate.find(query, RiskUserMouldCatalogModel.class);
    }

}
