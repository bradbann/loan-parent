package org.songbai.loan.admin.finance.controller;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.finance.model.po.DeductPo;
import org.songbai.loan.admin.finance.model.po.DeductQueuePO;
import org.songbai.loan.admin.finance.model.vo.DeductQueueVo;
import org.songbai.loan.admin.finance.service.FinanceDeductService;
import org.songbai.loan.admin.finance.service.PaymentService;
import org.songbai.loan.admin.finance.service.impl.PaymentFactory;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.config.Accessible;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.service.finance.service.ComFinanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * 所有支付平台的放款操作都在这里
 *
 * @author wjl
 * @date 2018年11月15日 16:35:38
 * @description
 */
@RestController
@RequestMapping("/payment")
public class PaymentController {
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);
    @Autowired
    private AdminUserHelper adminUserHelper;
    @Autowired
    private PaymentFactory paymentFactory;
    @Autowired
    private ComFinanceService comFinanceService;
    @Autowired
    private OrderDao orderDao;
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private FinanceDeductService financeDeductService;

    /**
     * 转账
     */
    @Accessible(onlyAgency = true)
    @PostMapping("/transfer")
    public Response transfer(@RequestParam("ids") List<String> ids, HttpServletRequest request) {
        if (CollectionUtils.isEmpty(ids)) {
            throw new BusinessException(AdminRespCode.PARAM_ERROR);
        }
        Integer agencyId = adminUserHelper.getAgencyId(request);
        Integer actorId = adminUserHelper.getAdminUserId(request);
        EntityWrapper<OrderModel> ew = new EntityWrapper<>();
        ew.in("order_number", ids);
        ew.eq("agency_id", agencyId);
        ew.eq("stage", OrderConstant.Stage.LOAN.key);
        ew.eq("auth_status", CommonConst.STATUS_VALID);
        ew.in("status", Arrays.asList(1, 8));
        List<OrderModel> orderModels = orderDao.selectList(ew);
        if (CollectionUtils.isEmpty(orderModels)) {
            log.info("放款订单集合为空");
            throw new BusinessException(AdminRespCode.PARAM_ERROR, "选中的订单不是待放款状态或已放款成功");
        }
        String platformCode = comFinanceService.getPayCodeByAgency(agencyId);
        PaymentService bean = paymentFactory.getBeanByCode(platformCode);
        if (bean == null) {
            throw new BusinessException(AdminRespCode.PARAM_ERROR, "系统异常，请联系技术人员！");
        }
        List<Integer> list = bean.validate(orderModels, agencyId, actorId);
        if (CollectionUtils.isEmpty(list)) {
            throw new BusinessException(AdminRespCode.PARAM_ERROR, "选中的订单不是待放款状态或已放款成功");
        }
        bean.transfer(list, agencyId, actorId);
        return Response.success();
    }

    /**
     * 代扣
     */
    @Accessible(onlyAgency = true)
    @PostMapping("/autoRepay")
    public Response autoRepay(String orderNumbers, HttpServletRequest request) {
        if (StringUtils.isBlank(orderNumbers)) {
            throw new BusinessException(AdminRespCode.PARAM_ERROR, "请至少勾选一笔订单进行代扣操作");
        }
        Integer agencyId = adminUserHelper.getAgencyId(request);
        AdminUserModel actorModel = adminUserHelper.getAdminUser(request);
//        Map<String, Object> map = new HashMap<>();
//        map.put("ids", ids);
//        map.put("agencyId", agencyId);
//        map.put("actorId", actorId);
//        jmsTemplate.convertAndSend(JmsDest.AUTO_REPAYMENT, JSON.toJSONString(map));
//

        String[] orderIds = StringUtil.tokenizeToStringArray(orderNumbers);
        List<String> failList =  financeDeductService.saveFinanceDeductModel(Arrays.asList(orderIds), agencyId, actorModel);

        StringBuilder sb = new StringBuilder();
        sb.append("总提交 ").append(orderIds.length).append(" 条");
        sb.append(", 成功 ").append( orderIds.length - (failList!= null ? failList.size() : 0) ).append(" 条");
        sb.append(", 失败 ").append((failList!= null ? failList.size() : 0) ).append(" 条 !!");

        if(failList!= null && failList.size() > 0 ) {
            sb.append("\r\n失败原因：");

            for (String s : failList) {
                sb.append(s);
            }
        }
        return Response.success(sb.toString());
    }
    /**
     * 代扣队列
     */
    @Accessible(onlyAgency = true)
    @GetMapping("/queue")
    public Response queue(DeductQueuePO po, HttpServletRequest request) {

        Integer agencyId = adminUserHelper.getAgencyId(request);
        po.setAgencyId(agencyId);
        po.initLimit();
        Page<DeductQueueVo> p =  financeDeductService.findDeductQueue(po);

        return Response.success(p);
    }
    /**
     * 代扣队列-取消
     */
    @Accessible(onlyAgency = true)
    @PostMapping("/cancel")
    public Response cancelRepay(String orderNumber, HttpServletRequest request) {
        if (StringUtils.isBlank(orderNumber)) {
            throw new BusinessException(AdminRespCode.PARAM_ERROR, "请选择需要取消的订单");
        }
        Integer agencyId = adminUserHelper.getAgencyId(request);
        financeDeductService.cancelRepay(orderNumber, agencyId);


        return Response.success();
    }

    @GetMapping("/findDeductFlowList")
    public Response findDeductFlowList(DeductPo po, HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId != 0) po.setAgencyId(agencyId);
        po.initLimit();
        return Response.success(financeDeductService.findDeductFlowList(po));
    }

    @GetMapping("/findDeductTotal")
    public Response findDeductTotal(HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
//        Integer agencyId = 0;
        if (agencyId == 0) agencyId = null;
        return Response.success(financeDeductService.findDeductTotal(agencyId));
    }

}
