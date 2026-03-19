package com.masterlab.fraud_rule_engine.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI fraudRuleEngineOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Fraud Rule Engine API")
                .description("Production-grade fraud detection system for processing financial transactions")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Fraud Detection Team")
                    .email("fraud-detection@masterlab.com")));
    }
}
