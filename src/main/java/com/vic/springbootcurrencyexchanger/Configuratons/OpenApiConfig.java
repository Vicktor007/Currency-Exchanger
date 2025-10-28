package com.vic.springbootcurrencyexchanger.Configuratons;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;


/**
 * OpenApi documentation configuration
 */

@Configuration
public class OpenApiConfig {

    @Value("${serverUrl}")
    private String serverUrl;

    @Bean
    public OpenAPI exchangerOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Currency Exchanger API")
                        .version("v1.0")
                        .description("""
                                **A springboot application that enables users to convert currencies using live exchange rate APIs.
                                 This project supports multiple data sources for currency exchange rates.
                                
                                """)
                )
                .servers(List.of(
                        new Server().url(serverUrl).description("Local Development Server")
                ));
    }
}
