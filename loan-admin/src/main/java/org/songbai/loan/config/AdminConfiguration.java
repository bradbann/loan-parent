package org.songbai.loan.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.songbai.cloud.basics.boot.*;
import org.songbai.cloud.basics.boot.config.MybatisConfig;
import org.songbai.cloud.basics.boot.config.MybatisPlusConfig;
import org.songbai.cloud.basics.boot.config.RedisConfig;
import org.songbai.cloud.basics.boot.config.RestConfig;
import org.songbai.cloud.basics.mvc.i18n.LocaleKit;
import org.songbai.cloud.basics.utils.excel.ExcelNewHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.AbstractResourceBasedMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableBasic
@EnableConfig
@EnableJms
@Import({
        RestConfig.class,
        RedisConfig.class
})
@EnableAliyun
public class AdminConfiguration {


    @Bean
    public ExcelNewHelper excelNewHelper(SqlSessionFactory sqlSessionFactory) {

        ExcelNewHelper excelNewHelper = new ExcelNewHelper();

        excelNewHelper.setSqlSessionFactory(sqlSessionFactory);

        return excelNewHelper;
    }



    @Bean
    @ConditionalOnBean(MessageSource.class)
    public LocaleKit localeKit(MessageSource messageSource) {

        if (messageSource instanceof AbstractResourceBasedMessageSource) {
            AbstractResourceBasedMessageSource source = (AbstractResourceBasedMessageSource) messageSource;
            source.addBasenames("message_common");
        }

        return LocaleKit.of(messageSource);
    }

}
