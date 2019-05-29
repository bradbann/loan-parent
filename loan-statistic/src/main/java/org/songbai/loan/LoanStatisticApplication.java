package org.songbai.loan;

import org.mybatis.spring.annotation.MapperScan;
import org.songbai.cloud.basics.boot.EnableWeb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableWeb
@MapperScan(basePackages={"org.songbai.loan.**.dao"})
public class LoanStatisticApplication {
    public static void main(String[] args) {
        SpringApplication.run(LoanStatisticApplication.class,args);
    }
}