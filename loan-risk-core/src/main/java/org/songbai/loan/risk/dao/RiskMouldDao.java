package org.songbai.loan.risk.dao;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.songbai.loan.risk.model.mould.RiskMouldModel;
import org.songbai.loan.risk.model.mould.RiskMouldVariableModel;
import org.songbai.loan.risk.model.mould.RiskMouldWeightModel;

import java.util.List;


@Mapper
public interface RiskMouldDao {


    @Select("select * from risk_mould_variable where catalog = #{arg0} and mould_id = #{arg1} and status = 1 order by indexed, id ")
    public List<RiskMouldVariableModel> selectMouldVarList(Integer catalog, Integer mouldId);


    @Select("select * from risk_mould where status = 1 ")
    public RiskMouldModel selectRiskMouldModel();


    @Select("select * from risk_mould_weight where mould_id = #{arg0} and catalog=#{arg1} ")
    RiskMouldWeightModel selectMouldWeightModel(Integer mouldId, Integer catalog);


    @Select("select * from risk_mould_weight where mould_id = #{arg0}")
    List<RiskMouldWeightModel> selectMouldWeightListByMouldId(Integer mouldId);


    @Select("select distinct catalog from risk_mould_variable where mould_id = #{arg0} ")
    List<Integer> selectMouldVariableCatalog(Integer mouldId);


    @Select("select m.* from loan_u_user u, dream_u_agency a ,risk_mould m \n" +
            " where u.agency_id = a.id  and m.id = a.mould_id and u.third_id = #{arg0}")
    public RiskMouldModel selectRiskMouldModelByThirdId(String thirdId);

    @Select("select * from risk_mould where id = #{arg0} ")
    RiskMouldModel selectMouldById(Integer mouldId);
}
