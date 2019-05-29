package org.songbai.loan;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableJms
@MapperScan(basePackages={"org.songbai.loan.**.dao"})
@EnableAspectJAutoProxy(
        proxyTargetClass = true,
        exposeProxy = true
)
@Configuration
public class LoanAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoanAdminApplication.class,args);
    }
}
