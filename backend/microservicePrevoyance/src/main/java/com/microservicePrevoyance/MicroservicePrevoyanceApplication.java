package com.microservicePrevoyance;

import com.microservicePrevoyance.model.PrevoyanceProps;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableConfigurationProperties(PrevoyanceProps.class)
@EnableFeignClients
public class MicroservicePrevoyanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicePrevoyanceApplication.class, args);
	}

}
