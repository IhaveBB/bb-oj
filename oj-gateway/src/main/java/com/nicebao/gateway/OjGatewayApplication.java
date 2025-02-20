package com.nicebao.gateway;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * OJSystemApplication
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/18
 */
@SpringBootApplication
public class OjGatewayApplication {
	public static void main(String[] args) {
		SpringApplication.run(OjGatewayApplication.class, args);
	}
}
