package com.sptek.kafka.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
public class ThreadPoolConfiguration {
     
	private final static Logger LOGGER = LoggerFactory.getLogger(ThreadPoolConfiguration.class);
	
    @Value("${spring.threadpool.name}")
	private String threadPoolName;
    
	@Value("${spring.threadpool.init.size}")
	private int threadPoolInitSize;
	
	@Value("${spring.threadpool.max.size}")
	private int threadPoolMaxSize;
	
	@Value("${spring.threadpool.queue.capacity}")
	private int threadPoolQueueCapacity;
	
	@Value("${node.ip}")
	private String nodeIP;
     
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        
        /*
    	 * 내장된 서블릿 컨테이너(Tomcat)가 관리하는 독립적인 1개의 Worker 쓰레드에 의해 동기 방식으로 실행된다. 
    	 * 또 다른 독립적인 Worker 스레드로 실행된다. 
    	 */
    	LOGGER.info("==================================================");
    	LOGGER.info("[Sptek] Threadpool initialize.");
    	LOGGER.info("[Sptek] Threadpool Name : " + threadPoolName);
    	LOGGER.info("[Sptek] Threadpool Init Size : " + threadPoolInitSize);
    	LOGGER.info("[Sptek] Threadpool Max Size : " + threadPoolMaxSize);
    	LOGGER.info("[Sptek] Threadpool Queue Capacity : " + threadPoolQueueCapacity);
    	LOGGER.info("[Sptek] Threadpool NODE IP : " + nodeIP);
    	LOGGER.info("==================================================");
    	
    	ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
    	pool.setThreadNamePrefix(threadPoolName);
    	pool.setCorePoolSize(threadPoolInitSize);
    	pool.setMaxPoolSize(threadPoolMaxSize);
    	pool.setQueueCapacity(threadPoolQueueCapacity);
        
        LOGGER.info("[Sptek] Threadpool instantiation.");

        return pool;
    }
}