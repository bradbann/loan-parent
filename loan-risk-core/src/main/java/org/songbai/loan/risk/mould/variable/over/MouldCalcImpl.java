package org.songbai.loan.risk.mould.variable.over;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.songbai.cloud.basics.boot.properties.SpringProperties;
import org.songbai.cloud.basics.concurrent.Executors;
import org.songbai.cloud.basics.exception.BusinessException;
import org.songbai.cloud.basics.utils.http.HttpTools;
import org.songbai.loan.constant.risk.RiskConst;
import org.songbai.loan.constant.user.OrderConstant;
import org.songbai.loan.model.agency.AgencyModel;
import org.songbai.loan.model.user.UserInfoModel;
import org.songbai.loan.model.user.UserModel;
import org.songbai.loan.risk.model.mould.RiskMouldModel;
import org.songbai.loan.risk.model.mould.RiskMouldWeightModel;
import org.songbai.loan.risk.model.user.RiskUserMouldCatalogModel;
import org.songbai.loan.risk.mould.helper.RiskResultBuilder;
import org.songbai.loan.risk.mould.variable.MouldCalc;
import org.songbai.loan.risk.service.RiskMouldService;
import org.songbai.loan.risk.vo.RiskResultVO;
import org.songbai.loan.service.agency.service.ComAgencyService;
import org.songbai.loan.service.user.service.ComUserService;
import org.songbai.loan.vo.risk.RiskOrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;


@Slf4j
@Service
public class MouldCalcImpl implements MouldCalc {
    @Autowired
    private RiskMouldService riskMouldService;
    @Autowired
    ComUserService userService;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    DataPrepareHelper prepareHelper;
    @Autowired
    private SpringProperties properties;

    private ExecutorService executor;
    @Autowired
    ComAgencyService comAgencyService;


    @PostConstruct
    public void init() {
        int maxNum = Runtime.getRuntime().availableProcessors() * 4;
        int minNum = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(minNum, maxNum, "mould-calc");
    }


    @Override
    public RiskResultVO calc(RiskOrderVO orderVO) {

        RiskMouldModel mouldModel = null;
        UserInfoModel infoModel = null;
        try {
            mouldModel = getRiskMouldModel(orderVO.getThridId());
            infoModel = userService.selectUserInfoByThridId(orderVO.getThridId());

            log.info("开始计算用户风控值 {}，使用模型:{}", orderVO, mouldModel);

            List<RiskMouldWeightModel> realList = getUsedMouldWeightModels(mouldModel);

            CatalogMouldResult mouldResult = awaitCatalogMouldFinish(orderVO, realList, mouldModel);

            List<RiskUserMouldCatalogModel> mouldCatalogModels = getVarResultModels(orderVO);

            if (!isAllReady(mouldCatalogModels, realList)) {
                return RiskResultVO.builder()
                        .code(RiskResultVO.CODE_WAITDATA).msg(mouldResult.msg)
                        .userId(orderVO.getThridId())
                        .orderNumber(orderVO.getOrderNumber())
                        .mouldId(mouldModel.getId())
                        .build();
            }


            log.info("开始组合每一个类别风控结果 {}，result:{}", orderVO, mouldCatalogModels);

            RiskResultBuilder builder = RiskResultBuilder.create(mouldModel, infoModel.getSex());


            builder.calc(mouldCatalogModels);

            RiskResultVO resultVO = builder.build();
            resultVO.setMsg("old_scorecard:{" + resultVO.getScoring() + "}");
            resultVO.setScoring(getScoring(orderVO));
            resultVO.setUserId(orderVO.getThridId());
            resultVO.setOrderNumber(orderVO.getOrderNumber());
            resultVO.setMouldId(mouldModel.getId());
            return resultVO;
        } catch (BusinessException e) {
            log.error("风控计算异常", e);
            return RiskResultVO.builder()
                    .code(RiskResultVO.CODE_FAIL).msg(e.getMessage())
                    .userId(orderVO.getThridId())
                    .orderNumber(orderVO.getOrderNumber())
                    .mouldId(mouldModel != null ? mouldModel.getId() : null)
                    .build();
        }


    }

    private List<RiskMouldWeightModel> getUsedMouldWeightModels(RiskMouldModel mouldModel) {
        List<RiskMouldWeightModel> weightModels = riskMouldService.selectMouldWeightListByMouldId(mouldModel.getId());

        return weightModels.stream().filter(v -> v.getCatalogCount() > 0).collect(Collectors.toList());
    }

    private CatalogMouldResult awaitCatalogMouldFinish(final RiskOrderVO orderVO, final List<RiskMouldWeightModel> realList, RiskMouldModel mouldModel) {
        CatalogCalcHelper calcHelper = new CatalogCalcHelper(mongoTemplate, riskMouldService, mouldModel);
        CountDownLatch latch = new CountDownLatch(realList.size());

        CatalogMouldResult mouldResult = new CatalogMouldResult();

        log.info("分别计算没个类别的风控值 {}", orderVO);

        for (RiskMouldWeightModel weightModel : realList) {

            executor.submit(() -> {
                try {
                    readyForPlatform(orderVO, weightModel.getCatalog());
                    RiskUserMouldCatalogModel catalogModel = calcHelper.calc(weightModel, orderVO);

                    mouldResult.addModel(catalogModel);
                } catch (Exception e) {
                    log.error("分组计算异常", e);
                    mouldResult.setException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }

        return mouldResult;
    }


    private void readyForPlatform(RiskOrderVO vo, Integer catalog) {


        switch (Objects.requireNonNull(RiskConst.Catalog.parse(catalog))) {

            case BASIC:
                prepareHelper.prepareForBase(vo, catalog);
                break;
            case CONTACTS:
                prepareHelper.prepareForContact(vo, catalog);
                break;
            case MOXIEREPORT:
                prepareHelper.prepareForMoxieReport(vo, catalog);
                break;
            case CARRIERS:
//                prepareHelper.prepareForCarrierReport(vo, catalog);
                break;
            case TAOBAO:
//                prepareHelper.prepareForTaobaoReport(vo, catalog);
                break;

        }


    }

    private boolean isAllReady(List<RiskUserMouldCatalogModel> list, List<RiskMouldWeightModel> catalogWeightList) {
        List<Integer> catalogList1 = list.stream().map(RiskUserMouldCatalogModel::getCatalog).collect(Collectors.toList());
        List<Integer> catalogList2 = catalogWeightList.stream().map(RiskMouldWeightModel::getCatalog).collect(Collectors.toList());


        if (catalogList1.size() < catalogList2.size()) {

            return false;
        }

        return CollectionUtils.isEqualCollection(catalogList1, catalogList2);
    }


    private List<RiskUserMouldCatalogModel> getVarResultModels(RiskOrderVO orderVO) {
        Criteria criteria = Criteria.where("userId").is(orderVO.getThridId())
                .and("orderNumber").is(orderVO.getOrderNumber());

        return mongoTemplate.find(Query.query(criteria), RiskUserMouldCatalogModel.class);
    }


    private RiskMouldModel getRiskMouldModel(String userid) {
        RiskMouldModel mouldModel = null;
        UserModel userModel = userService.selectUserModelByThridId(userid);
        if (userModel != null) {
            AgencyModel agencyModel = comAgencyService.findAgencyById(userModel.getAgencyId());
            if (agencyModel != null) {
                if (userModel.getGuest() == OrderConstant.Guest.NEW_GUEST.key) {
                    mouldModel = riskMouldService.selectMouldById(agencyModel.getMouldId());
                } else if (userModel.getGuest() == OrderConstant.Guest.OLD_GUEST.key) {
                    mouldModel = riskMouldService.selectMouldById(agencyModel.getOldGuestMouldId());
                }
            }
        }

        if (mouldModel == null) {
            log.error("不能查询到启用的风控模型,userThirdId={}", userid);
            throw new RuntimeException("不能查询到启用的风控模型");
        }
        return mouldModel;
    }


    class CatalogMouldResult {
        Exception exception;
        private String msg = "";

        List<RiskUserMouldCatalogModel> models = new ArrayList<>();

        void setException(Exception exception) {
            this.exception = exception;
            setMsg(exception.getMessage());
        }

        void setMsg(String msg) {
            if (msg != null) {
                if (msg.length() > 100) {
                    this.msg = this.msg.substring(0, 100) + msg + ";";
                } else {
                    this.msg = this.msg + msg + ";";
                }
            } else {
                this.msg = this.msg + "null;";
            }
        }

        void addModel(RiskUserMouldCatalogModel model) {
            this.models.add(model);
        }

    }

    private Integer getScoring(RiskOrderVO orderVO) {

        HashMap<String, String> param = new HashMap<>();

        param.put("userid", orderVO.getThridId());
        param.put("order_number", orderVO.getOrderNumber());

        try {
            String url = properties.getString("risk.scorecard.url", "http://localhost:3200/risk/scorecard");
            String result = HttpTools.doGet(url, param);

            JSONObject obj = JSONObject.parseObject(result);

            log.info("request scorecard param={},result={}", param, result);

            if (obj.getInteger("code") == 200) {
                // {"code":200,"data":{"prob":0.06770000000000001,"score":311},"msg":"success"}
                return obj.getJSONObject("data").getInteger("score");
            }
        } catch (Exception e) {
            log.info("request scorecard error: " + param, e);
        }

        return -1;
    }


}
