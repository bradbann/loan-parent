package org.songbai.loan.risk.config.retrofit;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;


@Slf4j
@Component
public class RetrofitAutowiredProcessor extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationContextAware {

    ApplicationContext applicationContext;

    @Override
    public boolean postProcessAfterInstantiation(final Object bean, final String beanName) throws BeansException {
        ReflectionUtils.doWithFields(bean.getClass(), field -> {
            HttpService httpApi = field.getAnnotation(HttpService.class);
            if (httpApi == null) {
                return;
            }
            createRetrofitService(bean, field, field.getType());
        });
        return super.postProcessAfterInstantiation(bean, beanName);
    }


    private void createRetrofitService(Object bean, Field field, Class clazz) throws IllegalAccessException {
        //读取注解中的值
        HttpApi httpApi = (HttpApi) clazz.getAnnotation(HttpApi.class);
        String key = httpApi.value();
        if (StringUtils.isBlank(key)) {
            return;
        }

        String value = applicationContext.getEnvironment().getProperty(key, String.class);
        if (StringUtils.isEmpty(value)) {
            value = key;
        }
        //根据地址创建retrofit
        Object object = RetrofitBeanFactory.requireRetrofitBean(value, clazz, httpApi.interceptor());
        if (object == null) {
            return;
        }
        field.setAccessible(true);
        field.set(bean, object);
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
