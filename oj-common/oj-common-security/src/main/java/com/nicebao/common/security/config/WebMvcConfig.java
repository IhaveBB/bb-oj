package com.nicebao.common.security.config;

import com.nicebao.common.security.interceptor.TokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvcConfig
 * <p>
 *
 * @author IhaveBB
 * @date 2025/2/21
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private TokenInterceptor tokenInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(tokenInterceptor)
				.excludePathPatterns("/**/login", "/**/test/**")
				.addPathPatterns("/**");
	}
}
