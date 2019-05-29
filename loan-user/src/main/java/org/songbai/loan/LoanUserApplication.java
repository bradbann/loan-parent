package org.songbai.loan;

import org.mybatis.spring.annotation.MapperScan;
import org.songbai.cloud.basics.boot.EnableAliyun;
import org.songbai.cloud.basics.boot.EnableWeb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableDiscoveryClient
@EnableWeb
@EnableTransactionManagement
@MapperScan(basePackages = {"org.songbai.loan.**.dao"})
@EnableAliyun
@EnableAsync
public class LoanUserApplication {


	public static void main(String[] args) {
		SpringApplication.run(LoanUserApplication.class, args);
	}

}
