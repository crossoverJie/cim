package com.crossoverjie.cim.client.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI createRestApi() {
        return new OpenAPI()
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("cim client")
                .description("cim client api")
                .termsOfService("http://crossoverJie.top")
                .contact(contact())
                .version("1.0.0");
    }

    private Contact contact () {
        Contact contact = new Contact();
        contact.setName("crossoverJie");
        return contact;
    }
}