package com.microservicePrevoyance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class MicroservicePrevoyanceApplication {

	public static void main(String[] args) {
		SpringApplication.run(MicroservicePrevoyanceApplication.class, args);
	}

}
