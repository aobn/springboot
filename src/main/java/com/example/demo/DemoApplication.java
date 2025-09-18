package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Spring Boot 应用程序入口类
 * 
 * @SpringBootApplication - 组合了@Configuration、@EnableAutoConfiguration和@ComponentScan
 * @MapperScan - 自动扫描指定包下的Mapper接口
 * @EnableTransactionManagement - 启用事务管理
 */
@SpringBootApplication
@MapperScan("com.example.demo.mapper")
@EnableTransactionManagement
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
