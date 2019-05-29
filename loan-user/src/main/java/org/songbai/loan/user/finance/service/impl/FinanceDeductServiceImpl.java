package org.songbai.loan.user.finance.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.apache.commons.lang.math.NumberUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.cloud.basics.utils.math.Arith;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.user.DeductConst;
import org.songbai.loan.constant.user.DeductConst.Flow;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.FinanceDeductFlowModel;
import org.songbai.loan.model.loan.FinanceDeductModel;
import org.songbai.loan.user.finance.dao.FinanceDeductDao;
import org.songbai.loan.user.finance.dao.FinanceDeductFlowDao;
import org.songbai.loan.user.finance.model.vo.PayBankCardVO;
import org.songbai.loan.user.finance.model.vo.PayOrderVO;
import org.songbai.loan.user.finance.model.vo.PayResultVO;
import org.songbai.loan.user.finance.service.FinanceDeductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FinanceDeductServiceImpl implements FinanceDeductService {


    @Autowired
    private FinanceDeductDao financeDeductDao;

    @Autowired
    private FinanceDeductFlowDao financeDeductFlowDao;


    @Override
    public FinanceDeductModel selectDeductModelById(Integer id) {


        return financeDeductDao.selectById(id);
    }

    @Override
    public FinanceDeductFlowModel selectDeductFlowModelByDeductId(Integer deductId) {


        return financeDeductDao.selectLastFinanceDeductFlowByDeductId(deductId);
    }

    @Override
    public FinanceDeductFlowModel saveDeductFlow(FinanceDeductModel deductModel, PayOrderVO orderVO, PayBankCardVO payBankCardVO) {


        FinanceDeductFlowModel model = createDeductFlowModel(deductModel, orderVO, payBankCardVO);


        if (orderVO.getOrderModel() != null) {
            model.setRepaymentDate(orderVO.getOrderModel().getRepaymentDate());
        }

        model.setDeductTime(new Date());

        financeDeductFlowDao.insert(model);

        return model;
    }

    @Override
    public void updateDeductFlowForPayResult(FinanceDeductFlowModel flowModel, PayResultVO resultVO) {


        FinanceDeductFlowModel update = new FinanceDeductFlowModel();

        update.setId(flowModel.getId());


        if (resultVO.getSts() == CommonConst.NO || resultVO.getSts() == CommonConst.OK) {   // 失败
            update.setDeductNumber(resultVO.getPayTrxId());

            update.setStatus(Flow.FAIL.code);
            update.setRemark(resultVO.getMsg());
        } else if (resultVO.getSts() == CommonConst.YES) { // 成功

            update.setDeductNumber(resultVO.getPayTrxId());
            update.setStatus(Flow.WAIT.code); // 这里不能更新状态
            update.setRemark(resultVO.getMsg());
        } else {
            throw new BusinessException("PayResultVO.sts is error : " + resultVO);
        }


        financeDeductFlowDao.updateById(update);

        flowModel.setStatus(update.getStatus());
        flowModel.setRemark(update.getRemark());
        flowModel.setDeductNumber(update.getDeductNumber());
    }

    @Override
    public void updateDeductStatus(FinanceDeductModel deductModel, Integer sts, String msg) {

        FinanceDeductModel update = new FinanceDeductModel();

        update.setId(deductModel.getId());
        update.setStatus(sts);
        update.setRemark(msg);

        financeDeductDao.updateById(update);

    }

    @Override
    public Integer queryDeductSuccessCount(Integer deductId) {
        FinanceDeductFlowModel flowModel = new FinanceDeductFlowModel();

        flowModel.setDeductId(deductId);
        flowModel.setStatus(Flow.SUCCESS.code);

        return financeDeductFlowDao.selectCount(new EntityWrapper<>(flowModel));
    }

    @Override
    public void updateDeductStatusAndNum(FinanceDeductModel deductModel, Integer sts, String msg) {

        FinanceDeductModel update = new FinanceDeductModel();

        update.setId(deductModel.getId());
        update.setStatus(sts);
        update.setRemark(msg);
        update.setDeductNum(deductModel.getDeductNum() + 1);

        financeDeductDao.updateById(update);

    }


    @Override
    public FinanceDeductModel updateDeductMoney(Integer deductId, Double money) {
        financeDeductDao.updateDeductMoney(deductId, money);

        return financeDeductDao.selectById(deductId);
    }


    @Override
    public FinanceDeductFlowModel updateDeductFlowStatusForIoModel(FinanceIOModel ioModel, boolean success, String msg) {

        FinanceDeductFlowModel query = new FinanceDeductFlowModel();

        query.setDeductNumber(ioModel.getRequestId());
        query = financeDeductFlowDao.selectOne(query);

        if (query == null) {
            throw new BusinessException("不能获取到代扣流水信息");
        }


        FinanceDeductFlowModel update = new FinanceDeductFlowModel();

        update.setId(query.getId());

        update.setStatus(success ? Flow.SUCCESS.code : Flow.FAIL.code); // 这里不能更新状态
        update.setRemark(msg);

        financeDeductFlowDao.updateById(update);

        query.setStatus(update.getStatus());
        query.setRemark(update.getRemark());
        return query;
    }

    private FinanceDeductFlowModel createDeductFlowModel(FinanceDeductModel deductModel, PayOrderVO orderVO, PayBankCardVO payBankCardVO) {
        FinanceDeductFlowModel model = new FinanceDeductFlowModel();


        model.setDeductId(deductModel.getId());
        model.setDeductLevel(orderVO.getPayRate());


        model.setUserId(deductModel.getUserId());
        model.setAgencyId(orderVO.getAgencyId());

        model.setOrderNumber(deductModel.getOrderNumber());
        model.setActorId(deductModel.getActorId());
        model.setActorName(deductModel.getActorName());


//        model.setDeductNumber();
        model.setDeductMoney(orderVO.getPayment());
        model.setAlreadyDeduct(orderVO.getOrderModel().getChargingMoney());
        model.setStatus(Flow.WAIT.code);

        model.setPayPlatform(payBankCardVO.getBindPlatform());
        model.setBankName(payBankCardVO.getBankName());
        model.setBankCardNum(payBankCardVO.getBankCardNum());
        return model;
    }


    /**
     *
     * @param deductModel
     * @param lastDeductFlow
     * @return double array , length 2
     *         return[0] == -1 : 配置错误
     *         return[0] == -2 : 数据错误
     */
    @Override
    public double[] getDeductLimit(FinanceDeductModel deductModel, FinanceDeductFlowModel lastDeductFlow) {

        if (deductModel == null) {
            throw new RuntimeException("Deduct model not found");
        }


        if (deductModel.getDeductType() == DeductConst.DeductType.FIX.code) {
            // 按照固定的额度进行扣款的。
            int fixLimit = NumberUtils.toInt(deductModel.getDeductConfig(), 0);

            if (lastDeductFlow == null) {
                // 表示第一次扣款
                double remaining = Arith.subtract(2, deductModel.getPayment(), deductModel.getDeductMoney());

                return new double[]{DeductConst.DeductType.FIX.code, remaining > fixLimit ? fixLimit : remaining};
            }

            if (lastDeductFlow.getStatus() == DeductConst.Flow.SUCCESS.code) {
                // 如果上次扣款成功了，那么就看看还剩下多少
                double remaining = Arith.subtract(2, deductModel.getPayment(), deductModel.getDeductMoney());

                return new double[]{DeductConst.DeductType.FIX.code, remaining > fixLimit ? fixLimit : remaining};

            } else if (lastDeductFlow.getStatus() == DeductConst.Flow.FAIL.code) {
                // 如果上次扣款失败， 那么就不用扣了

                return new double[]{DeductConst.DeductType.FIX.code, 0};
            }

        } else if (deductModel.getDeductType() == DeductConst.DeductType.RATE.code) {
            // 按照比例进行扣款的。

            Integer[] fixLimit = StringUtil.split2Int(deductModel.getDeductConfig());

            int limitRate = 0;

            if (fixLimit.length != 2) {
                return new double[]{-1, 0};
            }

            if (lastDeductFlow == null) {
                // 表示第一次扣款
                limitRate = fixLimit[0];
            } else {
                if (lastDeductFlow.getDeductLevel().equals(fixLimit[0])) {

                    if (lastDeductFlow.getStatus() == DeductConst.Flow.SUCCESS.code) {
                        limitRate = fixLimit[0];
                    } else if (lastDeductFlow.getStatus() == DeductConst.Flow.FAIL.code) {
                        limitRate = fixLimit[1];
                    }

                } else if (lastDeductFlow.getDeductLevel().equals(fixLimit[1])) {
                    // 如果到最后一档，不管是成功还是失败，都不扣了
                    if (lastDeductFlow.getStatus() == DeductConst.Flow.SUCCESS.code
                            || lastDeductFlow.getStatus() == DeductConst.Flow.FAIL.code) {
                        // 扣款成功。
                        limitRate = 0;
                    }
                }
            }
            return new double[]{DeductConst.DeductType.RATE.code, limitRate};
        }

        return new double[]{-2, 0};
    }

}
