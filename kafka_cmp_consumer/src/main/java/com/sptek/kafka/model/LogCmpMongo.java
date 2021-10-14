package com.sptek.kafka.model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Document(collection = "cmp_log")
public class LogCmpMongo {
	
	//@Transient
    //public static final String SEQUENCE_NAME = "tomcat_access_sequence";

	//@Indexed
	//private String client_ip;
	
	@Id
	private ObjectId id;
	// 노드 IP 추가
	private String node_ip;
	private Date log_time;
	private Date save_time;
	private String log_type;
	private String message;
	
}
