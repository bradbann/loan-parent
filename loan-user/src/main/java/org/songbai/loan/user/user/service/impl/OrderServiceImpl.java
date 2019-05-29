package org.songbai.loan.user.user.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.common.helper.StatisSendHelper;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.common.util.PlatformKit;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.resp.UserRespCode;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.constant.user.UserConstant;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.loan.OrderOptModel;
import org.songbai.loan.model.loan.ProductModel;
import org.songbai.loan.model.statistic.dto.UserStatisticDTO;
import org.songbai.loan.model.user.AuthenticationModel;
import org.songbai.loan.model.user.UserBankCardModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.user.appConfig.dao.AppVestDao;
import org.songbai.loan.user.user.dao.AdminVestDao;
import org.songbai.loan.user.user.dao.AuthenticationDao;
import org.songbai.loan.user.user.dao.OrderDao;
import org.songbai.loan.user.user.dao.OrderOptDao;
import org.songbai.loan.user.user.helper.OrderHelper;
import org.songbai.loan.user.user.model.vo.LoanDetail;
import org.songbai.loan.user.user.model.vo.OrderDetailVO;
import org.songbai.loan.user.user.model.vo.OrderListVO;
import org.songbai.loan.user.user.model.vo.OrderProgressVO;
import org.songbai.loan.user.user.service.OrderService;
import org.songbai.loan.vo.risk.RiskOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.songbai.loan.constant.user.OrderConstant.Stage.REPAYMENT;
import static org.songbai.loan.constant.user.OrderConstant.Status.FAIL;
import static org.songbai.loan.constant.user.UserConstant.Status.BLACK_LIST;
import static org.songbai.loan.constant.user.UserConstant.Status.GREY_LIST;

/**
 * Author: qmw
 * Date: 2018/10/31 4:24 PM
 */
@Service
public class OrderServiceImpl implements OrderService {
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Autowired
    private AuthenticationDao authenticationDao;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private OrderHelper orderHelper;
    @Autowired
    private OrderOptDao orderOptDao;
    @Autowired
    private SpringProperties properties;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private StatisSendHelper statisSendHelper;
    @Autowired
    private AdminVestDao vestDao;


    @Override
    public void loan(Integer userId, Double loan) {
        // 1校验黑名单
        UserModel userModel = comUserService.selectUserModelById(userId, 0);
        if (userModel == null) {
            throw new BusinessException(UserRespCode.AUTH_NOT_EXIST);
        }
        // 1校验黑灰名单
        if (BLACK_LIST.key == userModel.getStatus() || GREY_LIST.key == userModel.getStatus()) {
            throw new BusinessException(UserRespCode.CREDIT_NOT_DISSATISFACTION);
        }
        //int guest = NEW_GUEST.key;
        // 2校验用户的认证状态
        AuthenticationModel authModel = authenticationDao.findUserAuthenticationByUserId(userId);
        if (authModel == null) {
            throw new BusinessException(UserRespCode.AUTHENTICATION_NOT_COMPLETE);
        }
        if (authModel.getStatus() == CommonConst.DELETED_NO) {
            throw new BusinessException(UserRespCode.AUTHENTICATION_NOT_COMPLETE);
        }

        // 3校验用户是否有未完成的订单
        OrderModel dbOrderModel = orderDao.finRecentOrderByUserId(userId);
        if (dbOrderModel != null) {
            if (REPAYMENT.key != dbOrderModel.getStage()) {
                if (FAIL.key != dbOrderModel.getStatus()) {
                    throw new BusinessException(UserRespCode.ORDER_NOT_COMPLETE);
                }
                //guest = SECOND_GUEST.key;
            } else {
                List<Integer> succ = Arrays.asList(2, 5, 6, 7, 8);
                if (!succ.contains(dbOrderModel.getStatus())) {
                    throw new BusinessException(UserRespCode.ORDER_NOT_COMPLETE);
                }
                //guest = OLD_GUEST.key;
            }

            if (dbOrderModel.getAgainDate() != null) {
                LocalDateTime againDate = Date8Util.date2LocalDateTime(dbOrderModel.getAgainDate());
                if (againDate.isAfter(LocalDateTime.now())) {

                    throw new BusinessException(UserRespCode.ORDER_NOT_COMPLETE, LocaleKit.get("msg.2406", Date8Util.LocalDateTime2SimpleString(againDate.minusDays(1))));
                }
            }
            if (dbOrderModel.getStage() == OrderConstant.Stage.MACHINE_AUTH.key || dbOrderModel.getStage() == OrderConstant.Stage.ARTIFICIAL_AUTH.key) {
                if (dbOrderModel.getStatus() == OrderConstant.Status.FAIL.key) {
                    // 4用户上次订单审核失败的时间
                    OrderOptModel optModel = orderOptDao.findAuthFailModel(userId, dbOrderModel.getOrderNumber());
                    if (optModel != null) {

                        LocalDate current = LocalDate.now();
                        Integer failDays = properties.getInteger("user.auth.fail.days", 7);
                        LocalDate repayLocalDate = Date8Util.date2LocalDate(optModel.getCreateTime()).plusDays(failDays);

                        Period between = Period.between(current, repayLocalDate);

                        if (between.getDays() > 0) {
                            throw new BusinessException(UserRespCode.USER_FAIL_DAYS_LIMIT, LocaleKit.get("msg.2406", repayLocalDate.toString()));
                        }
                    }
                }
            }
        }
        AppVestModel vest = vestDao.selectById(userModel.getVestId());

        if (vest == null || vest.getStatus() == CommonConst.STATUS_INVALID) {
            throw new BusinessException(UserRespCode.PRODUCT_VEST_NOT_START);
        }

        // 5查询标的
        ProductModel productModel = orderHelper.gettingUserAvailableProduct(loan, userId, userModel.getAgencyId(), vest);

        if (productModel == null) {
            throw new BusinessException(UserRespCode.LOAN_NOT_EXIST);
        }

        OrderModel orderModel = new OrderModel();
        // 6查询用户绑定的银行卡
        UserBankCardModel bankCard = orderHelper.gettingUserDefaultBindBankCard(userId);

        if (bankCard == null) {
            throw new BusinessException(UserRespCode.BANK_CARD_HAS_NOT_DEFAULT);
        }

        orderModel.setBankId(bankCard.getId());
        orderModel.setAgencyId(userModel.getAgencyId());
        orderModel.setUserId(userModel.getId());
        orderModel.setProductId(productModel.getId());

        orderModel.setDays(productModel.getDays());
        orderModel.setLoan(productModel.getLoan());
        orderModel.setStampTax(productModel.getStamp());
        orderModel.setObtain(productModel.getPay());
        orderModel.setGuest(userModel.getGuest());
        orderModel.setPayment(productModel.getLoan());
        orderModel.setOrderNumber(OrderIdUtil.getLoanId());
        orderModel.setCreateTime(new Date());
        orderModel.setGroupId(vest.getGroupId());
        orderDao.insert(orderModel);


        RiskOrderVO orderVO = new RiskOrderVO();
        orderVO.setThridId(userModel.getThirdId());
        orderVO.setOrderNumber(orderModel.getOrderNumber());
        jmsTemplate.convertAndSend(JmsDest.RISK_ORDER_MOULD, orderVO);

        UserStatisticDTO dto = new UserStatisticDTO();
        dto.setRegisterDate(Date8Util.date2LocalDate(userModel.getCreateTime()));
        dto.setAgencyId(userModel.getAgencyId());
        dto.setChannelCode(userModel.getChannelCode());
        dto.setActionDate(LocalDate.now());
        dto.setVestId(userModel.getVestId());
        if (userModel.getGuest() == OrderConstant.Guest.NEW_GUEST.key) {
            dto.setIsNew(CommonConst.YES);
        } else {
            dto.setIsOld(CommonConst.YES);
        }

        jmsTemplate.convertAndSend(JmsDest.USER_STATISTIC, dto);
        logger.info(">>>>发送统计,用户行为(提单)jms ,data={}", dto);

        //信审统计
        statisSendHelper.sendReviewStatis(orderModel, userModel.getVestId(), OrderConstant.Stage.MACHINE_AUTH.key, OrderConstant.Status.WAIT.key, userModel.getChannelCode());
    }

    @Override
    public Page<OrderListVO> orderList(Integer userId, PageRow pageRow) {
        int count = orderDao.selectOrderCount(userId);
        if (count <= 0) {
            return new Page<>(pageRow.getPage(), pageRow.getPageSize(), 0, new ArrayList<>());
        }
        List<OrderListVO> list = orderDao.selectOrderList(userId, pageRow);
        orderHelper.setOrderAppStatus(list);


        return new Page<>(pageRow.getPage(), pageRow.getPageSize(), count, list);
    }

    @Override
    public Ret orderDetailByOrderNumber(Integer userId, String orderNumber) {
        OrderModel orderModel = orderDao.selectOrderByOrderNumberAndUserId(orderNumber, userId);
        if (orderModel == null) {
            throw new BusinessException(UserRespCode.ORDER_NOT_EXIST);
        }
        OrderDetailVO detailVO = new OrderDetailVO();

        List<OrderProgressVO> progress = orderHelper.spliceProgressByOrder(orderModel, detailVO);
        Ret ret = Ret.create();
        ret.put("progress", progress);
        ret.put("orderNumber", orderNumber);

        ret.put("detail", detailVO);
        return ret;
    }

    @Override
    public LoanDetail loanDetail(Integer userId, Integer agencyId, Double loan) {

        UserModel userModel = comUserService.selectUserModelById(userId);
        if (userModel == null) {
            throw new BusinessException(UserRespCode.AUTH_NOT_EXIST);
        }

        AppVestModel vest = vestDao.selectById(userModel.getVestId());

        if (vest == null || vest.getStatus() == CommonConst.STATUS_INVALID) {
            throw new BusinessException(UserRespCode.PRODUCT_VEST_NOT_START);
        }


        ProductModel productModel = orderHelper.gettingUserAvailableProduct(loan, userId, agencyId, vest);

        if (productModel == null) {
            throw new BusinessException(UserRespCode.LOAN_NOT_EXIST);
        }

        UserBankCardModel bankCard = orderHelper.gettingUserDefaultBindBankCard(userId);
        if (bankCard == null) {
            throw new BusinessException(UserRespCode.BANK_CARD_HAS_NOT_DEFAULT);
        }

        LoanDetail vo = new LoanDetail();
        vo.setLoan(FormatUtil.formatDouble2(productModel.getLoan()));
        vo.setStampTax(FormatUtil.formatDouble2(productModel.getStamp()));
        vo.setObtain(FormatUtil.formatDouble2(productModel.getPay()));
        vo.setDays(productModel.getDays());
        vo.setBankName(bankCard.getBankName());
        vo.setBankNumber(bankCard.getBankCardNum().substring(bankCard.getBankCardNum().length() - 4));
        return vo;
    }

    @Override
    public Ret loanHome(Integer userId, HttpServletRequest request) {
        Ret ret = Ret.create();
        if (userId == null) {
            ret.put("type", 0); // 用户没登录/未全部认证通过
            ret.put("guest", OrderConstant.Guest.NEW_GUEST.key);
            return ret;
        }
        UserModel userModel = comUserService.selectUserModelById(userId);
        if (userModel == null) {
            throw new BusinessException(UserRespCode.AUTH_NOT_EXIST);
        }

        AppVestModel vest = vestDao.selectById(userModel.getVestId());

        String url = null;
        if (vest != null) {
            url = vest.getRefuseJumpUrl();
        }
        ret.put("guest", userModel.getGuest());

        // 1校验黑灰名单
        if (BLACK_LIST.key == userModel.getStatus() || GREY_LIST.key == userModel.getStatus()) { // 黑名单 显示初审通过,查看详情跳转贷超
            ret.remove("limit");
            ret.put("type", 2); // 用户黑名单/灰名单
            if (StringUtil.isNotEmpty(url)) {
                ret.put("url", url);
            }
            return ret;
        }
        AuthenticationModel authModel = authenticationDao.findUserAuthenticationByUserId(userId);
        if (authModel == null || authModel.getStatus() == CommonConst.NO) {
            ret.put("type", 0); // 用户没登录/未全部认证通过
            return ret;
        }

        if (vest == null) {
            throw new BusinessException(UserRespCode.PRODUCT_VEST_NOT_START);
        }

        List<Double> loans = orderHelper.gettingUserAvailableLoans(userId, vest);

        OrderModel dbOrderModel = orderDao.finRecentOrderByUserId(userId);

        if (dbOrderModel == null) {
            if (loans.isEmpty()) {
                throw new BusinessException(UserRespCode.LOAN_NOT_EXIST);
            }
            ret.put("type", 1);
            ret.put("amount", 1000D);//最初版本用
            ret.put("loans", loans);
            return ret;
        }

        ret.put("orderNumber", dbOrderModel.getOrderNumber());
        LocalDate current = LocalDate.now();
        if (dbOrderModel.getStage() == OrderConstant.Stage.REPAYMENT.key) {
            List<Integer> succStatus = Arrays.asList(2, 5, 6, 7);
            if (succStatus.contains(dbOrderModel.getStatus())) {
                if (loans.isEmpty()) {
                    throw new BusinessException(UserRespCode.LOAN_NOT_EXIST);
                }
                ret.put("amount", 1000D);//最初版本用
                ret.put("type", 1);
                ret.put("loans", loans);
                return ret;
            }
            if (OrderConstant.Status.WAIT.key == dbOrderModel.getStatus() || OrderConstant.Status.PROCESSING.key == dbOrderModel.getStatus()) {
                LocalDate repayLocalDate = Date8Util.date2LocalDate(dbOrderModel.getRepaymentDate());
                Period between = Period.between(current, repayLocalDate);
                if (between.getDays() >= 0) {
                    ret.put("type", 4); // 正常还款状态
                    ret.put("payment", FormatUtil.formatDouble2(dbOrderModel.getPayment() - dbOrderModel.getAlreadyMoney()));//待还金额
                    ret.put("repaymentDate", dbOrderModel.getRepaymentDate());//最迟还款日
                    ret.put("orderNumber", dbOrderModel.getOrderNumber());//地单号

                    ret.put("arrivalTime", between.getDays());//还剩几天
                    return ret;
                }
                ret.put("type", 5); // 逾期状态
                ret.put("payment", FormatUtil.formatDouble2(dbOrderModel.getPayment() - dbOrderModel.getAlreadyMoney()));//待还金额
                ret.put("exceedFee", FormatUtil.formatDouble2(dbOrderModel.getExceedFee()));//逾期费用
                ret.put("exceedDays", dbOrderModel.getExceedDays());//逾期天数
                ret.put("orderNumber", dbOrderModel.getOrderNumber());//地单号
                return ret;
            }


            ret.put("type", 5); // 逾期状态
            ret.put("payment", FormatUtil.formatDouble2(dbOrderModel.getPayment() - dbOrderModel.getAlreadyMoney()));//待还金额
            ret.put("exceedFee", FormatUtil.formatDouble2(dbOrderModel.getExceedFee()));//逾期费用
            ret.put("exceedDays", dbOrderModel.getExceedDays());//逾期天数
            ret.put("orderNumber", dbOrderModel.getOrderNumber());//地单号
            return ret;

        } else {

            boolean isUrl = true;// 是否显示订单详情
            ret.put("type", 3); // 未放款前状态

            if (dbOrderModel.getStage() == OrderConstant.Stage.LOAN.key) {
                if (dbOrderModel.getStatus() == OrderConstant.Status.FAIL.key) {
                    if (dbOrderModel.getAgainDate() != null) {
                        LocalDateTime againDate = Date8Util.date2LocalDateTime(dbOrderModel.getAgainDate());
                        if (againDate.isBefore(LocalDateTime.now())) {
                            if (loans.isEmpty()) {
                                throw new BusinessException(UserRespCode.LOAN_NOT_EXIST);
                            }
                            ret.put("loans", loans);
                            ret.put("type", 1);
                            ret.put("amount", 1000D);//最初版本用
                            return ret;
                        }
                    }
                } else {
                    isUrl = false;
                }

            } else if (dbOrderModel.getStage() == OrderConstant.Stage.MACHINE_AUTH.key || dbOrderModel.getStage() == OrderConstant.Stage.ARTIFICIAL_AUTH.key) {
                //审核失败的要校验时间
                if (dbOrderModel.getStatus() == OrderConstant.Status.FAIL.key) {
                    OrderOptModel optModel = orderOptDao.findAuthFailModel(userId, dbOrderModel.getOrderNumber());
                    if (optModel != null) {
                        Integer failDays = properties.getInteger("user.auth.fail.days", 7);
                        LocalDate repayLocalDate = Date8Util.date2LocalDate(optModel.getCreateTime()).plusDays(failDays);
                        Period between = Period.between(current, repayLocalDate);
                        if (between.getDays() <= 0) {
                            ret.put("type", 1);
                            ret.put("amount", 1000D);//最初版本用
                            ret.put("loans", loans);
                            return ret;
                        } else {
                            ret.put("type", 2); // 用户黑名单/灰名单
                            ret.remove("orderNumber");
                            if (StringUtil.isNotEmpty(url)) {
                                ret.put("url", url);
                            }
                            return ret;
                        }
                    }
                } else {
                    isUrl = false;
                }
            }

            OrderDetailVO detailVO = new OrderDetailVO();
            List<OrderProgressVO> progress = orderHelper.spliceProgressByOrder(dbOrderModel, detailVO);
            ret.put("progress", progress);
            if (isUrl && StringUtil.isNotEmpty(url)) {
                ret.put("url", url);
            }
        }
        return ret;
    }

}
