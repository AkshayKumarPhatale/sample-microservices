package com.accenture.empportal.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import brave.sampler.Sampler;



@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate() {
       return new RestTemplate();
    }
    
    @Bean
    public Sampler alwaysSampler() {
		return Sampler.ALWAYS_SAMPLE;
    	
    	
    }
}
