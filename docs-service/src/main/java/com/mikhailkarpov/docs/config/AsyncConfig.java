package com.mikhailkarpov.docs.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.VirtualThreadTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class AsyncConfig {

  @Bean(name = "applicationTaskExecutor")
  VirtualThreadTaskExecutor applicationTaskExecutor() {
    return new VirtualThreadTaskExecutor("virtual-");
  }
}
