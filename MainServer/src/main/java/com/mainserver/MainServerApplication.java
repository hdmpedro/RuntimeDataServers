package com.mainserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MainServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(MainServerApplication.class, args);
		System.out.println("MainServer rodando na porta {porta}");
	}
}
