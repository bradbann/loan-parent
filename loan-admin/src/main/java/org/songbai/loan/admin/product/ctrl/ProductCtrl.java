package org.songbai.loan.admin.product.ctrl;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.product.service.ProductService;
import org.songbai.loan.constant.CommonConst;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.model.loan.ProductModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author: qmw
 * Date: 2018/12/17 3:54 PM
 */
@RestController
@RequestMapping("/product")
public class ProductCtrl {
    @Autowired
    private AdminUserHelper adminUserHelper;
    @Autowired
    private ProductService productService;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

//    @Accessible(onlyAgency = true)
    @PostMapping("add")
    public Response add(ProductModel model, HttpServletRequest request) {
        Assert.hasLength(model.getName(), LocaleKit.get("common.param.notnull", "name"));
        Assert.hasLength(model.getRemark(), LocaleKit.get("common.param.notnull", "remark"));

        Assert.notNull(model.getLoan(), LocaleKit.get("common.param.notnull", "loan"));
        Assert.notNull(model.getStamp(), LocaleKit.get("common.param.notnull", "stamp"));
        Assert.notNull(model.getPay(), LocaleKit.get("common.param.notnull", "pay"));
        Assert.notNull(model.getDays(), LocaleKit.get("common.param.notnull", "days"));
        Assert.notNull(model.getExceedDays(), LocaleKit.get("common.param.notnull", "exceedDays"));
        Assert.notNull(model.getExceedFee(), LocaleKit.get("common.param.notnull", "exceedFee"));
        Assert.notNull(model.getLoanCountMin(), LocaleKit.get("common.param.notnull", "loanCountMin"));
        Assert.notNull(model.getLoanCountMax(), LocaleKit.get("common.param.notnull", "loanCountMax"));
        Assert.notNull(model.getOverdueMax(), LocaleKit.get("common.param.notnull", "overdueMax"));
        Assert.notNull(model.getGroupId(), LocaleKit.get("common.param.notnull", "groupId"));
        Assert.notNull(model.getStatus(), LocaleKit.get("common.param.notnull", "status"));
        Assert.notNull(model.getSorted(), LocaleKit.get("common.param.notnull", "sorted"));
        Assert.notNull(model.getIsDefault(), LocaleKit.get("common.param.notnull", "isDefault"));

        List<Integer> statuss = Arrays.asList(0, 1);
        if (!statuss.contains(model.getStatus())) {
            return Response.success();
        }
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0) model.setAgencyId(agencyId);
        model.setType(CommonConst.NO);
        model.setExceedRate(null);

        productService.insertProduct(model);
        return Response.success();
    }

    /**
     */
    @GetMapping("list")
    public Response list(Integer status,Integer agencyId, HttpServletRequest request) {
        Integer currentAgencyId = adminUserHelper.getAgencyId(request);
        if (currentAgencyId != 0){
            agencyId = currentAgencyId;
        }
        return Response.success(productService.findProductList(agencyId, status));
    }

    /**
     */
    @GetMapping("selected")
    public Response selected(HttpServletRequest request) {
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId == 0){
            agencyId = null;
        }
        return Response.success(productService.findProductSelected(agencyId));
    }


    /**
     */
//    @Accessible(onlyAgency = true)
    @PostMapping("delete")
    public Response delete(Integer id, HttpServletRequest request) {
        Assert.notNull(id, LocaleKit.get("common.param.notnull", "id"));
        Integer agencyId = adminUserHelper.getAgencyId(request);
        productService.deleteProduct(id, agencyId);
        redisTemplate.opsForHash().delete(UserRedisKey.USER_PRODUCT, id);
        return Response.success();
    }

    /**
     */
//    @Accessible(onlyAgency = true)
    @PostMapping("updateStatus")
    public Response updateStatus(Integer id, Integer status, HttpServletRequest request) {
        Assert.notNull(id, LocaleKit.get("common.param.notnull", "id"));
        Assert.notNull(status, LocaleKit.get("common.param.notnull", "status"));
        List<Integer> statuss = Arrays.asList(0, 1);
        if (!statuss.contains(status)) {
            return Response.success();
        }

        Integer agencyId = adminUserHelper.getAgencyId(request);
        productService.updateProductModelStatus(id, status, agencyId);
        redisTemplate.opsForHash().delete(UserRedisKey.USER_PRODUCT, id);
        return Response.success();
    }

    /**
     */
//    @Accessible(onlyAgency = true)
    @PostMapping("updateDefault")
    public Response updateDefault(Integer id, Integer isDefault, HttpServletRequest request) {
        Assert.notNull(id, LocaleKit.get("common.param.notnull", "id"));
        Assert.notNull(isDefault, LocaleKit.get("common.param.notnull", "isDefault"));
        //List<Integer> statuss = Arrays.asList(0, 1);
        List<Integer> statuss = Collections.singletonList(1);
        if (!statuss.contains(isDefault)) {
            return Response.success();
        }

        Integer agencyId = adminUserHelper.getAgencyId(request);
        productService.updateProductModelDefault(id, isDefault, agencyId);
        redisTemplate.opsForHash().getOperations().delete(UserRedisKey.USER_PRODUCT);
        return Response.success();
    }
}
