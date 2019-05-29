package org.songbai.loan.config.ueditor;

import lombok.extern.slf4j.Slf4j;
import org.songbai.cloud.basics.helper.upload.AliyunOssHelper;
import org.songbai.loan.config.ueditor.upload.StorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class UeditorConfig {

    @Value("classpath:/config.json")
    private Resource resource;

    @Autowired
    AliyunOssHelper aliyunOssHelper;

    @PostConstruct
    public void init() {

        StorageManager.init(aliyunOssHelper);
    }

    @Bean
    public ConfigManager configManager() {
        ConfigManager configManager = ConfigManager.getInstance("/");

        try {
            configManager.initEnv(resource);
        } catch (IOException e) {
            log.error("初始化ueditor失败", e);
        }

        return configManager;
    }


    @Bean
    public SimpleUrlHandlerMapping ueditorUrlHandlerMapping() {

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();


        Map<String, Object> urlMap = new HashMap<>();

        urlMap.put("ueditor/safe_controller.do", new UeditorRequestHandler());
        urlMap.put("ueditor/safe_config.do", new UeditorRequestHandler());

        mapping.setUrlMap(urlMap);
        return mapping;
    }


}
