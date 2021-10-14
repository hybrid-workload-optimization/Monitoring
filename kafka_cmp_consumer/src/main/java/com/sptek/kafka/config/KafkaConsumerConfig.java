package com.sptek.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import com.sptek.kafka.model.LogCmpMongo;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
	
	@Value(value = "${kafka.bootstrapAddress}")
	private String bootstrapAddress;

	@Value(value = "${topic.group.id}")
	private String groupId;

	/*
	* Broker : 카프카 에플리케이션 서버 단위

	* Topic : 데이터 분리 단위

	* Topic Group : 토픽 그룹별로 offset을 관리 할 수 있다.

	* Partition : 하나의 토픽은 여러개의 Partition 으로 구성 되어 있다. 
	              늘리면 다시 줄일 수 없다. 
	              Consumer개수를 늘려서 분산처리를 할 수 있게 한다.
	*/
	// Consume user objects from Kafka
	public ConsumerFactory<String, LogCmpMongo> consumerFactory() {
		Map<String, Object> props = new HashMap<>();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
		props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
		return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
				new JsonDeserializer<>(LogCmpMongo.class));
	}

	@Bean
	public ConcurrentKafkaListenerContainerFactory<String, LogCmpMongo> kafkaListenerContainerFactory() {
		ConcurrentKafkaListenerContainerFactory<String, LogCmpMongo> factory = new ConcurrentKafkaListenerContainerFactory<>();
		factory.setConsumerFactory(consumerFactory());
		// Consumer 인스턴스 3개를 만든다.(파티션이 나눠줘야 한다.)
		// Consumer 는 스레드로부터 안전하지 않다. 
		// ConcurrentKafkaListenerContainerFactory API는 Kafka Consumer API를 동시에 사용하는 방법을 제공
		//factory.setConcurrency(1);
	    //factory.getContainerProperties().setPollTimeout(3000);
		return factory;
	}
	

}
