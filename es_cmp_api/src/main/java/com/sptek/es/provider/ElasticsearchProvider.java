package com.sptek.es.provider;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.sptek.es.controller.CmpController;
import com.sptek.es.vo.LogCmpSearchData;

@Component
public class ElasticsearchProvider {

	private final static Logger LOGGER = LoggerFactory.getLogger(ElasticsearchProvider.class);
	
	final RestHighLevelClient restHighLevelClient;

	public ElasticsearchProvider(RestHighLevelClient restHighLevelClient) {
		this.restHighLevelClient = restHighLevelClient;
	}


	public SearchResponse searchQuery( LogCmpSearchData logCmpSearchData, 
			                           String index, 
			                           Object[] searchAfter, 
			                           int size) throws IOException {
		
		// [Bool Query] Builder
		BoolQueryBuilder bqb = QueryBuilders.boolQuery();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		// searchSourceBuilder.query(QueryBuilders.typeQuery("coffee"));

		/*
		 * - gte (Greater-than or equal to) - 이상 (같거나 큼) - gt (Greater-than) – 초과 (큼) -
		 * lte (Less-than or equal to) - 이하 (같거나 작음) - lt (Less-than) - 미만 (작음)
		 */
		String[] includeFields = new String[] { "*" };
		String[] excludeFields = new String[] { "@timestamp" };

		searchSourceBuilder.fetchSource(includeFields, excludeFields);

		// [로그 내용] 검색
		if(logCmpSearchData.getMessage() != null && !logCmpSearchData.getMessage().equalsIgnoreCase(""))
			//bqb.must(QueryBuilders.queryStringQuery(logCmpSearchData.getMessage()).field("message"));
			bqb.must(QueryBuilders.matchPhraseQuery("message", logCmpSearchData.getMessage()));
		// [로그 타입] 검색
		bqb.filter(QueryBuilders.termQuery("log_type", logCmpSearchData.getLog_type()));

		// [날짜 검색]
		String searchStartDt = logCmpSearchData.getStart_log_time();
		String searchEndDt = logCmpSearchData.getEnd_log_time();

		try {

			if ((searchStartDt != null && !searchStartDt.equalsIgnoreCase(""))
					|| (searchEndDt != null && !searchEndDt.equalsIgnoreCase(""))) {

				SimpleDateFormat originFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				SimpleDateFormat endFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				// SimpleDateFormat startFormat = new SimpleDateFormat("yyyy-MM-dd 00:00:00");
				// SimpleDateFormat endFormat = new SimpleDateFormat("yyyy-MM-dd 23:59:59");

				// [시작일] 만 적용
				if ((searchStartDt != null && !searchStartDt.equalsIgnoreCase(""))
						&& (searchEndDt == null || searchEndDt.equalsIgnoreCase(""))) {

					Date originDate = originFormat.parse(searchStartDt);
					String newStartDate = startFormat.format(originDate);
					bqb.filter(QueryBuilders.rangeQuery("log_time").from(newStartDate));
					// [종료일] 만 적용
				} else if ((searchStartDt != null && !searchStartDt.equalsIgnoreCase(""))
						&& (searchEndDt == null || searchEndDt.equalsIgnoreCase(""))) {

					Date originDate = originFormat.parse(searchEndDt);
					String newEndDate = endFormat.format(originDate);
					bqb.filter(QueryBuilders.rangeQuery("log_time").to(newEndDate));
					// [시작,종료일] 둘다 적용
				} else {
					Date originDateStart = originFormat.parse(searchStartDt);
					Date originDateEnd = originFormat.parse(searchEndDt);
					String newStartDate = startFormat.format(originDateStart);
					String newEndDate = endFormat.format(originDateEnd);
					// [UTC] 시간으로 [KST] 시간으로 변경 후 검색
					bqb.filter(QueryBuilders.rangeQuery("log_time")
							.from(newStartDate).to(newEndDate)
							.format("yyyy-MM-dd HH:mm:ss")
							.timeZone("+09:00"));
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		// [인덱스] 설정
		//SearchRequest searchRequest = new SearchRequest("sptek-logs-*");
		SearchRequest searchRequest = new SearchRequest(index);
				
		// [sort] 필드 사용 (search after)
		Map<String, SortOrder> sorts = new HashMap<String, SortOrder>() {
			{
				put("mongo_id", SortOrder.DESC);  // mongo_id 내림차순
				put("log_time", SortOrder.DESC);  // log_time 내림차순
			}
		};
			
		// [Search After] 쿼리
		try {
			searchSourceBuilder = ElasticsearchHandler.searchAfter(sorts, bqb, searchAfter, size);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//searchSourceBuilder.query(bqb);
		//LOGGER.info("Query DSL > " + searchSourceBuilder.query().toString());
				
		// Document 사이즈 (1,000)
		//searchSourceBuilder.size(1000);
		
		// 타임아웃 (60초)
		searchSourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

		searchRequest.source(searchSourceBuilder);
		
		return restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
	}

}