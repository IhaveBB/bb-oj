package com.nicebao;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * OJSystemApplication
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/18
 */
@SpringBootApplication
@MapperScan("com.nicebao.system.mapper")
public class OjSystemApplication {
	public static void main(String[] args) {
		SpringApplication.run(OjSystemApplication.class, args);
	}
}
