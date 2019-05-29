package org.songbai.loan.admin.order.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.songbai.loan.admin.order.po.RepaymentRecordPO;
import org.songbai.loan.admin.order.vo.OrderRepayRecordVO;
import org.songbai.loan.common.util.PageRow;
import org.songbai.loan.model.loan.RepaymentFlowModel;

import java.util.List;

/**
 * Author: qmw
 * Date: 2018/11/7 8:16 PM
 */
public interface RepaymentFlowDao extends BaseMapper<RepaymentFlowModel> {

    int findRePaymentRecordCount(@Param("po") RepaymentRecordPO po);

    List<OrderRepayRecordVO> findRePaymentRecordList(@Param("po") RepaymentRecordPO po, @Param("page") PageRow pageRow);
}
