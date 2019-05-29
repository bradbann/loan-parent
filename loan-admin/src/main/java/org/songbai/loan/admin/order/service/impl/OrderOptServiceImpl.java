package org.songbai.loan.admin.order.service.impl;

import org.apache.commons.lang.time.DateUtils;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.utils.base.Ret;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.admin.dao.AdminActorDao;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.order.dao.OrderDao;
import org.songbai.loan.admin.order.dao.OrderOptDao;
import org.songbai.loan.admin.order.po.OrderOptPo;
import org.songbai.loan.admin.order.po.OrderPo;
import org.songbai.loan.admin.order.service.OrderOptService;
import org.songbai.loan.admin.order.vo.OptListVO;
import org.songbai.loan.admin.order.vo.OrderOptPageVo;
import org.songbai.loan.admin.order.vo.OrderOptVo;
import org.songbai.loan.common.util.FormatUtil;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.constant.user.OrderConstant.AuthStatus;
import org.songbai.loan.constant.user.OrderConstant.Stage;
import org.songbai.loan.constant.user.OrderConstant.Status;
import org.songbai.loan.model.loan.OrderModel;
import org.songbai.loan.model.loan.OrderOptModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.service.user.service.ComUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OrderOptServiceImpl implements OrderOptService {
    @Autowired
    private OrderOptDao orderOptDao;

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private ComUserService comUserService;
    @Autowired
    private AdminActorDao adminActorDao;

    private Map<String, String> statusNameMaps = new ConcurrentHashMap<>();

    @Override
    public void createOrderOpt(OrderOptModel optModel) {
        orderOptDao.insert(optModel);
    }

    @Override
    public Ret getOwnerRecord(Integer agencyId, Integer actorId) {
        Ret ret = Ret.create();
        Date date = DateUtils.truncate(new Date(), Calendar.DATE);

        long todayCount = 0;
        long todaySuccCount = 0;
        long todayFailCount = 0;
        long todaySuccNewCount = 0;
        long todayFailNewCount = 0;
        long count = 0;
        long succCount = 0;
        long succNewCount = 0;
        long failCount = 0;
        long failNewCount = 0;


        List<OrderOptVo> todayList = orderOptDao.findOwnerReviewList(agencyId, actorId, date);
        if (!CollectionUtils.isEmpty(todayList)) {
            List<OrderOptVo> succList = todayList.stream().filter(e -> e.getStatus().equals(OrderConstant.Status.SUCCESS.key)).collect(Collectors.toList());
            todaySuccNewCount = succList.stream().filter(e -> e.getGuest().equals(OrderConstant.Guest.NEW_GUEST.key)).count();
            todaySuccCount = succList.stream().filter(e -> e.getGuest().equals(OrderConstant.Guest.OLD_GUEST.key)).count();


            List<OrderOptVo> failList = todayList.stream().filter(e -> e.getStatus().equals(OrderConstant.Status.FAIL.key)).collect(Collectors.toList());
            todayFailNewCount = failList.stream().filter(e -> e.getGuest().equals(OrderConstant.Guest.NEW_GUEST.key)).count();
            todayFailCount = failList.stream().filter(e -> e.getGuest().equals(OrderConstant.Guest.OLD_GUEST.key)).count();

            todayCount = todayList.size();
        }

        List<OrderOptVo> list = orderOptDao.findOwnerReviewList(agencyId, actorId, null);
        if (!CollectionUtils.isEmpty(list)) {
            List<OrderOptVo> succList = list.stream().filter(e -> e.getStatus().equals(OrderConstant.Status.SUCCESS.key)).collect(Collectors.toList());
            succNewCount = succList.stream().filter(e -> e.getGuest().equals(OrderConstant.Guest.NEW_GUEST.key)).count();
            succCount = succList.stream().filter(e -> e.getGuest().equals(OrderConstant.Guest.OLD_GUEST.key)).count();

            List<OrderOptVo> failList = list.stream().filter(e -> e.getStatus().equals(OrderConstant.Status.FAIL.key)).collect(Collectors.toList());
            failNewCount = failList.stream().filter(e -> e.getGuest().equals(OrderConstant.Guest.NEW_GUEST.key)).count();
            failCount = failList.stream().filter(e -> e.getGuest().equals(OrderConstant.Guest.OLD_GUEST.key)).count();

            count = list.size();
        }

        ret.put("todayCount", todayCount);
        ret.put("todaySuccCount", todaySuccNewCount + "/" + todaySuccCount);
        ret.put("todayFailCount", todayFailNewCount + "/" + todayFailCount);
        ret.put("count", count);
        ret.put("succCount", succNewCount + "/" + succCount);
        ret.put("failCount", failNewCount + "/" + failCount);


        OrderPo orderPo = new OrderPo();
        orderPo.setOrderStage(Stage.ARTIFICIAL_AUTH.key);
        orderPo.setOrderStatus(OrderConstant.Status.WAIT.key);
        orderPo.setOrderAuthStatus(AuthStatus.WAIT_REVIEW.key);
        orderPo.setAgencyId(agencyId);
        Integer waitOrderCount = orderDao.queryOrderCount(orderPo);

        ret.put("waitOrderCount", waitOrderCount);
        return ret;
    }

    @Override
    public Page<OrderOptPageVo> getOrderOptPage(OrderOptPo po) {
        po.setStage(OrderConstant.Stage.ARTIFICIAL_AUTH.key);
        Integer count = orderOptDao.queryOrderOptCount(po);
        List<OrderOptPageVo> list = new ArrayList<>();
        if (count > 0) {
            list = orderOptDao.queryOrderOptPage(po);
        }
        return new Page<>(po.getPage(), po.getPageSize(), count, list);
    }

    @Override
    public OrderOptModel findOptLimitOne(String orderNumber, Integer agencyId, Integer stage, Integer status) {
        return orderOptDao.findOptLimitOne(orderNumber, agencyId, stage, status);
    }

    @Override
    public List<OptListVO> findOptList(Integer agencyId, String orderNumber) {

        OrderModel select = new OrderModel();
        select.setAgencyId(agencyId);
        select.setOrderNumber(orderNumber);
        OrderModel orderModel = orderDao.selectOne(select);
        if (orderModel == null) {
            return new ArrayList<>();
        }
        List<OptListVO> list = initOptList(orderModel);
        List<OrderOptModel> optList = orderOptDao.findOptListByOrderNumber(orderNumber);

        Map<Integer, String> actorMap = new HashMap<>();

        for (OrderOptModel opt : optList) {
            OptListVO vo = new OptListVO();
            vo.setCreateTime(opt.getCreateTime());
            vo.setRemark(opt.getRemark());
            if (opt.getActorId() != null) {//人审
                String actorName = actorMap.get(opt.getActorId());
                if (StringUtil.isNotEmpty(actorName)) {
                    vo.setActorName(actorName);
                } else {
                    AdminUserModel adminUser = adminActorDao.getAdminUser(opt.getActorId());
                    if (adminUser != null) {
                        actorMap.put(opt.getActorId(), adminUser.getName());
                        vo.setActorName(adminUser.getName());
                    }
                }
            }
            vo.setStageName(opt.getStageFlag());
            vo.setStatusName(getOptStatusName(opt.getStage(), opt.getStatus()));
            list.add(vo);
        }
        return list;
    }

    private String getOptStatusName(Integer stage, Integer status) {
        String key = stage + "," + status;

        String statusName = statusNameMaps.get(key);

        if (StringUtil.isNotEmpty(statusName)) {
            return statusName;
        }
        setMapKeyName(stage, status, key);
        return statusNameMaps.get(key);
    }

    private void setMapKeyName(Integer stage, Integer status, String key) {
        if (Stage.MACHINE_AUTH.key == stage) {

            if (Status.SUCCESS.key == status) {
                statusNameMaps.put(key, "通过，待复审");
            } else if (Status.PROCESSING.key == status) {
                statusNameMaps.put(key, "通过，待放款");
            } else if (Status.FAIL.key == status) {
                statusNameMaps.put(key, "拒绝");
            } else if (Status.OVERDUE.key == status) {
                statusNameMaps.put(key, "机审转人工");
            }

        } else if (Stage.ARTIFICIAL_AUTH.key == stage) {

            if (Status.SUCCESS.key == status) {
                statusNameMaps.put(key, "通过，待放款");
            } else if (Status.FAIL.key == status) {
                statusNameMaps.put(key, "拒绝");
            }

        } else if (Stage.LOAN.key == stage) {

            if (Status.SUCCESS.key == status) {
                statusNameMaps.put(key, "放款成功,待还款");
            } else if (Status.FAIL.key == status) {
                statusNameMaps.put(key, "拒绝放款");
            } else if (Status.EXCEPTION.key == status) {
                statusNameMaps.put(key, "放款失败(异常)");
            } else if (Status.OVERDUE.key == status) {
                statusNameMaps.put(key, "财务退回");
            }

        } else if (Stage.REPAYMENT.key == stage) {

            if (Status.PROCESSING.key == status) {
                statusNameMaps.put(key, "正在还款");
            } else if (Status.SUCCESS.key == status) {
                statusNameMaps.put(key, "正常还款");
            } else if (Status.FAIL.key == status) {
                statusNameMaps.put(key, "坏账");
            } else if (Status.OVERDUE.key == status) {
                statusNameMaps.put(key, "订单逾期");
            } else if (Status.OVERDUE_LOAN.key == status) {
                statusNameMaps.put(key, "逾期还款");
            } else if (Status.ADVANCE_LOAN.key == status) {
                statusNameMaps.put(key, "提前还款");
            } else if (Status.CHASE_LOAN.key == status) {
                statusNameMaps.put(key, "催收还款");
            } else if (Status.EXCEPTION.key == status) {
                statusNameMaps.put(key, "还款失败");
            } else if (Status.DEDUCT.key == status) {
                statusNameMaps.put(key, "减免金额");
            } else if (Status.SEPERATE_ORDER.key == status) {
                statusNameMaps.put(key, "催收分配");
            } else if (Status.GROUP_SEPERATE.key == status) {
                statusNameMaps.put(key, "组内分单");
            } else if (Status.AUTO_DEDUCT.key == status) {
                statusNameMaps.put(key, "部分还款");
            }
        }
    }

    /**
     * 初始化订单流水
     *
     * @param orderModel
     * @return
     */
    private List<OptListVO> initOptList(OrderModel orderModel) {
        List<OptListVO> list = new ArrayList<>();
        OptListVO vo = new OptListVO();
        vo.setCreateTime(orderModel.getCreateTime());
        vo.setStatusName("创建订单");
        vo.setStageName("创建订单");


        UserModel userModel = comUserService.selectUserModelById(orderModel.getUserId());
        StringBuilder sb = new StringBuilder(100);
        if (userModel != null) {
            sb.append(userModel.getName());
        }
        sb.append("申请借款").append(FormatUtil.formatDouble2(orderModel.getLoan())).append("元，周期")
                .append(orderModel.getDays()).append("天，服务费").append(FormatUtil.formatDouble2(orderModel.getStampTax())).append("元，到账")
                .append(FormatUtil.formatDouble2(orderModel.getObtain())).append("元");
        vo.setRemark(sb.toString());
        list.add(vo);

        return list;

    }

}
