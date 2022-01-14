package com.sptek.kafka.service;

import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import com.sptek.kafka.model.LogCmpMongo;
import com.sptek.kafka.repository.MongoRepository;
import com.sptek.kafka.util.PatternUtil;

@Service
public class KafKaConsumerService {

	private final Logger logger = LoggerFactory.getLogger(KafKaConsumerService.class);

	@Autowired
	private MongoRepository mongoRepository;
	
	@KafkaListener(topics = "${topic.name}", groupId = "${topic.group.id}", containerFactory = "kafkaListenerContainerFactory")
	public void consume(@Payload LogCmpMongo logCmpMongo,
                        @Headers MessageHeaders messageHeaders) {
		
		try {
			
			//logger.info("Received Message: " + logCmpMongo.toString() + " headers: " + messageHeaders);
	   		
			// UTC 방식 타임 저장
			//logCmpMongo.setSave_time(DateUtil.getUTCdatetimeAsDate());
			//logCmpMongo.setSave_time(new Date());
			logCmpMongo.setSave_time(new Date(System.currentTimeMillis()));
				
			// [LOG_TIME] 정규식 표현 값 비교 및 추출
			logCmpMongo.setLog_time(PatternUtil.extractDate(logCmpMongo.getMessage()));
			
			// [NODE_IP] 정규식 표현 값 비교 및 추출
			logCmpMongo.setNode_ip(PatternUtil.extractIP(logCmpMongo.getMessage()));
		
			// Mongo DB 데이터 INSERT
			mongoRepository.save(logCmpMongo);
			
		} catch (ParseException e) {
			logger.error("CMP Date Extract Error -> %s", e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			logger.error("CMP MongoDB Save Error -> %s", e.getMessage());
			e.printStackTrace();
		} 
		
	}
	
	
}