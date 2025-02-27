package com.workerserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class WorkerServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(WorkerServerApplication.class, args);
		System.out.println("WorkerServer rodando na porta nseikk");
	}
}
