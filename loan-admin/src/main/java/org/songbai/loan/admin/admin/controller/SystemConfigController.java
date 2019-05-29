package org.songbai.loan.admin.admin.controller;

import org.songbai.cloud.basics.mvc.Page;
import org.songbai.cloud.basics.mvc.Response;
import org.songbai.loan.admin.config.model.SystemConfigModel;
import org.songbai.loan.admin.config.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping(value = "/systemConfig")
public class SystemConfigController {

    @Autowired
    SystemConfigService systemConfigService;

    @Autowired
    private DiscoveryClient discoveryClient;


    /**
     * 刷新配置
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "flushAllConfig")
    @ResponseBody
    public Response flushAllConfig() throws Exception {

        systemConfigService.flushSystemConfig();

        return Response.success();
    }

    /**
     * 导入配置文件
     *
     * @param configSystem
     * @param context
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "importConfig")
    @ResponseBody
    public Response importConfig(String configSystem, String context) throws Exception {

        if (StringUtils.isEmpty(configSystem) || StringUtils.isEmpty(context)) {
            return Response.error("请求参数不能为空");
        }
        systemConfigService.importConfig(configSystem, context);

        return Response.success("成功");
    }

    /**
     * 导入配置文件
     *
     * @param configSystem
     * @param
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "exportConfig")
    @ResponseBody
    public void exportConfig(String configKey, String configSystem, HttpServletResponse response) throws Exception {
        Page<SystemConfigModel> systemConfig = systemConfigService.querypaging(configKey, configSystem, 0, 9999);

        OutputStream out = null;
        BufferedWriter writer = null;
        try {
            response.reset();
            response.setContentType("text/plain;charset=utf-8");
            response.setHeader("Content-Disposition", "attachment;filename=" + new String(("exportConfig.txt").getBytes(), "iso-8859-1"));

            out = response.getOutputStream();

            writer = new BufferedWriter(new OutputStreamWriter(out));

            for (SystemConfigModel configModel : systemConfig.getData()) {
                writer.write(configModel.getConfigSystem() + "||" + configModel.getConfigKey() + "=" + configModel.getConfigValue());
                writer.newLine();
            }
            writer.flush();

        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } finally {
                if (out != null)
                    out.close();
            }

        }
    }


    @RequestMapping(value = "/saveSystemConfig", method = {RequestMethod.POST})
    @ResponseBody
    public Response saveSystemConfig(String key, String value, String remark, String configSystem) throws Exception {

        Assert.notNull(key, "要保存的配置的名称不能为空");
        Assert.notNull(value, "配置的值不能为空");
        Assert.notNull(configSystem, "配置服务不能为空");

        if (checkConfigSystem(configSystem)) {
            return Response.response(703, "配置服务错误");
        }

        SystemConfigModel systemConfigModel = systemConfigService.getByKey(key);
        if (systemConfigModel == null) {
            systemConfigModel = new SystemConfigModel();
            systemConfigModel.setConfigKey(key);
            systemConfigModel.setConfigValue(value);
            systemConfigModel.setRemark(remark);
            systemConfigModel.setConfigSystem(configSystem);
            systemConfigService.saveSystemConfig(systemConfigModel);
        } else {
            return Response.error("保存失败，已存在相同的配置名称");
        }

        return Response.success();
    }

    @RequestMapping(value = "/updateSystemConfig", method = {RequestMethod.POST})
    @ResponseBody
    public Response updateSystemConfig(Integer id, String key, String value, String remark, String configSystem) throws Exception {

        Assert.notNull(id, "要修改的纪录id不能为空");
        Assert.notNull(key, "配置的名称不能为空");
        Assert.notNull(value, "配置的值不能为空");
        Assert.notNull(configSystem, "配置服务不能为空");
        if (checkConfigSystem(configSystem)) {
            return Response.response(703, "配置服务错误");
        }

        systemConfigService.updateSystemConfig(id, key, value, remark, configSystem);
        return Response.success();
    }

    /**
     * @param ids
     * @throws Exception
     */
    @RequestMapping(value = "/deleteSystemConfig", method = {RequestMethod.POST})
    @ResponseBody
    public Response deleteSystemConfig(String ids) throws Exception {
        Assert.notNull(ids, "要删除的id不能为空");
        systemConfigService.deleteByIds(ids);
        return Response.success();
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/querypaging")
    @ResponseBody
    public Response querypaging(String configKey, String configSystem,
                                @RequestParam(value = "page", defaultValue = "0") Integer page,
                                @RequestParam(value = "pageSize", defaultValue = "20") Integer pageSize) {

        Page<SystemConfigModel> systemConfig = systemConfigService.querypaging(configKey, configSystem, page, pageSize);
        return Response.success(systemConfig);
    }


    private boolean checkConfigSystem(String configSystem) {

        List<String> serviceList = discoveryClient.getServices();

        List<String> shortlist = serviceList.stream().filter((v) -> v.startsWith("loan-")).map(v -> v.replace("loan-", "")).collect(Collectors.toList());

        return !shortlist.contains(configSystem);
    }


}
