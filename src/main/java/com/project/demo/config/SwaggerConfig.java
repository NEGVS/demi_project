package com.project.demo.config;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info().title("接口文档")
                        .description("测试接口文档")
                        .version("v1.0")
                        .contact(new Contact().name("andy").url("https://github.com/")))
                .externalDocs(new ExternalDocumentation()
                        .description("未知描述")
                        .url("https://github.com/"));
    }
}



