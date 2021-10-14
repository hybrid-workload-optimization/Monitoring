package com.sptek.es.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sptek.es.vo.LogCmpSearchData;
import com.sptek.es.vo.LogCmpTotal;


/**
 * API 유효성 검사 클래스
 * 
 */
public class APIValidate {

	/** 로거 */
	private final static Logger LOGGER = LoggerFactory.getLogger(APIValidate.class);

	/**
     * searchLog
     * 
     * @return 유효하면 null, 아니면 정보를 담은 LogCmpTotal 리턴
     */
    @SuppressWarnings("unchecked")
    public static void searchLog(LogCmpSearchData logCmpSearchData, LogCmpTotal logCmpTotal) {
    	
    	LOGGER.debug("APIValidate : {}", logCmpSearchData);

        if (logCmpSearchData.getLog_type() == null && !logCmpSearchData.getLog_type().equals("")) {
            logCmpTotal.setINVALID_MSG("searchLog.log_type.isNull");
            logCmpTotal.setIS_VALID(false);
        } else if (logCmpSearchData.getStart_log_time() == null || logCmpSearchData.getStart_log_time().equals("")) {
        	logCmpTotal.setINVALID_MSG("searchLog.start_log_time.isNull");
        	logCmpTotal.setIS_VALID(false);
        } else if (logCmpSearchData.getEnd_log_time() == null || logCmpSearchData.getEnd_log_time().equals("")) {
        	logCmpTotal.setIS_VALID(false);
        	logCmpTotal.setINVALID_MSG("searchLog.end_log_time.isNull");
        }

    }
    
       
}
