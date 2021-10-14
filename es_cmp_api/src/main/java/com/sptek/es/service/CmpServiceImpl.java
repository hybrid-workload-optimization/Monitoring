package com.sptek.es.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHits;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.sptek.es.provider.ElasticsearchProvider;
import com.sptek.es.vo.ElasticResult;
import com.sptek.es.vo.LogCmpData;
import com.sptek.es.vo.LogCmpSearchData;

@Component
public class CmpServiceImpl implements CmpService {

	@Autowired
	ElasticsearchProvider elasticsearchProvider;
	
	@Override
	public ElasticResult<LogCmpData> searchQuery( LogCmpSearchData logCmpSearchData,
			                                      String index, 
			                                      Object[] searchAfter, 
			                                      int size) throws IOException {
		
		SearchResponse searchResponse = elasticsearchProvider.searchQuery(logCmpSearchData, index, searchAfter, size); 
		SearchHits searchHits = searchResponse.getHits(); 
		
		int hitCnt = searchHits.getHits().length; 
		boolean isLast = 0 == hitCnt || size > hitCnt; 
		
		return ElasticResult.<LogCmpData>builder() 
				.cursor(isLast ? null : searchHits.getHits()[hitCnt - 1].getSortValues()) 
				.isLast(isLast) .list(extractProductList(searchHits)) 
				.total(searchHits.getTotalHits().value) 
				.build();

	}
	
	private List<LogCmpData> extractProductList(SearchHits searchHits) { 
		
		List<LogCmpData> logCmpList = new ArrayList<>(); 
		
		searchHits.forEach(hit -> { 
			
			Map<String, Object> result = hit.getSourceAsMap(); 
			
			logCmpList.add(LogCmpData.builder() 
					.index(String.valueOf(hit.getIndex()))      // INDEX
					.mongo_id(String.valueOf(result.get("mongo_id")))
					.log_time(String.valueOf(result.get("log_time"))) 
					.log_type(String.valueOf(result.get("log_type")))
					//.updateAt(Long.valueOf(result.get("updateAt").toString())) 
					.message(String.valueOf(result.get("message"))).build()); 
			});
		
		return logCmpList; 
	}


}
