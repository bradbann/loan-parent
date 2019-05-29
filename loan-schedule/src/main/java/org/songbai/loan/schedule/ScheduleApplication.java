package org.songbai.loan.schedule;

import org.mybatis.spring.annotation.MapperScan;
import org.songbai.cloud.basics.boot.EnableWeb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zengfanlin
 * @date 2017年5月24日
 * @description
 */
@SpringBootApplication
@EnableWeb
@MapperScan(basePackages={"org.songbai.loan.schedule.dao"})
public class ScheduleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScheduleApplication.class, args);
    }
}



