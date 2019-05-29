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
public class ExchangeSmsApplication {

    static {
        System.setProperty("mail.smtp.connectiontimeout", "100000");
        System.setProperty("mail.smtp.timeout", "150000");
    }

    public static void main(String[] args) {
        SpringApplication.run(ExchangeSmsApplication.class, args);
    }

}
