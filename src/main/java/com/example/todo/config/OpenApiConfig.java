package com.example.todo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI metadata for the generated API documentation and Swagger UI.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI todoOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Todo API")
                        .description("REST API for managing todo items")
                        .version("v1")
                        .license(new License().name("MIT")));
    }
}
