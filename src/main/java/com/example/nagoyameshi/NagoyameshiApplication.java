package com.example.nagoyameshi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.nagoyameshi")
public class NagoyameshiApplication {

	public static void main(String[] args) {
		SpringApplication.run(NagoyameshiApplication.class, args);
	}

}
