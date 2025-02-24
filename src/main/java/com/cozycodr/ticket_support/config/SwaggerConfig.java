package com.cozycodr.ticket_support.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI ticketSupportOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Ticket Support API")
                        .description("API for the IT Support Ticket System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Bright Londa")
                                .email("brightl.dev@gmail.com")
                                .url("https://github.com/cozycodr")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Local Development Server")
                ));
    }
}
