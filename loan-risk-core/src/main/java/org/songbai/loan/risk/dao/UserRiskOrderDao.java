package org.songbai.loan.risk.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.songbai.loan.risk.model.user.UserRiskOrderModel;

import java.util.List;


@Mapper
public interface UserRiskOrderDao extends BaseMapper<UserRiskOrderModel> {


    @Select("select * from risk_user_risk_order where status =2 and create_time <= date_sub(now(),INTERVAL #{arg0} MINUTE )")
    public List<UserRiskOrderModel> selectUserRiskOrderModel(Integer intervalMinute);


    @Select("select task_id from risk_user_data_task where user_id = #{arg0} and sources = #{arg1} and status = 5 limit 1")
    public String getTaskIdByUserIdAndSource(String userid, String sources);

    @Select("select * from risk_user_risk_order where user_id = #{arg0} and order_number = #{arg1} order by id desc limit 1")
    public UserRiskOrderModel selectOneLastRiskOrder(String userid, String orderNumber);
}