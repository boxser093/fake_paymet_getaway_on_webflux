package com.ilya.payment_getaway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
        info = @Info(
                title = "Simple fake payment getaway provider.",
                description = "This is first part of microservice system on payment getaway",
                version = "1.0.0",
                contact = @Contact(
                        name = "Ilya Predvechnyy",
                        email = "foodev@example.dev",
                        url = "https://foo.dev.biz"
                )
        )
)
@SecurityScheme(
        name = "Simple Basic",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "Basic",
        scheme = "basic"
)
@Configuration
public class OpenApiConfig {

}
