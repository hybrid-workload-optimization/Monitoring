package com.sptek.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;

import com.sptek.kafka.executor.BenchmarkExecutor;

@PropertySource("file:${user.dir}/application.properties")
@EnableKafka
@SpringBootApplication
public class KafkaBenchmarkApplication  implements CommandLineRunner {

	private final static Logger LOGGER = LoggerFactory.getLogger(KafkaBenchmarkApplication.class);

	@Autowired
	BenchmarkExecutor benchmarkExecutor;
	
	public static void main(String[] args) {
		SpringApplication.run(KafkaBenchmarkApplication.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		benchmarkExecutor.start();
	}

}
