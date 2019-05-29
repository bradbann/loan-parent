package org.songbai.loan.admin.product.ctrl;

import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.product.service.ProductGroupService;
import org.songbai.loan.constant.rediskey.UserRedisKey;
import org.songbai.loan.model.loan.ProductGroupModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * Author: qmw
 * Date: 2018/12/17 3:54 PM
 */
@RestController
@RequestMapping("/product/group")
public class ProductGroupCtrl {
    @Autowired
    private AdminUserHelper adminUserHelper;
    @Autowired
    private ProductGroupService groupService;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;


//    @Accessible(onlyAgency = true)
    @PostMapping("add")
    public Response add(ProductGroupModel model, HttpServletRequest request) {
        Assert.hasLength(model.getName(), LocaleKit.get("common.param.notnull", "name"));
        //Assert.hasLength(model.getRemark(), LocaleKit.get("common.param.notnull", "remark"));
        Assert.notNull(model.getStatus(), LocaleKit.get("common.param.notnull", "status"));

        List<Integer> statuss = Arrays.asList(0, 1);
        if (!statuss.contains(model.getStatus())) {
            return Response.success();
        }
        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0){
            model.setAgencyId(agencyId);
        }

        groupService.insertProductGroup(model);
        return Response.success();
    }

    /**
     */
    @GetMapping("list")
    public Response list(Integer agencyId,HttpServletRequest request) {
        Integer currentAgencyId = adminUserHelper.getAgencyId(request);
        if (currentAgencyId != 0){
            agencyId = currentAgencyId;
        }
        return Response.success(groupService.findProductGroupList(agencyId));
    }

    /**
     */
    @GetMapping("selected")
    public Response selected(HttpServletRequest request, Integer agencyId) {
        if (agencyId == null){
            agencyId = adminUserHelper.getAgencyId(request);
        }
        return Response.success(groupService.findProductGroupSelected(agencyId));
    }


    /**
     */
//    @Accessible(onlyAgency = true)
    @PostMapping("delete")
    public Response delete(Integer id, HttpServletRequest request) {
        Assert.notNull(id, LocaleKit.get("common.param.notnull", "id"));
        Integer agencyId = adminUserHelper.getAgencyId(request);
        groupService.deleteProductGroup(id, agencyId);
        redisTemplate.opsForHash().delete(UserRedisKey.USER_PRODUCT_GROUP, id);
        return Response.success();
    }

    /**
     */
//    @Accessible(onlyAgency = true)
    @PostMapping("update")
    public Response update(ProductGroupModel model, HttpServletRequest request) {
        Assert.notNull(model.getId(), LocaleKit.get("common.param.notnull", "id"));
        Assert.hasLength(model.getName(), LocaleKit.get("common.param.notnull", "name"));
        Assert.notNull(model.getStatus(), LocaleKit.get("common.param.notnull", "status"));

        List<Integer> statuss = Arrays.asList(0, 1);
        if (!statuss.contains(model.getStatus())) {
            return Response.success();
        }

        Integer agencyId = adminUserHelper.getAgencyId(request);
        if (agencyId != 0){
            model.setAgencyId(agencyId);
        }else {
            model.setAgencyId(null);
        }
        groupService.update(model);
        redisTemplate.opsForHash().delete(UserRedisKey.USER_PRODUCT_GROUP, model.getId());

        return Response.success();
    }


}
