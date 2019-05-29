package org.songbai.loan.risk.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.songbai.loan.risk.model.mould.RiskVariableSourceModel;

import java.util.List;

@Mapper
public interface RiskVariableDao {

    /**
     * 获取所有的当前来源的变量
     *
     * @param sources
     * @return
     */
    @Select("select * from risk_variable_source where sources = #{arg0} and status = 1 ")
    public List<RiskVariableSourceModel> selectVariableSource(String sources);


    @Select("select distinct sources from risk_variable_source where catalog = #{arg0}")
    public List<String> selectSourceByCatalog(Integer catalog);
}
