package com.eduardo.geolocation_h3uber.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Geolocation H3 Uber API", version = "1.0", description = "API de geolocalização utilizando o sistema H3 da Uber com Spring Security JWT.", contact = @Contact(name = "Eduardo Bombonatto", email = "seuemail@exemplo.com")), security = @SecurityRequirement(name = "bearerAuth"))
@SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT", description = "Insira o token JWT gerado na rota de login.")
public class SwaggerConfig {

}
