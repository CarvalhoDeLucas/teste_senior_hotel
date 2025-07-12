package com.example.hotel.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI hotelApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("Hotel API")
                        .description("API para gerenciamento de hóspedes e check-ins")
                        .version("1.0"));
    }

}