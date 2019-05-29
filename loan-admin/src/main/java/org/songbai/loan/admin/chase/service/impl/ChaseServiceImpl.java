package org.songbai.loan.admin.chase.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.lock.DistributeLock;
import org.songbai.cloud.basics.lock.DistributeLockFactory;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.excel.ExcelNewHelper;
import org.songbai.cloud.basics.utils.excel.ExcelWriteBuilder;
import org.songbai.loan.admin.admin.dao.AdminActorDao;
import org.songbai.loan.admin.admin.dao.AdminDeptDao;
import org.songbai.loan.admin.admin.model.AdminDeptModel;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.service.AdminDeptService;
import org.songbai.loan.admin.admin.service.AdminUserService;
import org.songbai.loan.admin.chase.dao.ChaseDebtDao;
import org.songbai.loan.admin.chase.dao.ChaseFeedDao;
import org.songbai.loan.admin.chase.po.ChaseDebtPo;
import org.songbai.loan.admin.chase.service.ChaseService;
import org.songbai.loan.admin.chase.vo.ChaseExcelVo;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.admin.order.dao.OrderOptDao;
import org.songbai.loan.admin.order.vo.OrderPageVo;
import org.songbai.loan.common.helper.OrderIdUtil;
import org.songbai.loan.common.util.Date8Util;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.JmsDest;
import org.songbai.loan.constant.agency.AgencyDeptConstant.DeptType;
import org.songbai.loan.constant.lock.ZKLockConst;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.loan.OrderOptModel;
import org.songbai.loan.model.statistic.dto.RepayStatisticDTO;
import org.songbai.loan.model.user.UserContactModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.model.version.AppVestModel;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static org.songbai.loan.constant.resp.UserRespCode.ORDER_NOT_EXIST;

@Service
public class ChaseServiceImpl implements ChaseService {
    private static final Logger logger = LoggerFactory.getLogger(ChaseService.class);
    @Autowired
    ChaseDebtDao chaseDebtDao;
    @Autowired
    OrderDao orderDao;
    @Autowired
    ChaseFeedDao chaseFeedDao;
    @Autowired
    ExcelNewHelper excelNewHelper;
    @Autowired
    ComUserService comUserService;
    @Autowired
    AdminDeptService adminDeptService;
    @Autowired
    AdminUserService adminUserService;
    @Autowired
    private DistributeLockFactory lockFactory;
    @Autowired
    private OrderOptDao orderOptDao;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private DataSourceTransactionManager transactionManager;
    @Autowired
    private AdminDeptDao adminDeptDao;
    @Autowired
    private AdminActorDao adminActorDao;
    @Autowired
    private ComAgencyService comAgencyService;

    @Override
    public Page<OrderPageVo> getChasePage(ChaseDebtPo po) {
        Integer count = chaseDebtDao.queryChaseCount(po);
        if (count == 0) {
            return new Page<>(po.getPage(), po.getPageSize(), count, new ArrayList<>());
        }
        List<OrderPageVo> list = chaseDebtDao.queryChasePageList(po);
        List<OrderPageVo> result = new ArrayList<>();
        for (OrderPageVo vo : list) {
            if (vo.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(vo.getVestId());
                if (vestModel != null) vo.setVestName(vestModel.getName());
            }
            result.add(vo.change(vo));
        }
        return new Page<>(po.getPage(), po.getPageSize(), count, result);
    }

    @Override
    @Transactional
    public void seperateOrder(String orderNumbers, Integer deptId, Integer actorId) {
        String[] orderNumber = orderNumbers.split(",");
        List<OrderModel> list = orderDao.findOrderListByOrderNumbs(orderNumber);

        AdminDeptModel adminDeptModel = adminDeptDao.selectById(deptId);

        String deptName = null;

        if (adminDeptModel != null) {
            deptName = adminDeptModel.getName();

        }

        String finalDeptName = deptName;
        list.forEach(model -> {
            if (!model.getStage().equals(OrderConstant.Stage.REPAYMENT.key)) {
                throw new BusinessException(AdminRespCode.ORDER_STATUS_ERROR, "已还款中的订单不能分单");
            }
            if (!model.getStatus().equals(OrderConstant.Status.OVERDUE.key) && model.getStatus() != OrderConstant.Status.FAIL.key) {
                throw new BusinessException(AdminRespCode.ORDER_STATUS_ERROR, "已还款中的订单不能分单");
            }
            if (StringUtils.isEmpty(model.getChaseId())) {
                model.setChaseId(OrderIdUtil.getChaseId());
            }
            orderDao.updateOrderChaseInfoById(model.getId(), deptId, model.getChaseId(), null, new Date());

            // 插入操作记录
            OrderOptModel optModel = new OrderOptModel();
            optModel.setStage(OrderConstant.Stage.REPAYMENT.key);
            optModel.setStageFlag(OrderConstant.Stage.REPAYMENT.name);
            optModel.setStatus(OrderConstant.Status.SEPERATE_ORDER.key);
            optModel.setType(CommonConst.OK);
            optModel.setOrderNumber(model.getOrderNumber());
            optModel.setAgencyId(model.getAgencyId());
            optModel.setGuest(model.getGuest());
            optModel.setActorId(actorId);
            optModel.setUserId(model.getUserId());
            optModel.setRemark("分配到" + finalDeptName);
            orderOptDao.insert(optModel);


        });
    }

    @Override
    public void doBadDebt(String orderNumber, Integer agencyId, Integer actorId) {
        DistributeLock lock = null;
        try {
            lock = lockFactory.newLock(ZKLockConst.ORDER_LOCK + orderNumber);
            lock.lock();
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            TransactionStatus status = transactionManager.getTransaction(def);
            try {

                OrderModel select = new OrderModel();
                select.setOrderNumber(orderNumber);
                select.setAgencyId(agencyId);
                OrderModel orderModel = orderDao.selectOne(select);
                if (orderModel == null) {
                    throw new BusinessException(ORDER_NOT_EXIST);
                }

                if (OrderConstant.Stage.REPAYMENT.key != orderModel.getStage()) {
                    throw new BusinessException(AdminRespCode.REPAY_ORDER_NOT_EXIST);
                }

                List<Integer> wait = Arrays.asList(1, 3, 4);
                if (!wait.contains(orderModel.getStatus())) {
                    throw new BusinessException(AdminRespCode.REPAY_ORDER_NOT_EXIST);
                }

                chaseDebtDao.doBadDebt(orderModel.getId(), OrderConstant.Status.FAIL.key);

                // 插入操作记录
                OrderOptModel optModel = new OrderOptModel();
                optModel.setStage(OrderConstant.Stage.REPAYMENT.key);
                optModel.setStageFlag(OrderConstant.Stage.REPAYMENT.name);
                optModel.setStatus(OrderConstant.Status.FAIL.key);
                optModel.setType(CommonConst.OK);
                optModel.setOrderNumber(orderModel.getOrderNumber());
                optModel.setAgencyId(orderModel.getAgencyId());
                optModel.setActorId(actorId);
                optModel.setGuest(orderModel.getGuest());
                optModel.setUserId(orderModel.getUserId());
                optModel.setRemark("坏账");
                orderOptDao.insert(optModel);

                if (logger.isInfoEnabled()) {
                    logger.error(">>>>order chase 用户={}将订单={}置为坏账", actorId, orderNumber);
                }

                UserModel userModel = comUserService.selectUserModelById(orderModel.getUserId());

                // 坏账的jms
                RepayStatisticDTO dto = new RepayStatisticDTO();
                dto.setRepayDate(Date8Util.date2LocalDate(orderModel.getRepaymentDate()));
                dto.setAgencyId(orderModel.getAgencyId());
                dto.setIsFail(CommonConst.YES);
                dto.setVestId(userModel.getVestId());
                jmsTemplate.convertAndSend(JmsDest.ORDER_CONFIRM_OPT, dto);
                logger.info(">>>>发送统计,坏账jms ,data={}", dto);

                transactionManager.commit(status);

            } catch (Exception e) {
                if (logger.isErrorEnabled()) {
                    logger.error("坏账程序异常,订单号" + orderNumber, e);
                }
                transactionManager.rollback(status);
                throw e;
            }
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    @Override
    public void exportChasePage(ChaseDebtPo po, HttpServletResponse response) {
        Integer count = chaseDebtDao.queryChaseCount(po);
        if (count == 0) {
            throw new BusinessException(AdminRespCode.NOT_HAVE_ORDER);
        }
        ExcelWriteBuilder excelWriteBuilder = excelNewHelper.createExcelWriteBuilder("催收分配");
        excelWriteBuilder
                .addHeaderColumn("申贷日期", "createTime")
                .addHeaderColumn("用户姓名", "userName")
                .addHeaderColumn("用户手机", "userPhone")
                .addHeaderColumn("身份证号", "idcardNum")
                .addHeaderColumn("产品名称", "productName")
                .addHeaderColumn("放款金额", "loan")
                .addHeaderColumn("逾期金额", "exceedFee")
                .addHeaderColumn("应还本金", "obtain")
                .addHeaderColumn("应还利息", "stampTax")
                .addHeaderColumn("联系人1姓名", "firstContact")
                .addHeaderColumn("联系人1电话", "firstPhone")
                .addHeaderColumn("联系人2姓名", "otherContact")
                .addHeaderColumn("联系人2电话", "otherPhone")
                .addHeaderColumn("通讯录", "userContactList");
        int totalRow = 1000;
        // 默认查询
        po.setPage(0);
        po.setPageSize(totalRow);
        po.initLimit();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (true) {
            List<ChaseExcelVo> list = chaseDebtDao.queryChaseExcelList(po);

            for (ChaseExcelVo vo : list) {
                Map<String, Object> obj = new ConcurrentHashMap<>();
                obj.put("createTime", dateFormat.format(vo.getCreateTime()));
                obj.put("userName", vo.getUserName() == null ? "---" : vo.getUserName());
                obj.put("userPhone", vo.getUserPhone() == null ? "---" : vo.getUserPhone());
                obj.put("idcardNum", vo.getIdcardNum() == null ? "---" : vo.getIdcardNum());
                obj.put("productName", vo.getProductName() == null ? "---" : vo.getProductName());
                obj.put("loan", vo.getLoan());
                obj.put("exceedFee", vo.getExceedFee());
                obj.put("obtain", vo.getObtain());
                obj.put("stampTax", vo.getStampTax());
                obj.put("firstContact", vo.getFirstContact() == null ? "---" : vo.getFirstContact());
                obj.put("firstPhone", vo.getFirstPhone() == null ? "---" : vo.getFirstPhone());
                obj.put("otherContact", vo.getOtherContact() == null ? "---" : vo.getOtherContact());
                obj.put("otherPhone", vo.getOtherPhone() == null ? "---" : vo.getOtherPhone());

                obj.put("userContactList", handleUserContact(vo.getUserId()));

                excelWriteBuilder.appendRowData(obj);
            }
            po.initLimit();
            if (list.size() < totalRow) break;
            po.setPage(po.getPage() + 1);
        }

        try {
            excelNewHelper.write2Servlet(response, "催收分配", excelWriteBuilder);
        } catch (IOException e) {
            throw new BusinessException(AdminRespCode.INNER_RESULT_ERROR);
        }
    }

    @Override
    public Page<OrderPageVo> getGroupChasePage(ChaseDebtPo po, AdminUserModel userModel) {
        List<Integer> deptIds = adminDeptService.findDeptIdsByType(userModel, DeptType.CHASEDEBT_DEPT.key);//.stream().map(AdminDeptModel::getId).collect(Collectors.toList());
        Integer count = chaseDebtDao.getGroupChaseCount(po, deptIds);
        if (count == 0) {
            return new Page<>(po.getPage(), po.getPageSize(), count, new ArrayList<>());
        }
        List<OrderPageVo> list = chaseDebtDao.findGroupChaseList(po, deptIds);
        List<OrderPageVo> result = new ArrayList<>();
        for (OrderPageVo vo : list) {
            if (vo.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(vo.getVestId());
                if (vestModel != null) vo.setVestName(vestModel.getName());
            }
            result.add(vo.change(vo));
        }
        return new Page<>(po.getPage(), po.getPageSize(), count, result);
    }

    @Override
    public List<AdminUserModel> getChaseDeptActor(AdminUserModel userModel, Integer deptType) {
//        List<Integer> deptIds = adminDeptService.findDeptListByType(userModel, deptType).stream().map(AdminDeptModel::getId).collect(Collectors.toList());
        List<Integer> deptIds = adminDeptService.findDeptIdsByType(userModel, deptType);//.stream().map(AdminDeptModel::getId).collect(Collectors.toList());
        return adminUserService.findUserListByDeptIds(deptIds, userModel.getDataId());
    }

    @Override
    @Transactional
    public void groupSeperateOrder(String orderNumbers, Integer actorId, Integer currentActorId) {
        String[] orderNumber = orderNumbers.split(",");
        List<OrderModel> list = orderDao.findOrderListByOrderNumbs(orderNumber);

        String actorName = null;
        AdminUserModel adminUser = adminActorDao.getAdminUser(actorId);
        if (adminUser != null) {
            actorName = adminUser.getName();
        }

        String finalActorName = actorName;
        list.forEach(model -> {
            if (!model.getStage().equals(OrderConstant.Stage.REPAYMENT.key)) {
                throw new BusinessException(AdminRespCode.ORDER_STATUS_ERROR, "已还款中的订单不能分单");
            }
            if (!model.getStatus().equals(OrderConstant.Status.OVERDUE.key) && model.getStatus() != OrderConstant.Status.FAIL.key) {
                throw new BusinessException(AdminRespCode.ORDER_STATUS_ERROR, "已还款中的订单不能分单");
            }
            OrderModel order = new OrderModel();
            order.setId(model.getId());
            order.setChaseActorId(actorId);
            order.setChaseDate(new Date());
            orderDao.updateById(order);


            // 插入操作记录
            OrderOptModel optModel = new OrderOptModel();
            optModel.setStage(OrderConstant.Stage.REPAYMENT.key);
            optModel.setStageFlag(OrderConstant.Stage.REPAYMENT.name);
            optModel.setStatus(OrderConstant.Status.GROUP_SEPERATE.key);
            optModel.setType(CommonConst.OK);
            optModel.setOrderNumber(model.getOrderNumber());
            optModel.setAgencyId(model.getAgencyId());
            optModel.setGuest(model.getGuest());
            optModel.setActorId(currentActorId);
            optModel.setUserId(model.getUserId());
            optModel.setRemark("分单给" + finalActorName);
            orderOptDao.insert(optModel);


        });
    }

    @Override
    public Page<OrderPageVo> getOwnerChasePage(ChaseDebtPo po) {
        Integer count = chaseDebtDao.queryOwnerChaseCount(po);
        if (count == 0) {
            return new Page<>(po.getPage(), po.getPageSize(), count, new ArrayList<>());
        }
        List<OrderPageVo> list = chaseDebtDao.queryOwnerChasePage(po);
        List<OrderPageVo> result = new ArrayList<>();
        for (OrderPageVo vo : list) {
            if (vo.getVestId() != null) {
                AppVestModel vestModel = comAgencyService.getVestInfoByVestId(vo.getVestId());
                if (vestModel != null) vo.setVestName(vestModel.getName());
            }
            result.add(vo.change(vo));
        }
        return new Page<>(po.getPage(), po.getPageSize(), count, result);

    }

    @Override
    public void exportOwnerChasePage(ChaseDebtPo po, HttpServletResponse response) {
        Integer count = chaseDebtDao.queryOwnerChaseCount(po);
        if (count == 0) {
            throw new BusinessException(AdminRespCode.NOT_HAVE_ORDER);
        }
        ExcelWriteBuilder excelWriteBuilder = excelNewHelper.createExcelWriteBuilder("我的催单");
        excelWriteBuilder
                .addHeaderColumn("申贷日期", "createTime")
                .addHeaderColumn("用户姓名", "userName")
                .addHeaderColumn("用户手机", "userPhone")
                .addHeaderColumn("身份证号", "idcardNum")
                .addHeaderColumn("产品名称", "productName")
                .addHeaderColumn("放款金额", "loan")
                .addHeaderColumn("逾期金额", "exceedFee")
                .addHeaderColumn("应还本金", "obtain")
                .addHeaderColumn("应还利息", "stampTax")
                .addHeaderColumn("联系人1姓名", "firstContact")
                .addHeaderColumn("联系人1电话", "firstPhone")
                .addHeaderColumn("联系人2姓名", "otherContact")
                .addHeaderColumn("联系人2电话", "otherPhone")
                .addHeaderColumn("通讯录", "userContactList");
        int totalRow = 1000;
        // 默认查询
        po.setPage(0);
        po.setPageSize(totalRow);
        po.initLimit();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        while (true) {
            List<ChaseExcelVo> list = chaseDebtDao.queryOwnerChaseExcelList(po);

            for (ChaseExcelVo vo : list) {
                Map<String, Object> obj = new ConcurrentHashMap<>();
                obj.put("createTime", dateFormat.format(vo.getCreateTime()));
                obj.put("userName", vo.getUserName() == null ? "---" : vo.getUserName());
                obj.put("userPhone", vo.getUserPhone() == null ? "---" : vo.getUserPhone());
                obj.put("idcardNum", vo.getIdcardNum() == null ? "---" : vo.getIdcardNum());
                obj.put("productName", vo.getProductName() == null ? "---" : vo.getProductName());
                obj.put("loan", vo.getLoan());
                obj.put("exceedFee", vo.getExceedFee());
                obj.put("obtain", vo.getObtain());
                obj.put("stampTax", vo.getStampTax());
                obj.put("firstContact", vo.getFirstContact() == null ? "---" : vo.getFirstContact());
                obj.put("firstPhone", vo.getFirstPhone() == null ? "---" : vo.getFirstPhone());
                obj.put("otherContact", vo.getOtherContact() == null ? "---" : vo.getOtherContact());
                obj.put("otherPhone", vo.getOtherPhone() == null ? "---" : vo.getOtherPhone());

                obj.put("userContactList", handleUserContact(vo.getUserId()));

                excelWriteBuilder.appendRowData(obj);
            }
            po.initLimit();
            if (list.size() < totalRow) break;
            po.setPage(po.getPage() + 1);
        }

        try {
            excelNewHelper.write2Servlet(response, "我的催单", excelWriteBuilder);
        } catch (IOException e) {
            throw new BusinessException(AdminRespCode.INNER_RESULT_ERROR);
        }
    }

    private String handleUserContact(Integer userId) {
        if (userId == null) return null;
        Map map = new HashMap();
        List<UserContactModel> list = comUserService.findUserContactListByUserId(userId);
        if (CollectionUtils.isNotEmpty(list)) {
            for (UserContactModel model : list) {
                map.put(model.getPhone(), model.getName());
            }
        }
        return JSONObject.toJSONString(map);
    }

}
