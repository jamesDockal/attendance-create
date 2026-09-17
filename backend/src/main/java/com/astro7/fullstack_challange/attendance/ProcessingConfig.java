package com.astro7.fullstack_challange.attendance;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling
public class ProcessingConfig {

	@Bean(destroyMethod = "shutdownNow")
	ExecutorService processingExecutor(@Value("${processing.threads}") int threads) {
		return Executors.newFixedThreadPool(threads);
	}

}
