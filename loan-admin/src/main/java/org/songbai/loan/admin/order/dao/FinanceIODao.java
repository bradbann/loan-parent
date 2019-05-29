package org.songbai.loan.admin.order.dao;

import org.songbai.loan.model.finance.FinanceIOModel;

import com.baomidou.mybatisplus.mapper.BaseMapper;

import java.util.List;

public interface FinanceIODao extends BaseMapper<FinanceIOModel>{

	List<Integer> selectPayProcessingOrder();
}
