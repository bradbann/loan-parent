package org.songbai.loan.user.user.helper;

import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.FinanceConstant;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.finance.FinanceIOModel;
import org.songbai.loan.model.loan.*;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.user.appConfig.dao.AppVestDao;
import org.songbai.loan.user.finance.dao.FinanceIODao;
import org.songbai.loan.user.finance.dao.RepaymentFlowDao;
import org.songbai.loan.user.user.dao.*;
import org.songbai.loan.user.user.model.vo.OrderDetailVO;
import org.songbai.loan.user.user.model.vo.OrderListVO;
import org.songbai.loan.user.user.model.vo.OrderProgressVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.songbai.loan.constant.user.OrderConstant.Stage.*;
import static org.songbai.loan.constant.user.OrderConstant.Status.*;

/**
 * Author: qmw
 * Date: 2018/10/31 4:37 PM
 */
@Component
public class OrderHelper {
    @Autowired
    private OrderOptDao orderOptDao;
    @Autowired
    private UserBankCardDao userBankCardDao;
    @Autowired
    private FinanceIODao financeIODao;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ProductDao productDao;
    @Autowired
    private ProductGroupDao productGroupDao;
    @Autowired
    private RepaymentFlowDao repaymentFlowDao;
    @Autowired
    private AdminVestDao vestDao;

    public LinkedList<OrderProgressVO> spliceProgressByOrder(OrderModel orderModel, OrderDetailVO detailVO) {
        LinkedList<OrderProgressVO> vos = new LinkedList<>();
        OrderProgressVO vo = new OrderProgressVO();

        vo.setTitle("申请提交成功");
        StringBuilder sb = new StringBuilder(100);
        sb.append("申请周转资金");
        sb.append(FormatUtil.formatDouble2(orderModel.getLoan()));
        sb.append("元，周期");
        sb.append(orderModel.getDays());
        sb.append("天，服务费");
        sb.append(FormatUtil.formatDouble2(orderModel.getStampTax()));
        sb.append("元，到账");
        sb.append(FormatUtil.formatDouble2(orderModel.getObtain()));
        sb.append("元");
        vo.setContent(sb.toString());
        vo.setTime(orderModel.getCreateTime());

        vos.addLast(vo);
        if (orderModel.getExceedDays() > 0 && orderModel.getExceedFee() > 0D) {
            detailVO.setExceedFee(FormatUtil.formatDouble2(orderModel.getExceedFee()));
        }
        detailVO.setLoan(FormatUtil.formatDouble2(orderModel.getLoan()));
        detailVO.setStampTax(FormatUtil.formatDouble2(orderModel.getStampTax()));
        detailVO.setObtain(FormatUtil.formatDouble2(orderModel.getObtain()));
        double payment = orderModel.getPayment();
        detailVO.setPayment(FormatUtil.formatDouble2(payment));
        detailVO.setDays(orderModel.getDays());
        detailVO.setRepaymentDate(orderModel.getRepaymentDate());
        detailVO.setExceedFee(FormatUtil.formatDouble2(orderModel.getExceedFee()));
        detailVO.setExceedDays(orderModel.getExceedDays());
        detailVO.setRepaymentTime(orderModel.getRepaymentTime());

        UserBankCardModel userBank = userBankCardDao.selectById(orderModel.getBankId());

        detailVO.setBankName(userBank.getBankName());
        if (userBank.getBankCardNum().length() > 4) {
            String bankNumber = userBank.getBankCardNum().substring(userBank.getBankCardNum().length() - 4);
            detailVO.setBankNumber(bankNumber);
        }

        sb.delete(0, sb.capacity());

        int status = 1;//审核中

        vo = new OrderProgressVO();
        vo.setTitle("审核中");
        vo.setContent("您的订单正在快速审核，请耐心等待");
        vo.setTime(orderModel.getCreateTime());
        vos.addLast(vo);
        detailVO.setStatus(status);
        boolean isFinality = false;

        if (MACHINE_AUTH.key <= orderModel.getStage()) {
            if (MACHINE_AUTH.key == orderModel.getStage() && WAIT.key == orderModel.getStatus()) {
                return vos;
            }
            OrderOptModel opt = orderOptDao.findOrderOptByStageAndStatus(orderModel.getOrderNumber(), MACHINE_AUTH.key);
            if (opt == null) {
                return vos;
            }
            if (PROCESSING.key == opt.getStatus()) {
                vos.removeLast();
                vo = new OrderProgressVO();
                vo.setTime(opt.getCreateTime());
                vo.setTitle("审核通过");
                status = 3;//放款中
                vo.setContent("恭喜您通过审核");
                vos.addLast(vo);

                vo = new OrderProgressVO();
                vo.setTime(opt.getCreateTime());
                vo.setTitle("放款中");
                vo.setContent("已进入放款状态，请您耐心等待");
                vos.addLast(vo);
            } else if (FAIL.key == opt.getStatus()) {
                vos.removeLast();
                vo = new OrderProgressVO();
                vo.setTime(opt.getCreateTime());
                vo.setTitle("审核失败");
                status = 2;//放款中

                vo.setContent("您的信用评级未达到平台要求");
                vos.addLast(vo);
                isFinality = true;
            }
        }
        detailVO.setStatus(status);
        if (isFinality) return vos;

        if (ARTIFICIAL_AUTH.key <= orderModel.getStage()) {
            if (ARTIFICIAL_AUTH.key == orderModel.getStage() && WAIT.key == orderModel.getStatus()) {
                return vos;
            }
            OrderOptModel opt = orderOptDao.findOrderOptByStageAndStatus(orderModel.getOrderNumber(), ARTIFICIAL_AUTH.key);
            if (opt == null) {
                return vos;
            }
            vos.removeLast();
            if (SUCCESS.key == opt.getStatus()) {

                vo = new OrderProgressVO();
                vo.setTime(opt.getCreateTime());
                vo.setTitle("审核通过");
                status = 3;//放款中

                vo.setContent("恭喜您通过审核");
                vos.addLast(vo);

                vo = new OrderProgressVO();
                vo.setTime(opt.getCreateTime());
                vo.setTitle("放款中");
                vo.setContent("已进入放款状态，请您耐心等待");
                vos.addLast(vo);
            } else {
                vo = new OrderProgressVO();
                vo.setTime(opt.getCreateTime());
                vo.setTitle("审核失败");
                status = 2;//放款中
                vo.setContent("您的信用评级未达到平台要求");
                vos.addLast(vo);
                isFinality = true;
            }
        }
        detailVO.setStatus(status);
        if (isFinality) return vos;


        if (LOAN.key <= orderModel.getStage()) {
            if (LOAN.key == orderModel.getStage()) {
                if (WAIT.key == orderModel.getStatus() || EXCEPTION.key == orderModel.getStatus() || PROCESSING.key == orderModel.getStatus()) {
                    return vos;
                }
            }

            OrderOptModel opt = orderOptDao.findOrderOptByStageAndStatus(orderModel.getOrderNumber(), LOAN.key);
            if (opt == null) {
                return vos;
            }
            vos.removeLast();
            if (SUCCESS.key == opt.getStatus()) {

                vo = new OrderProgressVO();
                vo.setTime(orderModel.getTransferTime());
                vo.setTitle("打款成功");
                status = 5;//待还款
                sb.append("打款至");
                sb.append(detailVO.getBankName());
                sb.append("(尾号");
                sb.append(detailVO.getBankNumber());
                sb.append(")");
                vo.setContent(sb.toString());

                vos.addLast(vo);
                sb.delete(0, sb.capacity());

            } else if (FAIL.key == opt.getStatus()) {
                vo = new OrderProgressVO();
                vo.setTime(opt.getCreateTime());
                vo.setTitle("拒绝放款");
                status = 4;//打款失败
                vo.setContent(opt.getRemark());
                vos.addLast(vo);
                isFinality = true;

            }
        }
        detailVO.setStatus(status);
        if (isFinality) return vos;

        //最终的
        if (orderModel.getStatus() == PROCESSING.key) {
            vo = new OrderProgressVO();
            FinanceIOModel ioModel = financeIODao.findFinanceIoByOrderNumber(orderModel.getOrderNumber());
            if (ioModel != null) {
                vo.setTime(ioModel.getCreateTime());
            } else {
                vo.setTime(new Date());
            }
            vo.setTitle("还款中");
            vo.setContent("您的还款正在处理中，请稍后");
            status = 5;//待还款
            vos.addLast(vo);
            isFinality = true;
        }
        detailVO.setStatus(status);
        if (isFinality) return vos;

        LocalDate current = LocalDate.now();
        if (orderModel.getStatus() == WAIT.key) {// 等待
            LocalDate repayLocalDate = Date8Util.date2LocalDate(orderModel.getRepaymentDate());
            Period between = Period.between(current, repayLocalDate);
            int days = between.getDays();
            if (days == 0) {
                sb.append("今日还款");
            } else if (days == 1) {
                sb.append("明日还款");
            } else if (days > 1) {
                sb.append("待还款");
            } else {
                sb.append("还款中");
            }
            vo = new OrderProgressVO();
            vo.setTime(orderModel.getTransferTime());
            vo.setTitle(sb.toString());
            status = 5;//待还款
            sb.delete(0, sb.capacity());

            sb.append("为避免对您的信用产生影响，请务必于");
            sb.append(repayLocalDate.toString());
            sb.append(" 23:59:59");
            sb.append("前还款");
            vo.setContent(sb.toString());

            sb.delete(0, sb.capacity());
            vos.addLast(vo);
            isFinality = true;
        }
        detailVO.setStatus(status);
        if (isFinality) return vos;

        OrderOptModel opt = orderOptDao.findOrderOptByStageAndStatus(orderModel.getOrderNumber(), REPAYMENT.key);
        if (opt == null) return vos;

        if (orderModel.getStatus() == OVERDUE.key || orderModel.getStatus() == FAIL.key) {// 等待
            vo = new OrderProgressVO();
            vo.setTime(opt.getCreateTime());
            vo.setTitle("逾期");
            status = 6;//逾期中
            sb.append("您已逾期");
            sb.append(orderModel.getExceedDays());
            sb.append("天");
            sb.append("，逾期费用");
            sb.append(FormatUtil.formatDouble2(orderModel.getExceedFee()));
            sb.append("元，请尽快还款。否则我司将根据合同和声明中的规定上门催收和有权力将此次贷款消息告知其亲戚和朋友。");
            vo.setContent(sb.toString());
            sb.delete(0, sb.capacity());
            vos.addLast(vo);
        } else {
            vo = new OrderProgressVO();
            vo.setTime(orderModel.getRepaymentTime());

            detailVO.setRepaymentDate(orderModel.getRepaymentTime());
            vo.setContent("恭喜您,还款成功");
            if (orderModel.getStatus() == OVERDUE_LOAN.key || orderModel.getStatus() == CHASE_LOAN.key) {
                status = 7;//逾期中
                vo.setTitle("逾期还款");
            } else if (orderModel.getStatus() == SUCCESS.key || orderModel.getStatus() == ADVANCE_LOAN.key) {
                vo.setTitle("已还款");
                status = 8;//逾期中
            }
            vos.addLast(vo);

            RepaymentFlowModel flowModel = repaymentFlowDao.findFlowByOrderNumber(orderModel.getOrderNumber());

            if (flowModel != null) {
                if (flowModel.getType() == FinanceConstant.FlowType.ONLINE.type ||flowModel.getType() == FinanceConstant.FlowType.DEDUCT.type ) {//线上还款
                    if (flowModel.getRepayType().equals(OrderConstant.RepayType.ALIPAY.value)) {
                        detailVO.setBankNumber("--");
                        detailVO.setBankName("支付宝");
                    } else if (flowModel.getRepayType().equals(OrderConstant.RepayType.WEIXIN.value)) {
                        detailVO.setBankNumber("--");
                        detailVO.setBankName("微信");
                    }else {
                        detailVO.setBankNumber("--");
                        detailVO.setBankName(flowModel.getBankName());
                        detailVO.setBankNumber("--");
                        if(StringUtil.isNotEmpty(flowModel.getBankNumber())){
                            String bankNumber = flowModel.getBankNumber().substring(flowModel.getBankNumber().length() - 4);
                            detailVO.setBankNumber(bankNumber);
                        }
                    }

                } else {
                    detailVO.setBankNumber("--");
                    if (flowModel.getRepayType().equals(OrderConstant.RepayType.ALIPAY.value)) {
                        detailVO.setBankName("支付宝");
                    } else if (flowModel.getRepayType().equals(OrderConstant.RepayType.WEIXIN.value)) {
                        detailVO.setBankName("微信");
                    } else {
                        detailVO.setBankName("线下转账");
                    }
                }
            }
        }
        detailVO.setStatus(status);
        return vos;
    }

    /**
     * 设置app前台订单列表显示属性
     *
     * @param list
     */
    public void setOrderAppStatus(List<OrderListVO> list) {
        for (OrderListVO vo : list) {
            Integer stage = vo.getStage();
            Integer status = vo.getStatus();
            String statusName = null;
            if (stage == MACHINE_AUTH.key || stage == ARTIFICIAL_AUTH.key) {
                if (status == OrderConstant.Status.WAIT.key) {
                    status = 1;//审核中
                    statusName = "审核中";

                } else if (status == OrderConstant.Status.FAIL.key) {
                    status = 2;//审核失败
                    statusName = "审核失败";
                }
            } else if (stage == LOAN.key) {
                if (status == OrderConstant.Status.WAIT.key || status == OrderConstant.Status.PROCESSING.key || status == OrderConstant.Status.EXCEPTION.key) {
                    status = 3;//放款中
                    statusName = "放款中";

                } else if (status == OrderConstant.Status.FAIL.key) {
                    status = 4;//放款失败
                    statusName = "拒绝放款";
                }
            } else if (stage == REPAYMENT.key) {
                if (status == OrderConstant.Status.PROCESSING.key) {
                    status = 9;//正在还款
                    statusName = "还款中";
                } else if (status == OrderConstant.Status.WAIT.key) {
                    status = 5;//放款成功，待还款
                    statusName = "待还款";
                } else if (status == OrderConstant.Status.OVERDUE.key || status == OrderConstant.Status.FAIL.key) {
                    status = 6;//已逾期
                    statusName = "已逾期";
                } else if (status == OrderConstant.Status.SUCCESS.key || status == OrderConstant.Status.ADVANCE_LOAN.key) {
                    status = 8;//已完成
                    statusName = "已完成";
                } else {
                    status = 7;//逾期还款
                    statusName = "逾期还款";
                }
            }
            vo.setStatus(status);
            vo.setStatusName(statusName);
            vo.setStage(null);
            vo.setPayment(null);
        }
    }

    /**
     * 获取用户可用借款集合
     */
    public List<Double> gettingUserAvailableLoans(Integer userId, AppVestModel vest) {
        ProductGroupModel groupModel = productGroupDao.findProductGroupById(vest.getGroupId(), CommonConst.YES);
        if (groupModel == null) {
            throw new BusinessException(UserRespCode.PRODUCT_VEST_NOT_START);
        }

        int count = orderDao.findUserLoanSuccessLoanOrder(userId);
        // 1 查询用户最近的一笔订单
        OrderModel dbOrderModel = orderDao.finRecentCompleteOrderByUserId(userId);

        Integer overdueDay = null;
        Integer repayCount = count >= 0 ? count : null;
        if (dbOrderModel != null) {
            if (dbOrderModel.getExceedDays() > 0) {
                overdueDay = dbOrderModel.getExceedDays();
            }
        }
        return productDao.findUserUseProduct(groupModel.getId(), repayCount, overdueDay);
    }

    /**
     * 获取用户可用的标的
     */
    public ProductModel gettingUserAvailableProduct(Double loan, Integer userId, Integer agencyId, AppVestModel vest) {
        ProductModel productModel;

        if (loan == null) {//兼容最初版本,不传则去查询默认的标的
            productModel = productDao.findProductByAgencyId(agencyId);
        } else {
            List<Double> loans = gettingUserAvailableLoans(userId, vest);
            if (loans.isEmpty()) {
                throw new BusinessException(UserRespCode.LOAN_NOT_EXIST);
            }
            if (!loans.contains(loan)) {
                throw new BusinessException(UserRespCode.LOAN_NOT_EXIST);
            }
            productModel = productDao.findProductByAgencyIdAndLoan(agencyId, loan);
        }
        return productModel;

    }

    /**
     * 获取用户默认银行卡
     */
    public UserBankCardModel gettingUserDefaultBindBankCard(Integer userId) {
        UserBankCardModel bankCard = null;
        List<UserBankCardModel> bankCards = userBankCardDao.selectUserBankListByUserIdStatus(userId, CommonConst.YES);
        if (bankCards.isEmpty()) {
            throw new BusinessException(UserRespCode.BANK_CARD_NOT_EXIST);
        }

        for (UserBankCardModel card : bankCards) {
            if (card.getType() == CommonConst.YES) {
                bankCard = card;
                break;
            }
        }
        if (bankCard == null) {
            throw new BusinessException(UserRespCode.BANK_CARD_HAS_NOT_DEFAULT);
        }
        return bankCard;
    }
}
