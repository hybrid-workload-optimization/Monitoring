package com.sptek.es.provider;

import java.util.Map;

import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

/*
 * 자주 사용되는 Elasticsearch 문법을 처리하기 위해서 만들어 놓은 ElasticsearchHandler 
 * search after는 sort 필드가 없으면 사용이 불가능 하기 때문에 sort 필드가 없는 경우 에러를 전달한다.
 */
public class ElasticsearchHandler {

	public static SearchSourceBuilder searchAfter( Map<String, SortOrder> sortFields, 
			                                       QueryBuilder query,
			                                       Object[] searchAfter, int size) throws Exception {
		return searchAfterBuilder(sortFields, query, searchAfter, size);
	}

	public static SearchSourceBuilder searchAfter( Map<String, SortOrder> sortFields, 
			                                       QueryBuilder query,
			                                       Object[] searchAfter) throws Exception {
		return searchAfterBuilder(sortFields, query, searchAfter, 20);
	}

	private static SearchSourceBuilder searchAfterBuilder( Map<String, SortOrder> sortFields, 
			                                               QueryBuilder query, 
			                                               Object[] searchAfter, 
			                                               int size) throws Exception { 
		SearchSourceBuilder builder = new SearchSourceBuilder(); 
		if (sortFields.isEmpty()) { 
			throw new Exception("Invalid sort field request");
		} 
		sortFields.forEach((field, sort) -> { builder.sort(field, sort); }); 
		
		builder.size(size); 
		builder.query(query); 
		
		if (searchAfter.length != 0) {
			builder.searchAfter(searchAfter); 
		} 
	
		return builder; 
	}

}
