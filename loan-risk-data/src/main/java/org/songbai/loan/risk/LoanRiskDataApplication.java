package org.songbai.loan.risk;

import org.mybatis.spring.annotation.MapperScan;
import org.songbai.cloud.basics.boot.EnableWeb;
import org.songbai.cloud.basics.concurrent.Executors;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.concurrent.Executor;

@SpringBootApplication
@EnableDiscoveryClient
@EnableWeb
@EnableAsync
@MapperScan(basePackages = {"org.songbai.loan.**.dao"})
@ComponentScan(basePackages = {"org.songbai.loan"})
public class LoanRiskDataApplication {


    public static void main(String[] args) {
        SpringApplication.run(LoanRiskDataApplication.class, args);
    }

    @Configuration
    public static class DreamAsyncConfigurer implements AsyncConfigurer {

        @Override
        public Executor getAsyncExecutor() {
            return Executors.newFixedThreadPool(5, 40, "dream-async");
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return (throwable, method, objects) -> throwable.printStackTrace();
        }
    }

}
