package org.songbai.loan.admin.risk.controller;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.admin.support.AgencySecurityHelper;
import org.songbai.loan.admin.risk.model.po.RiskOrderPO;
import org.songbai.loan.admin.risk.service.UserRiskOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;

@RestController
@RequestMapping("/riskorder")
public class RiskOrderController {


    @Autowired
    private UserRiskOrderService riskOrderService;


    @Autowired
    private AgencySecurityHelper agencySecurityHelper;


    @PostMapping("riskOrderList")
    public Response riskOrderList(RiskOrderPO po, HttpServletRequest request) {
        
        if (!agencySecurityHelper.checkIsPingtai(request)) {
            return Response.success(new Page<>(0, Page.DEFAULE_PAGESIZE, 0, new ArrayList<>()));
        }

        return Response.success(riskOrderService.selectRiskOrderList(po));
    }


    @RequestMapping("mouldCatalog")
    public Response mouldCatalog(String userId, String orderNumber, HttpServletRequest request) {
        if (!agencySecurityHelper.checkIsPingtai(request)) {
            return Response.success();
        }

        return Response.success(riskOrderService.selectMouldCatalog(userId, orderNumber));
    }

}
