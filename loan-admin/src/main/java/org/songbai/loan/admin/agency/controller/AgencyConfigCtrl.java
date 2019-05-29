package org.songbai.loan.admin.agency.controller;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.cloud.basics.mvc.annotation.LimitLess;
import org.songbai.loan.admin.admin.model.AdminUserModel;
import org.songbai.loan.admin.admin.support.AdminUserHelper;
import org.songbai.loan.admin.agency.service.AgencyConfigService;
import org.songbai.loan.model.agency.AgencyConfigModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/agencyConfig")
public class AgencyConfigCtrl {
    Logger logger = LoggerFactory.getLogger(AgencyConfigCtrl.class);
    @Autowired
    AdminUserHelper adminUserHelper;
    @Autowired
    AgencyConfigService agencyConfigService;

    @GetMapping("/findAgencyConfigPage")
    public Response findAgencyConfigPage(AgencyConfigModel model, HttpServletRequest request, @RequestParam(defaultValue = "0") Integer page, @RequestParam(defaultValue = "20") Integer pageSize) {
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        return Response.success(agencyConfigService.findAgencyConfigPage(model, userModel, page, pageSize));
    }


    @PostMapping("/addConfig")
    public Response addConfig(AgencyConfigModel model, HttpServletRequest request) {
        Assert.notNull(model.getAmount(), "金额不能为空");
        Assert.notNull(model.getFeeRate(), "手续费利率不能为空");
        Assert.isTrue(model.getFeeRate() < 0 || model.getFeeRate() > 1, "手续费利率只能在0~1之间");
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        agencyConfigService.addConfig(model, userModel);
        return Response.success();
    }

    @PostMapping("/updateConfig")
    public Response updateConfig(AgencyConfigModel model, HttpServletRequest request) {
        Assert.notNull(model.getId(), "id不能为空");
        Assert.notNull(model.getAmount(), "金额不能为空");
        Assert.notNull(model.getFeeRate(), "手续费利率不能为空");
        Assert.isTrue(model.getFeeRate() < 0 || model.getFeeRate() > 1, "手续费利率只能在0~1之间");
        AdminUserModel userModel = adminUserHelper.getAdminUser(request);
        agencyConfigService.updateConfig(model, userModel);
        return Response.success();
    }

    @GetMapping("/findInfoById")
    public Response findInfoById(Integer id) {
        Assert.notNull(id, "id不能为空");
        return Response.success(agencyConfigService.findInfoById(id));
    }

    @GetMapping("/test")
    @LimitLess
    public void test(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long start = System.currentTimeMillis();
        String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
        String accessKeyId = "LTAI9Z225chvXK4t";
        String accessKeySecret = "OPwIsr7iAQo84C7uxmmIJMwzQDZErf";
        String bucketName = "lmloantest";
        String objectName = "upload/201810/2018-10-31/173318429.jpg";

        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        // ossObject包含文件所在的存储空间名称、文件名称、文件元信息以及一个输入流。
        OSSObject ossObject = ossClient.getObject(bucketName, objectName);

        response.setContentType(ossObject.getObjectMetadata().getContentType());
        response.setHeader("picUrl", "www.baidu.com");
        IOUtils.copy(ossObject.getObjectContent(), response.getOutputStream());

    }

}
