package org.songbai.loan.admin.risk.controller;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.risk.model.RiskMouldVo;
import org.songbai.loan.admin.risk.service.RiskMouldService;
import org.songbai.loan.constant.risk.RiskConst;
import org.songbai.loan.risk.model.mould.RiskMouldModel;
import org.songbai.loan.risk.model.mould.RiskMouldVariableModel;
import org.songbai.loan.risk.model.mould.RiskMouldWeightModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 风控模型
 * Created by mr.czh on 2018/11/6.
 */
@RestController
@RequestMapping("/risk")
public class RiskMouldController {

    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    RiskMouldService riskMouldService;
    /**
     * 后台列表
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/list")
    public Response getRiskList(Integer page, Integer pageSize, RiskMouldVo vo, HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        vo.setAgencyId(agencyId);
        return Response.success(riskMouldService.findByPage(page, pageSize, vo));
    }

    @GetMapping("/getRiskById")
    public Response getRiskById(Integer id) {
        return Response.success(riskMouldService.getRiskById(id));
    }

    /**
     * 新增
     * @param vo
     * @return
     */
    @PostMapping("/addRiskMould")
    public Response addRiskMould(RiskMouldVo vo) {
        Assert.notNull(vo.getName(),"模型名称不能为空");
        riskMouldService.addRiskMould(vo);
        return Response.success();
    }

    /**
     * 复制模型
     * @param name
     * @param status
     * @param mouldId
     * @return
     */
    @PostMapping("/copyRiskMould")
    public Response copyRiskMould(String name ,Integer status , Integer mouldId){

        Assert.notNull(name,"模型名称不能为空");
        Assert.notNull(mouldId,"复制的模型ID不能为空");
        riskMouldService.copyRiskMould(name,status,mouldId);
        return Response.success();
    }

    /**
     * 修改名称或者状态
     * @param vo
     * @return
     */
    @PostMapping("/updateRiskMould")
    public Response updateRiskMould(RiskMouldVo vo) {
        Assert.notNull(vo.getStatus(),"状态不能为空");
        Assert.notNull(vo.getId(),"id不能为空");
        riskMouldService.updateRiskMould(vo);
        return Response.success();
    }

    /**
     * 修改各个评分规则分数
     * @param vo
     * @return
     */
    @PostMapping("/updateRiskRule")
    public Response updateRiskRule(RiskMouldModel vo) {
        Assert.notNull(vo.getId(),"模型id不能为空");
        riskMouldService.updateRiskRule(vo);
        return Response.success();

    }

    /**
     * 修改评分规则模型
     * @return
     */
    @PostMapping("/updateRiskWeight")
    public Response updateRiskWeight(RiskMouldWeightModel model){
        Assert.notNull(model.getId(),"模型规则id不能为空");
        riskMouldService.updateRiskWeight(model);
        return Response.success();
    }

    /**
     * 新增评分规则模型
     * @param model
     * @return
     */
    @PostMapping("/addRiskWeight")
    public Response addRiskWeight(RiskMouldWeightModel model){
        Assert.notNull(model.getName(),"模型名称不能为空");
        Assert.notNull(model.getCatalog(),"模型类型不能为空");
        Assert.notNull(model.getMouldId(),"风控id不能为空");
        return riskMouldService.addRiskWeight(model);
    }

    /**
     * 评分规则模型下拉选
     * @return
     */
    @GetMapping("/ruleDrop")
    public Response ruleDrop() {
        return Response.success(riskMouldService.variableDrop());
    }

    /**
     * 风控模型标签下拉选
     * @param catalog
     * @return
     */
    @GetMapping("/riskTagDrop")
    public Response riskTagDrop(Integer catalog) {
        Assert.notNull(catalog,"风控类型不能为空");
        return Response.success(riskMouldService.riskTagDrop(catalog));
    }

    /**
     * 评分规则列表
     * @param model
     * @return
     */
    @GetMapping("/riskRuleList")
    public Response riskRuleList(RiskMouldWeightModel model) {
        Assert.notNull(model.getMouldId(),"风控模型id不能为空");
        return Response.success(riskMouldService.riskRuleList(model));
    }


    /**
     * 授信规则列表
     * @param model
     * @return
     */
    @GetMapping("/riskVariableList")
    public Response riskVariableList(RiskMouldVariableModel model) {
        Assert.notNull(model.getMouldId(),"风控模型id不能为空");
        return Response.success(riskMouldService.riskVariableList(model));
    }

    /**
     * 新增授信规则
     * @return
     */
    @PostMapping("/addVariable")
    public Response addVariable(String list , Integer mouldId) {
        List<RiskMouldVariableModel> varList = JSONObject.parseArray(list,RiskMouldVariableModel.class);
        riskMouldService.saveMouldVariable(mouldId,varList);

        return Response.success();
    }

    /**
     * 修改授信规则
     * @return
     */
    @PostMapping("/updateVariable")
    public Response updateVariable(String list , Integer mouldId) {
        List<RiskMouldVariableModel> varList = JSONObject.parseArray(list,RiskMouldVariableModel.class);
        riskMouldService.saveMouldVariable(mouldId,varList);

        return Response.success();
    }

    /**
     * 删除授信规则
     * @param id
     * @return
     */
    @PostMapping("/delVariable")
    public Response delVariable(Integer id) {
        Assert.notNull(id,"授信规则id不能为空");
        riskMouldService.delVariable(id);
        return Response.success();
    }

    /**
     * 数学符号下拉选
     * @return
     */
    @GetMapping("/calcSymbolDrop")
    public Response calcSymbolDrop() {
        Map<String,Object> result = new TreeMap<>();
        for (RiskConst.CalcSymbol e : RiskConst.CalcSymbol.values() ) {
            result.put(e.code,e.name);
        }
        return Response.success(result);
    }


    public boolean checkcalcSymbolWithData(String symbol, String left, String right) {
        if (symbol.equals(RiskConst.CalcSymbol.SECTION.code)) {
            if (StringUtils.isEmpty(left) || StringUtils.isEmpty(right)) {
                return false;
            } else {
                if (Double.valueOf(left) >= Double.valueOf(right)) {
                    return false;
                }
            }
        }  else if (StringUtils.isNotEmpty(left)) {
            return false;
        }
        return true;
    }

}
