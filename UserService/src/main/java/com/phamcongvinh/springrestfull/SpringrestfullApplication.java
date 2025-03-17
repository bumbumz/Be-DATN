package com.phamcongvinh.springrestfull;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

// //disable security
// @SpringBootApplication(exclude = {
// 		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
// })
@SpringBootApplication
@EnableFeignClients
public class SpringrestfullApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringrestfullApplication.class, args);
	}

}
