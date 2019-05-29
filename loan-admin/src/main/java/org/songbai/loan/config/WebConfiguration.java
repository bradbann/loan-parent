package org.songbai.loan.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.songbai.cloud.basics.mvc.converter.FastJsonConverter;
import org.songbai.cloud.basics.mvc.converter.OrdinalToEnumConverterFactory;
import org.songbai.cloud.basics.mvc.handler.GlobalExceptionHandler;
import org.songbai.cloud.basics.mvc.inteceptor.EffectInterceptor;
import org.songbai.cloud.basics.utils.base.support.StringToDateConverter;
import org.songbai.loan.config.converter.StringMessageConverter;
import org.songbai.loan.config.interceptor.AdminAccessInterceptor;
import org.songbai.loan.config.interceptor.UrlAccessResourceInterceptor;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Locale;

@Configuration
public class WebConfiguration extends WebMvcConfigurationSupport {


    private ApplicationContext applicationContext ;
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setDefaultEncoding("UTF-8");
        return multipartResolver;
    }

    @Bean
    public FastJsonHttpMessageConverter fastJsonConverter() {
        FastJsonConverter converter = new FastJsonConverter();


        FastJsonConfig config = new FastJsonConfig();

        config.setCharset(Charset.forName("utf-8"));
        config.setDateFormat("yyyy-MM-dd HH:mm:ss");
        config.setSerializerFeatures(
                SerializerFeature.WriteDateUseDateFormat,
                SerializerFeature.IgnoreErrorGetter,
                SerializerFeature.BrowserSecure,
                SerializerFeature.BrowserCompatible
        );

        converter.setFastJsonConfig(config);


        return converter;
    }

    @Bean
    public StringMessageConverter stringMessageConverter() {
        return new StringMessageConverter();
    }


    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver resolver = new CookieLocaleResolver();
        resolver.setDefaultLocale(Locale.SIMPLIFIED_CHINESE);
        resolver.setCookieName("language");
        resolver.setCookieMaxAge(3600);
        return resolver;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }


    @Override
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {

        converters.removeIf(httpMessageConverter -> httpMessageConverter instanceof MappingJackson2HttpMessageConverter);

        converters.add(fastJsonConverter());
        converters.add(stringMessageConverter());
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        //以后改为权限控制 由api-gateway控制
        registry.addInterceptor(new EffectInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(localeChangeInterceptor()).addPathPatterns("/**");
        registry.addInterceptor(new UrlAccessResourceInterceptor(applicationContext)).addPathPatterns("/**");
        registry.addInterceptor(new AdminAccessInterceptor(applicationContext)).addPathPatterns("/**");
    }

//    @Bean
//    public FilterRegistrationBean filterRegistration() {//
//        FilterRegistrationBean registration = new FilterRegistrationBean();
//        registration.setFilter(new UrlAccessResourceFilter());
//        registration.addUrlPatterns("*.do");
//        registration.addInitParameter("loginUrl", "/login/verification.do");
//        registration.setName("urlAccessResourceFilter");
//        return registration;
//    }

    @Bean
    public ServletRegistrationBean dispatcherRegistration(DispatcherServlet dispatcherServlet) {
        ServletRegistrationBean registration = new ServletRegistrationBean(dispatcherServlet);
        registration.getUrlMappings().clear();
        registration.addUrlMappings("*.do");
        return registration;
    }

    @Override
    protected void addFormatters(FormatterRegistry registry) {
        registry.addConverter(new StringToDateConverter());
        registry.removeConvertible(String.class, Enum.class);
        registry.addConverterFactory(new OrdinalToEnumConverterFactory());
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        super.setApplicationContext(applicationContext);
        this.applicationContext = applicationContext;
    }
}
