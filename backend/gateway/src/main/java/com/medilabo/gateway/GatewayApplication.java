package com.medilabo.gateway;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	// Route hardcodée pour tester : /api/** -> http://localhost:8080
	@Bean
	RouteLocator routes(RouteLocatorBuilder b) {
		return b.routes()
				.route("patient-service", r -> r
						.path("/api/**")
						.uri("http://localhost:8080"))
				.build();
	}
	@PostConstruct
	void started() { System.out.println(">> Gateway started on 8081 with /api/** route"); }
}
