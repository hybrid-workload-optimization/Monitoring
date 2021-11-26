package com.sptek.es.service;

import java.io.IOException;

import org.springframework.stereotype.Service;

import com.sptek.es.vo.ElasticResult;
import com.sptek.es.vo.LogCmpData;
import com.sptek.es.vo.LogCmpSearchData;

@Service
public interface CmpService {
	
	// 검색 쿼리
	ElasticResult<LogCmpData> searchQuery( LogCmpSearchData logCmpSearchData, 
			                               String index, 
			                               Object[] searchAfter, 
			                               int size ) throws IOException;
	
}
