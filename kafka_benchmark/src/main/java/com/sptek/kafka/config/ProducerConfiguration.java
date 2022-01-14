package com.sptek.kafka.config;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.sptek.kafka.model.LogCmpMongo;

@EnableKafka
@Configuration
public class ProducerConfiguration {

	@Value(value = "${kafka.bootstrapAddress}")
	private String bootstrapAddress;

	@Bean
    public Map<String, Object> logCmpProducerConfigs() {
        Map<String, Object> configProperties = new HashMap<>();
        configProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return configProperties;
    }
	
	@Bean
    public ProducerFactory<String, LogCmpMongo> logCmpProducerFactory() {
        return new DefaultKafkaProducerFactory<>(logCmpProducerConfigs());
    }
	
	@Bean
    public KafkaTemplate<String, LogCmpMongo> logCmpKafkaTemplate() {
        return new KafkaTemplate<>(logCmpProducerFactory());
    }

}