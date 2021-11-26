package com.sptek.es.vo;

import java.util.ArrayList;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class LogCmpData {
	
	private String mongo_id = "";
	private String index = "";
	//private Date log_time;
	private String log_time;
	//private Date save_time;
	private String log_type;
	private String message = "";
	private String message_vector = "";
	
	//private String start_log_time = "";
	//private String end_log_time = "";
	
}
