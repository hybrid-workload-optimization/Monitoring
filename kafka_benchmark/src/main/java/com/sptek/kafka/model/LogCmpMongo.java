package com.sptek.kafka.model;

import java.util.Date;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LogCmpMongo {
	
	//@Transient
    //public static final String SEQUENCE_NAME = "tomcat_access_sequence";

	//@Indexed
	//private String client_ip;
	
	// 노드 IP 추가
	private String node_ip;
	private Date log_time;
	private Date save_time;
	private String log_type;
	private String message;
	
}
