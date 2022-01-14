package com.sptek.kafka.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.sptek.kafka.model.LogCmpMongo;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class KafkaProducerService {

	private final KafkaTemplate<String, LogCmpMongo> logCmpKafkaTemplate;
	
	public void sendMessage(LogCmpMongo logCmpMongo) {
		//System.out.println("send message : " + logCmpKafka.toString());
		this.logCmpKafkaTemplate.send("bmt", logCmpMongo);
	}
}