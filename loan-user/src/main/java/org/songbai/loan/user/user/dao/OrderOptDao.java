package org.songbai.loan.user.user.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.model.loan.OrderOptModel;

import java.util.List;

/**
 * Author: qmw
 * Date: 2018/11/5 9:56 AM
 */
public interface OrderOptDao extends BaseMapper<OrderOptModel> {

    List<OrderOptModel> findOrderOptRecordByOrderNumber(String orderNumber);

    /**
     * 查询订单的审核失败的时间
     *
     * @return
     */
    OrderOptModel findAuthFailModel(@Param("userId") Integer userId, @Param("orderNumber") String orderNumber);

    /**
     * 查询订单的放款失败的时间
     *
     * @return
     */
    OrderOptModel findTransferFailModel(Integer userId);

    /**
     * 查询用户最近的一条操作记录
     *
     * @param orderNum
     * @param userId
     * @return
     */
    OrderOptModel getLastUpdateOpt(@Param("orderNum") String orderNum, @Param("userId") Integer userId);

    /**
     * 查询用户最近的一条还款进行中的记录
     */
    OrderOptModel getLastOptModelByOrderIdUserId(@Param("orderNum") String orderNum, @Param("userId") Integer userId);

    OrderOptModel findOrderOptByStageAndStatus(@Param("orderNum") String orderNum, @Param("stage") Integer stage);

}
