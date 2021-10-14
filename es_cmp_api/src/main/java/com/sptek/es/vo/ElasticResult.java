package com.sptek.es.vo;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
public class ElasticResult<T extends LogCmpData> { 
	
	/*
	 * 최종 결과물로 사용될 ElasticResult 클래스
	 */
	
	// 현재 요청이 마지막인지 표시
	private boolean isLast;
	// 결과값 전체 total
	private long total;
	// 결과 list
	private List<T> list;
	// 다음 요청을 위해 보내줘야 하는 cursor값
	private Object[] cursor;
	
}

