package com.nicebao.job.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * XxlJobConfig
 * <p>
 *
 * @author IhaveBB
 * @date 2025/4/1
 */

@Configuration
@Slf4j
public class XxlJobConfig {

	@Value("${xxl.job.admin.addresses}")
	private String adminAddresses;

	@Value("${xxl.job.accessToken}")
	private String accessToken;

	@Value("${xxl.job.executor.appname}")
	private String appname;

	@Bean
	public XxlJobSpringExecutor xxlJobExecutor() {
		log.info(">>>>>>>>>>> xxl-job config init.");
		XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
		xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
		xxlJobSpringExecutor.setAppname(appname);
		xxlJobSpringExecutor.setAccessToken(accessToken);
		return xxlJobSpringExecutor;
	}
}

