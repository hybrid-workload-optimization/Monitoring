package com.sptek.kafka.executor;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.sptek.kafka.executor.task.BenchmarkTask;
import com.sptek.kafka.service.KafkaProducerService;


@Component
public class BenchmarkExecutor {
	
	/**
     * 로거
     */
    private final static Logger LOGGER = LoggerFactory.getLogger(BenchmarkExecutor.class);
	    
    @Value("${spring.threadpool.init.size}")
	private int threadPoolInitSize;
    
    @Value("${task.document.count}")
	private int taskDocumentCount;
    
    @Value(value = "${node.ip}")
	private String nodeIP;
    
	@Autowired
	ThreadPoolTaskExecutor taskExecutor;
	
	@Autowired
	KafkaProducerService kafkaProducerService;
	
	
	private String benchmarkStartTime = null;
	
    public BenchmarkExecutor() {
    	
    }
    
    public void start() throws InterruptedException, ExecutionException {
        
    	long startTime = System.currentTimeMillis();
    	
    	//LOGGER.info("[START] Log to TXT make file");
    	
    	List<Future<String>> futureList = new ArrayList<>();
    	 
    	for (int i=0; i<threadPoolInitSize; i++) {
    		
    		SimpleDateFormat format1 = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        	Date time = new Date();
        	benchmarkStartTime = format1.format(time);
        	
        	BenchmarkTask benchmarkTask = new BenchmarkTask(kafkaProducerService, taskDocumentCount, 100, benchmarkStartTime, nodeIP);
        	Future<String> result = taskExecutor.submit(benchmarkTask);
        	futureList.add(result);
        	
    	}
    	 	
    	    
    	int sessionNum = 1;
    	
    	// 종료 시점 체크 
        for( Future<String> future : futureList )
        {
        	
        	try {
        		
        		//LOGGER.info(" Result MSG : " + future.get().toString());
        		String[] resultMsg = future.get().split(",");
    			long endTime = System.currentTimeMillis(); 
    			LOGGER.info("[Client Num "+ sessionNum++ + "] " + " COMPLETE " 
    					+ ", PROCESS SECONDS : " + (endTime-startTime)/1000);
        		
			} catch (Exception e) {
				e.printStackTrace();
			} 
        	
        }
        
    	long endTime = System.currentTimeMillis(); 
    	LOGGER.info("[FINAL END] Process Seconds ==========> " + (endTime-startTime)/1000 + " seconds");
    	System.exit(0);    // 시스템 강제 종료
    	
    }
    
  
          
}
