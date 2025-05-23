package com.example.spring.rmago.config;

import com.example.spring.rmago.repository.BaseRepositoryImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.example.spring.rmago.repository",
        repositoryBaseClass = BaseRepositoryImpl.class
)
public class JpaConfig {
}
