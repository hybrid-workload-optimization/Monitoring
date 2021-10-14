package com.sptek.es.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sptek.es.service.APIValidate;
import com.sptek.es.service.CmpService;
import com.sptek.es.util.DateUtil;
import com.sptek.es.vo.ElasticResult;
import com.sptek.es.vo.LogCmpData;
import com.sptek.es.vo.LogCmpSearchData;
import com.sptek.es.vo.LogCmpTotal;

@RestController
@RequestMapping("/cmpapi")
public class CmpController {

	private final static Logger LOGGER = LoggerFactory.getLogger(CmpController.class);
	 
	@Value("${api.result.limit}")
    private int apiResultLimit;
	
	@Autowired
	CmpService cmpService;

	@PostMapping("/search")
	public LogCmpTotal searchLog(@RequestBody LogCmpSearchData logCmpSearchData) throws IOException {
		
		long beforeTime = System.currentTimeMillis();
		
		LogCmpTotal logCmpTotal = new LogCmpTotal();
		
		// 0. 유효성 검사
		APIValidate.searchLog(logCmpSearchData, logCmpTotal);
		if (logCmpTotal.getIS_VALID() == false) {
			return logCmpTotal;
		}
		
		// 인덱스 이름, 사이즈 설정
		ElasticResult<LogCmpData> result = 
				      cmpService.searchQuery(logCmpSearchData, "sptek-logs-*", new Object[]{}, apiResultLimit); 
		List<LogCmpData> logCmpDataList = new ArrayList<>();

		// Search After 쿼리를 추후 동기식으로 구현
		while(result != null && !result.isLast()) { 
			
			logCmpDataList.addAll(result.getList()); 			
			result = cmpService.searchQuery(logCmpSearchData, "sptek-logs-*", result.getCursor(), apiResultLimit); 
		}
			
		logCmpDataList.addAll(result.getList());
		logCmpTotal.setLogCount(logCmpDataList.size());
		
		// 전체 리스트 시간 포멧 변경 (UTC - > Location Time Zone)
		for (LogCmpData logCmpData : logCmpDataList) {
			logCmpData.setLog_time(DateUtil.convertToCurrentTimeZone(logCmpData.getLog_time()));
		}
				
		logCmpTotal.setLogCmpList(logCmpDataList);
		
		long afterTime = System.currentTimeMillis(); 
		
		// seconds
		//long secDiffTime = (afterTime - beforeTime) / 1000;
		// miliseconds
		long secDiffTime = (afterTime - beforeTime);
		logCmpTotal.setSearchTime(secDiffTime);
		
		return logCmpTotal;
	}
	
}
