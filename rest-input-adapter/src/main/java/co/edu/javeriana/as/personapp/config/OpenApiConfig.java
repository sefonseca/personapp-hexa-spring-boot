package co.edu.javeriana.as.personapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI personAppOpenAPI() {
		return new OpenAPI().info(new Info()
				.title("PersonApp API")
				.description("API REST hexagonal para el CRUD de Persona, Profesion, Telefono y Estudio. "
						+ "En cada endpoint {database} puede ser MARIA o MONGO.")
				.version("v1"));
	}
}
