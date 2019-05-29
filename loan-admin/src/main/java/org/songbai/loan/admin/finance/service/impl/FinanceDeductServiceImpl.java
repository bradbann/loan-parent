package org.songbai.loan.admin.finance.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.finance.dao.FinanceDeductDao;
import org.songbai.loan.admin.finance.model.po.DeductPo;
import org.songbai.loan.admin.finance.model.po.DeductQueuePO;
import org.songbai.loan.admin.finance.model.vo.DeductPageVo;
import org.songbai.loan.admin.finance.model.vo.DeductQueueVo;
import org.songbai.loan.admin.finance.service.FinanceDeductService;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.user.DeductConst;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.loan.FinanceDeductModel;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;


@Service
public class FinanceDeductServiceImpl implements FinanceDeductService {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private FinanceDeductDao financeDeductDao;

    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private ComAgencyService comAgencyService;

    @Autowired
    private SpringProperties springProperties;


    @Override
    public List<String> saveFinanceDeductModel(List<String> orderNumbers, Integer agencyId, AdminUserModel actorModel) {


        List<OrderModel> orderModels = orderDao.selectOrderByIdsAndAgencyId(orderNumbers, agencyId);


        List<String> result = new ArrayList<>();
        for (OrderModel model : orderModels) {

            if (model.getStage() != OrderConstant.Stage.REPAYMENT.key) {
                result.add("订单" + model.getOrderNumber() + "不是还款阶段");
                continue;
            }

            int huankuanri = DateUtils.truncatedCompareTo(model.getRepaymentDate(), new Date(), Calendar.DAY_OF_MONTH);

            if (model.getStatus() != OrderConstant.Status.OVERDUE.key
                    && model.getStatus() != OrderConstant.Status.FAIL.key
                    && !(model.getStatus() == OrderConstant.Status.WAIT.key && huankuanri == 0)) {
                result.add("订单" + model.getOrderNumber() + "不是逾期或者待还款");
                continue;
            }

            FinanceDeductModel deductModel = saveDeductModelWrapper(model, actorModel);


            Map<String, Object> map = new HashMap<>();
            map.put("deductId", deductModel.getId());
            map.put("orderNumber", deductModel.getOrderNumber());
            jmsTemplate.convertAndSend(JmsDest.AUTO_DEDUCT, JSON.toJSONString(map));
        }
        return result;
    }

    @Override
    public Page<DeductPageVo> findDeductFlowList(DeductPo po) {
        Integer count = financeDeductDao.findDeductFlowCount(po);
        if (count == 0) return new Page<>(po.getPage(), po.getPageSize(), count, new ArrayList<>());
        List<DeductPageVo> list = financeDeductDao.findDeductFlowList(po);
        list.forEach(e -> {
            if (e.getAgencyId() != null) {
                AgencyModel agencyModel = comAgencyService.findAgencyById(e.getAgencyId());
                if (agencyModel != null) e.setAgencyName(agencyModel.getAgencyName());
            }
            if (StringUtils.isNotEmpty(e.getPayPlatform())) {
                e.setPlatformName(FinanceConstant.PayPlatform.getName(e.getPayPlatform()));
            }
            if (e.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(e.getVestId());
                if (vestModel != null) e.setVestName(vestModel.getName());
            }

        });
        return new Page<>(po.getPage(), po.getPageSize(), count, list);
    }

    @Override
    public Map findDeductTotal(Integer agencyId) {
        Map map = financeDeductDao.findDeductTotal(agencyId);
        if (map != null && map.get("deductMoney") != null) {
            String deductMoney = (String) map.get("deductMoney");
            DecimalFormat format = new DecimalFormat("0.00");
            map.put("deductMoney", format.format(new BigDecimal(deductMoney)));
        }
        return map;
    }

    @Override
    public Page<DeductQueueVo> findDeductQueue(DeductQueuePO po) {

        po.setEndRepayMentDate(StringUtil.trimToNull(po.getEndRepayMentDate()));
        po.setStartRepayMentDate(StringUtil.trimToNull(po.getStartRepayMentDate()));
        po.setOrderNumber(StringUtil.trimToNull(po.getOrderNumber()));
        po.setUserPhone(StringUtil.trimToNull(po.getUserPhone()));

        int count = financeDeductDao.findDeductQueueCount(po);
        if (count <= 0) {
            return new Page<>(po.getPage(), po.getPageSize(), 0, new ArrayList<>());
        }
        List<DeductQueueVo> list = financeDeductDao.findDeductQueueList(po);
        list.forEach(e -> {
            if (e.getVestId() != null) {
                AppVestModel vest = comAgencyService.getVestInfoByVestId(e.getVestId());
                if (vest != null) {
                    e.setVestName(vest.getName());
                }
                e.setVestId(null);
            }
        });
        return new Page<>(po.getPage(), po.getPageSize(), count, list);
    }

    @Override
    public void cancelRepay(String orderNumber, Integer agencyId) {
        FinanceDeductModel select = new FinanceDeductModel();
        select.setAgencyId(agencyId);
        select.setOrderNumber(orderNumber);

        FinanceDeductModel deductModel = financeDeductDao.selectOne(select);
        if (deductModel == null) {
            throw new BusinessException(AdminRespCode.DEDUCT_NOT_EXIST);

        }
        if (deductModel.getStatus() != DeductConst.Status.WAIT.code) {
            throw new BusinessException(AdminRespCode.DEDUCT_CANCEL_FAIL);
        }

        FinanceDeductModel update = new FinanceDeductModel();
        update.setId(deductModel.getId());
        update.setStatus(DeductConst.Status.FAIL.code);
        update.setRemark("人工取消");

        financeDeductDao.updateById(update);
    }

    private FinanceDeductModel saveDeductModelWrapper(OrderModel orderModel, AdminUserModel actorModel) {
        FinanceDeductModel deductModel = new FinanceDeductModel();

        deductModel.setUserId(orderModel.getUserId());
        deductModel.setAgencyId(orderModel.getAgencyId());
        deductModel.setOrderId(orderModel.getId());
        deductModel.setOrderNumber(orderModel.getOrderNumber());
        deductModel.setPayment(orderModel.getPayment() - orderModel.getAlreadyMoney());
        deductModel.setDeductMoney(0.0);
        deductModel.setDeductNum(0);

        Integer open = springProperties.getInteger("admin.deduct.open", 0);

        if (open == 1) {
            deductModel.setStatus(DeductConst.Status.WAIT.code);
        } else {
            deductModel.setStatus(DeductConst.Status.FAIL.code);
            deductModel.setRemark("没有开启代扣功能");
        }

        Integer deductType = springProperties.getInteger("admin.deduct.type", DeductConst.DeductType.FIX.code);
        deductModel.setDeductType(deductType);

        String deductConfig = springProperties.getString("admin.deduct.config", "300");
        deductModel.setDeductConfig(deductConfig);


        deductModel.setActorId(actorModel.getId());
        deductModel.setActorName(actorModel.getName());

        financeDeductDao.insert(deductModel);

        return deductModel;
    }


}
