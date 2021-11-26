package com.sptek.es;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableAsync
@ComponentScan
public class EsCmpApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(EsCmpApiApplication.class, args);
	}

}
