package com.sptek.kafka.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.boot.SpringApplication;

import com.sptek.kafka.KafkaConsumerApplication;

public class PatternUtil {

	/*
	 * This definitely returns UTC time: as String and Date objects !
	 */
	static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static Date getUTCdatetimeAsDate() {
		// note: doesn't check for null
		return stringDateToDate(getUTCdatetimeAsString());
	}

	public static String getUTCdatetimeAsString() {
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		final String utcTime = sdf.format(new Date());

		return utcTime;
	}

	public static Date stringDateToDate(String StrDate) {
		Date dateToReturn = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);

		try {
			dateToReturn = (Date) dateFormat.parse(StrDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return dateToReturn;
	}

	/*
	 * 날짜 형식을 추출하는 함수. 아래 형태의 숫자를 추출
	 *
	 * yyyy-MM-dd hh:mm:ss.SSS yyyy-MM-dd hh:mm:ss
	 *
	 */
	public static Date extractDate(String str) throws ParseException {

		SimpleDateFormat DATE_FORMAT_1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
		SimpleDateFormat DATE_FORMAT_2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

		Matcher matcher;
		String patternStr1 = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}.\\d{3}"; // 날자를 패턴으로 지정 (YYYY-MM-DD
																					// HH:MM:SS.SSS)
		String patternStr2 = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"; // 날자를 패턴으로 지정 (YYYY-MM-DD HH:MM:SS)

		int flags = Pattern.MULTILINE | Pattern.CASE_INSENSITIVE;

		if (str.isEmpty()) {
			matcher = null;
		} else {

			Pattern pattern1 = Pattern.compile(patternStr1, flags);
			matcher = pattern1.matcher(str);

			while (matcher.find()) {
				return DATE_FORMAT_1.parse(matcher.group());
			}

			Pattern pattern2 = Pattern.compile(patternStr2, flags);
			matcher = pattern2.matcher(str);

			while (matcher.find()) {
				return DATE_FORMAT_2.parse(matcher.group());
			}

		}
		// 소스 정보에 타임이 없는 경우 시스템 현재 시간
		return DATE_FORMAT_2.parse(DATE_FORMAT_2.format(new Date()));
	}

	/*
	 * IP 정보를 추출하는 함수. 아래 형태의 숫자를 추출
	 *
	 * 172.16.11.125
	 *
	 */
	public static String extractIP(String str) throws ParseException {

		Matcher matcher;
		String patternStr1 = "([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})\\.([0-9]{1,3})";

		int flags = Pattern.MULTILINE | Pattern.CASE_INSENSITIVE;

		if (str.isEmpty()) {
			matcher = null;
		} else {

			Pattern pattern1 = Pattern.compile(patternStr1, flags);
			matcher = pattern1.matcher(str);

			while (matcher.find()) {
				return matcher.group();
			}

		}
		// 소스 정보에 IP 정보가 없는 경우 빈 정보 리턴
		return "";
	}

	public static void main(String[] args) throws ParseException {
		// String srcStr = "2021-08-03 15:58:56.344 INFO [9a8325a4d3cbd192] -
		// INT-GW-Log|9a8325a4d3cbd192|||200|internal-gateway|serverless02-vm04|hclee|/hanasvc/test/nice/eureka|localTestId|172.16.11.96|2021-08-03
		// 13:58:56|9||172.16.11.241";
		
		String srcStr = "LOG_TIME=2021-09-10 15:20:19 NODE_IP=172.16.11.248 MESSAGE=CPU User: 0.8% CPU Nice: 0.0% CPU System: 0.3% CPU Idle: 99.0% CPU IOwait: 0.0% CPU IRQ: 0.0% CPU SoftIRQ: 0.0% CPU Steal: 0.0% CPU load: 1.0% Physical Memory: Available: 459.3 MiB/7.6 GiB Virtual Memory: Swap Used/Avail: 0 bytes/0 bytes, Virtual Memory In Use/Max=7.2 GiB/3.8 GiB  / (Local Disk) [xfs] 43.6 GiB of 50.0 GiB free (87.3%), 26.2 M of 26.2 M files free (99.8%) is /dev/mapper/centos-root [/dev/dm-0] and is mounted at /  /dev/vda1 (Local Disk) [xfs] 786.4 MiB of 1014 MiB free (77.6%), 523.9 K of 524.3 K files free (99.9%) is /dev/vda1  and is mounted at /boot  /dev/mapper/centos-home (Local Disk) [xfs] 36.8 GiB of 41.1 GiB free (89.4%), 21.5 M of 21.6 M files free (99.7%) is /dev/mapper/centos-home [/dev/dm-2] and is mounted at /home  tmpfs (Ram Disk) [tmpfs] 3.8 GiB of 3.8 GiB free (100.0%), 1.0 M of 1.0 M files free (100.0%) is tmpfs  and is mounted at /var/lib/kubelet/pods/6e1d13a0-9ee5-449a-8620-7519c7e7a252/volumes/kubernetes.io~projected/kube-api-access-9s8rq";
		
		System.out.println("DATE PATTERN : " + extractDate(srcStr));
		System.out.println("IP PATTERN : " + extractIP(srcStr));
	}

}
