package com.sptek.kafka.executor.task;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.sptek.kafka.model.LogCmpMongo;
import com.sptek.kafka.service.KafkaProducerService;


public class BenchmarkTask implements Callable<String> {

	final static Logger LOGGER = LoggerFactory.getLogger(BenchmarkTask.class);
	
	private String nodeIP;
	
	KafkaProducerService kafkaProducerService;
	
	String startBenchmarkTime = "";
	
	String resultMsg = "";
	
	long timeSleep = 0;
	
	int totalCnt = 0;
	
	boolean isError = false;   // 종료 시점 체크 여부 (2018.08.07)
	
	public BenchmarkTask(
							KafkaProducerService kafkaProducerService,
							int totalCnt,
							int timeSleep, 
			                String startLogTime,
			                String nodeIP) {
		
		this.kafkaProducerService = kafkaProducerService;
		this.totalCnt = totalCnt;
		this.timeSleep = timeSleep;
		this.startBenchmarkTime = startLogTime;
		this.nodeIP = nodeIP;
	}
	
	@Override
	public String call() throws IOException, InterruptedException, ParseException, SQLException {
		
		// 스레드 Sleep 시간 설정
		Thread.sleep(this.timeSleep);
		process();
		
		if (isError == true) {
			resultMsg = resultMsg + ",BENCHMARK_ERROR";
		} else {
			resultMsg = resultMsg + ",BENCHMARK_COMPLETE";
		}
		
		return resultMsg;
	}
	
	private void process() throws InterruptedException, SQLException { 
		
		// 현재 시간 설정 (LOG_TIME=2021-12-07 00:00:10)
		SimpleDateFormat dateFormat    = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String message = "LOG_TIME=" + dateFormat.format(new Date()) + " NODE_IP=" + nodeIP + " MESSAGE=CPU User: 2.0% CPU Nice: 0.0% CPU System: 3.3% CPU Idle: 65.9% CPU IOwait: 28.5% CPU IRQ: 0.0% CPU SoftIRQ: 0.3% CPU Steal: 0.0% CPU load: 5.6% Physical Memory: Available: 2.2 GiB/7.6 GiB Virtual Memory: Swap Used/Avail: 0 bytes/0 bytes, Virtual Memory In Use/Max=5.4 GiB/3.8 GiB  / (Local Disk) [xfs] 29.4 GiB of 50.0 GiB free (58.9%), 26.2 M of 26.2 M files free (99.8%) is /dev/mapper/centos-root [/dev/dm-0] and is mounted at /  /dev/vda1 (Local Disk) [xfs] 786.4 MiB of 1014 MiB free (77.6%), 523.9 K of 524.3 K files free (99.9%) is /dev/vda1  and is mounted at /boot  /dev/mapper/centos-home (Local Disk) [xfs] 32.0 GiB of 41.1 GiB free (77.8%), 21.6 M of 21.6 M files free (100.0%) is /dev/mapper/centos-home [/dev/dm-2] and is mounted at /home  tmpfs (Ram Disk) [tmpfs] 3.8 GiB of 3.8 GiB free (100.0%), 1.0 M of 1.0 M files free (100.0%) is tmpfs  and is mounted at /var/lib/kubelet/pods/5a9a0eda-4ee1-4002-a53d-cb2ed6b2249f/volumes/kubernetes.io~projected/kube-api-access-k5sp7  tmpfs (Ram Disk) [tmpfs] 3.8 GiB of 3.8 GiB free (100.0%), 1.0 M of 1.0 M files free (100.0%) is tmpfs  and is mounted at /var/lib/kubelet/pods/ff315574-97bc-4d50-9e90-415ff8042634/volumes/kubernetes.io~projected/kube-api-access-f8w7g  tmpfs (Ram Disk) [tmpfs] 3.8 GiB of 3.8 GiB free (100.0%), 1.0 M of 1.0 M files free (100.0%) is tmpfs  and is mounted at /var/lib/kubelet/pods/2d8fb875-1d59-4495-a770-a5d9dadf49c7/volumes/kubernetes.io~projected/kube-api-access-qqdld";;
		
		long startTime = System.currentTimeMillis();
		
		LogCmpMongo logCmp = LogCmpMongo.builder()
				.message(message)
				.log_type("HW-Log")
				.build();

		//LOGGER.info("logCmp object info : " + logCmp.toString());
		
		try {
			
			//Thread.sleep(1000);
					
			IntStream.rangeClosed(1, totalCnt).forEach(i -> {
				kafkaProducerService.sendMessage(logCmp);
	        });
	
		} catch (Exception e) {
			e.printStackTrace();
			isError = true;
		}
		
		
    	long endTime = System.currentTimeMillis(); 
    	LOGGER.info("[END] Kafka Benchmark COMPLETE, Process Seconds : " + (endTime-startTime)/1000 +", Seconds.");
    	
	}

	//get the current time zone
    public String getCurrentTimeZone() {
        TimeZone tz = Calendar.getInstance().getTimeZone();
        System.out.println(tz.getDisplayName());
        return tz.getID();
    }
   
}
