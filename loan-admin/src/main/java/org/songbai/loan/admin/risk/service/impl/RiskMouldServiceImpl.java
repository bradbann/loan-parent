package org.songbai.loan.admin.risk.service.impl;

import org.apache.commons.collections.MapUtils;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.utils.base.StringUtil;
import org.songbai.loan.admin.risk.dao.RiskMouldDao;
import org.songbai.loan.admin.risk.dao.RiskMouldWeightDao;
import org.songbai.loan.admin.risk.model.RiskMouldVariableVo;
import org.songbai.loan.admin.risk.model.RiskMouldVo;
import org.songbai.loan.admin.risk.model.RiskVariableDropVo;
import org.songbai.loan.admin.risk.service.RiskMouldService;
import org.songbai.loan.constant.resp.AdminRespCode;
import org.songbai.loan.constant.risk.RiskConst;
import org.songbai.loan.risk.model.mould.RiskMouldModel;
import org.songbai.loan.risk.model.mould.RiskMouldVariableModel;
import org.songbai.loan.risk.model.mould.RiskMouldWeightModel;
import org.songbai.loan.risk.model.mould.RiskVariableModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by mr.czh on 2018/11/6.
 */
@Service
public class RiskMouldServiceImpl implements RiskMouldService {

    @Autowired
    private RiskMouldDao riskMouldDao;

    @Autowired
    private RiskMouldWeightDao riskMouldWeightDao;

    @Override
    public Page<RiskMouldVo> findByPage(Integer page, Integer pageSize, RiskMouldVo vo) {
        page = page == null ? 0 : page;
        pageSize = pageSize == null ? 20 : pageSize;
        Integer limit = page * pageSize;
        List<RiskMouldVo> list = new ArrayList<>();
        Integer row = riskMouldDao.queryCount(vo);
        if (row > 0) {
            list = riskMouldDao.findList(limit, pageSize, vo);
        }
        Page<RiskMouldVo> result = new Page<>(page, pageSize, row);
        result.setData(list);
        return result;
    }

    @Override
    public RiskMouldModel getRiskById(Integer id) {
        return riskMouldDao.getRiskById(id);
    }

    @Override
    @Transactional
    public void addRiskMould(RiskMouldVo vo) {
        riskMouldDao.addRiskMould(vo);
        List<RiskVariableModel> list = riskMouldDao.variableDrop();
        for (RiskVariableModel type : list) {
            RiskMouldWeightModel model = new RiskMouldWeightModel();
            for (RiskConst.Catalog e : RiskConst.Catalog.values()) {
                if (type.getCatalog() == e.code) {
                    model.setName(e.name);
                }
            }
            model.setMouldId(vo.getId());
            model.setCatalog(type.getCatalog());
            riskMouldDao.addRiskModelWeight(model);
        }
    }

    @Override
    public void copyRiskMould(String name, Integer status, Integer mouldId) {
        RiskMouldModel mouldModel = getRiskById(mouldId);

        mouldModel.setName(name);
        mouldModel.setStatus(status);
        mouldModel.setCreateTime(null);
        mouldModel.setUpdateTime(null);
        mouldModel.setId(null);

        riskMouldDao.insert(mouldModel);

        List<RiskMouldWeightModel> weightModelList = riskMouldDao.selectRiskMouldWeightByMouldId(mouldId);


        for (RiskMouldWeightModel weightModel : weightModelList) {

            weightModel.setId(null);
            weightModel.setMouldId(mouldModel.getId());
            weightModel.setCreateTime(null);
            weightModel.setUpdateTime(null);

            riskMouldWeightDao.insert(weightModel);
        }


        List<RiskMouldVariableModel> variableModelList = riskMouldDao.selectMouldVariableByMouldId(mouldId);


        for (RiskMouldVariableModel variableModel : variableModelList) {

            variableModel.setId(null);
            variableModel.setMouldId(mouldModel.getId());
            variableModel.setCreateTime(null);
            variableModel.setUpdateTime(null);

            riskMouldDao.addVariable(variableModel);
        }

    }

    @Override
    public void updateRiskMould(RiskMouldVo vo) {
        riskMouldDao.updateRiskMould(vo);
    }

    @Override
    public void updateRiskRule(RiskMouldModel vo) {
        riskMouldDao.updateRiskRule(vo);
    }

    @Override
    public void updateRiskWeight(RiskMouldWeightModel model) {
        riskMouldDao.updateRiskWeight(model);
    }

    @Override
    public Response addRiskWeight(RiskMouldWeightModel model) {
        Integer count = riskMouldDao.queryCountWeight(model.getMouldId(), model.getCatalog());
        if (count > 0) {
            throw new BusinessException(AdminRespCode.PARAM_ERROR, "相同类型已经存在");
        }
        riskMouldDao.addRiskModelWeight(model);
        return Response.success();
    }

    @Override
    public List<RiskMouldWeightModel> riskRuleList(RiskMouldWeightModel model) {
        return riskMouldDao.selectRiskMouldWeightByMouldId(model.getMouldId());
    }


    private void updateMouldWeightCount(Integer mouldId, Set<Integer> catalogSet) {
        List<RiskMouldWeightModel> list = riskMouldDao.selectRiskMouldWeightByMouldId(mouldId);

        for (RiskMouldWeightModel weightModel : list) {

            if (!catalogSet.contains(weightModel.getCatalog())) {
                continue;
            }

            RiskMouldWeightModel update = new RiskMouldWeightModel();

            update.setId(weightModel.getId());
            setWeightHighScore(update, mouldId, weightModel.getCatalog());

            riskMouldDao.updateRiskWeight(update);
        }
    }


    private void setWeightHighScore(RiskMouldWeightModel update, Integer mouldId, Integer catalog) {

        RiskMouldModel mouldModel = riskMouldDao.getRiskById(mouldId);

        List<HashMap<String, Object>> scoreMap = riskMouldDao.selectVariableListForMaxMinScore(mouldId, catalog);


        Integer minScore = 0;
        Integer maxScore = 0;

        for (HashMap<String, Object> map : scoreMap) {

            Integer maxt = MapUtils.getInteger(map, "maxScore");
            Integer mint = MapUtils.getInteger(map, "minScore");


            if (mouldModel.getScoreType() == -1 && maxt < 0) {
                maxt = 0;
            }

            if (mouldModel.getScoreType() == 1 && mint > 0) {
                mint = 0;
            }

            minScore += mint;
            maxScore += maxt;
        }


        update.setCatalogCount(scoreMap.size());
        update.setLowerScore(minScore);
        update.setHighScore(maxScore);

    }


    @Override
    public List<RiskMouldVariableVo> riskVariableList(RiskMouldVariableModel model) {
        List<RiskMouldVariableVo> bigList = new ArrayList<>();
        List<RiskMouldVariableModel> minList = riskMouldDao.selectMouldVariableByMouldId(model.getMouldId());
        List<RiskMouldVariableModel> newList = new ArrayList<>();
        if (minList != null && minList.size() > 0) {
            Integer bag = minList.get(0).getCatalog();
            String code = minList.get(0).getVariableCode();
            for (int i = 0; i < minList.size(); i++) {
                if (!minList.get(i).getCatalog().equals(bag)) {
                    RiskMouldVariableVo vo = new RiskMouldVariableVo();
                    vo.setCataLg(bag);
                    vo.setVariableCode(code);
                    vo.setList(newList);
                    bigList.add(vo);
                    newList = new ArrayList<>();
                    newList.add(minList.get(i));
                    bag = minList.get(i).getCatalog();
                    code = minList.get(i).getVariableCode();
                } else {
                    if (minList.get(i).getVariableCode().equals(code)) {
                        newList.add(minList.get(i));
                    } else {
                        RiskMouldVariableVo vo = new RiskMouldVariableVo();
                        vo.setVariableCode(code);
                        vo.setCataLg(bag);
                        vo.setList(newList);
                        bigList.add(vo);
                        newList = new ArrayList<>();
                        newList.add(minList.get(i));
                        code = minList.get(i).getVariableCode();
                    }
                }
                if (i == minList.size() - 1) {
                    RiskMouldVariableVo vo = new RiskMouldVariableVo();
                    vo.setCataLg(bag);
                    vo.setVariableCode(code);
                    vo.setList(newList);
                    bigList.add(vo);
                }
            }
        }

        return bigList;
    }

    @Override
    public void saveMouldVariable(Integer mouldId, List<RiskMouldVariableModel> list) {
        if (list == null || list.isEmpty()) {
            return;
        }

        if (mouldId == null) {
            mouldId = list.get(0).getMouldId();
        }


        LinkedHashMap<String, RiskMouldVariableVo> entryHashMap = new LinkedHashMap<>();
        for (RiskMouldVariableModel model : list) {

            if (StringUtil.isEmpty(model.getVariableCode())
                    || model.getCatalog() == null
                    || StringUtil.isEmpty(model.getCalcSymbol())
                    || StringUtil.isEmpty(model.getCalcRight())
                    || model.getOperType() == null
                    || model.getOperScore() == null) {
                throw new BusinessException("数据不能为空");
            }

            entryHashMap.computeIfAbsent(model.getVariableCode() + model.getCatalog(),
                    v -> {
                        RiskMouldVariableVo vo = new RiskMouldVariableVo();
                        vo.setCataLg(model.getCatalog());
                        vo.setVariableCode(model.getVariableCode());
                        vo.setList(new LinkedList<>());
                        return vo;

                    }).getList().add(model);
        }

        Set<Integer> catalogSet = new HashSet<>();
        for (RiskMouldVariableVo entry : entryHashMap.values()) {
            catalogSet.add(entry.getCataLg());

            Integer indexed = getVariableCodeIndex(mouldId, entry);

            riskMouldDao.deleteVariableModelByCatalogAndCode(mouldId, entry.getCataLg(), entry.getVariableCode());

            for (RiskMouldVariableModel model : entry.getList()) {
                model.setIndexed(indexed);
                model.setUpdateTime(null);
                riskMouldDao.addVariable(model);
            }
        }

        updateMouldWeightCount(mouldId, catalogSet);
    }

    private Integer getVariableCodeIndex(Integer mouldId, RiskMouldVariableVo entry) {
        Integer indexed = null;
        List<RiskMouldVariableModel> mouldVariableModelList = riskMouldDao.selectVariableModelByCatalogAndCode(mouldId, entry.getCataLg(), entry.getVariableCode());

        if (mouldVariableModelList != null && mouldVariableModelList.size() > 0) {
            indexed = mouldVariableModelList.get(0).getIndexed();
        }
        if (indexed == null) {
            indexed = riskMouldDao.selectVariableModelMaxIndexdByCatalogAndCode(mouldId, entry.getCataLg());
        }
        indexed = indexed != null ? indexed + 1 : 1;
        return indexed;
    }

    @Override
    public void delVariable(Integer id) {
        riskMouldDao.delVariable(id);
    }

    @Override
    public List<RiskVariableDropVo> variableDrop() {
        List<RiskVariableModel> list = riskMouldDao.variableDrop();
        List<RiskVariableDropVo> lastList = new ArrayList<>();
        for (RiskVariableModel model : list) {
            RiskVariableDropVo vo = new RiskVariableDropVo();
            vo.setCatalog(model.getCatalog());
            for (RiskConst.Catalog e : RiskConst.Catalog.values()) {
                if (model.getCatalog() == e.code) {
                    vo.setName(e.name);
                }
            }
            lastList.add(vo);
        }
        return lastList;
    }

    @Override
    public List<RiskVariableModel> riskTagDrop(Integer catalog) {
        return riskMouldDao.riskTagDrop(catalog);
    }
}
