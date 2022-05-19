package com.accenture.empportal.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import brave.sampler.Sampler;

@Configuration
public class WebClientConfig {

	@Bean
	@LoadBalanced
	public WebClient loadBalancedWebClientBuilder() {
		return WebClient.builder().build();
	}

	@Bean
	public Sampler alwaysSampler() {
		return Sampler.ALWAYS_SAMPLE;

	}

}
