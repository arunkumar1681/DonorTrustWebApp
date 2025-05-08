package com.donorsignup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.donorsignup")
public class DonorDataApplication {

	public static void main(String[] args) {
		SpringApplication.run(DonorDataApplication.class, args);
	}

}
