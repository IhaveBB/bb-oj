package com.nicebao.common.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SwaggerConfig
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/19
 */
@Configuration
public class SwaggerConfig {
	@Bean
	public OpenAPI openAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("在线oj系统")
						.description("在线oj系统接口文档")
						.version("v1"));
	}
}
