package com.sptek.es.vo;

import java.util.Date;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@ToString
public class LogCmpSearchData {

	private String mongo_id = "";
	private String index = "";
	
	private Date log_time;
	// private Date save_time;
	private String log_type;
	private String message = "";

	private String start_log_time = "";
	private String end_log_time = "";

}
