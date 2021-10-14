package com.sptek.hwinfo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;


@Configuration
@SpringBootApplication
@PropertySource("file:${user.dir}/application.properties")
@EnableScheduling
@EnableAsync
@EnableAutoConfiguration
@Component
public class HardwareInfoApplication  implements CommandLineRunner {

	private final static Logger LOGGER = LoggerFactory.getLogger(HardwareInfoApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(HardwareInfoApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@PostConstruct
    public void init() throws Exception{

		
		// 샘플 로그 파일 불러오기 (2018-05-16)
		//LogFileToList.getDsLogFileToList();
		
		// [초기화 메서드]
    	LOGGER.info("[Sptek] Initialize.");
    	
    }
    
    @PreDestroy
    public static void close()	{
    	
    	// [자원 반환등 종료]
    	LOGGER.info("[Sptek] Finalize.");
    	
    	LOGGER.info("[Sptek] Threadpool finalize.");
    	//taskExecutor.shutdown();
    	//taskExecutor = null;
    	
    }

}
