package com.sptek.es.vo;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
public class LogCmpTotal {
	
	long searchTime;
	int logCount;
	List<LogCmpData> LogCmpList;
	
	/**
	 * 유효한지 여부
	 */
	Boolean IS_VALID = true;

	/**
	 * 유효하지 않을때 메시지
	 */
	String INVALID_MSG = "";
	
}
