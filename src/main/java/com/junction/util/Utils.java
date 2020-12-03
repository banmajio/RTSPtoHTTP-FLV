package com.junction.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

/**
 * @Title Utils.java
 * @description 工具类
 * @time 2020年10月27日 上午9:15:56
 * @author wuguodong
 **/
public class Utils {
	private final static Logger logger = LoggerFactory.getLogger(Utils.class);

	/**
	 * @Title: IpConvert
	 * @Description:域名转ip
	 * @param domainName
	 * @return ip
	 **/
	public static String IpConvert(String domainName) {
		String ip = domainName;
		try {
			ip = InetAddress.getByName(domainName).getHostAddress();
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return domainName;
		}
		return ip;
	}

	/**
	 * @Title: CheckParameters
	 * @Description:接口参数非空校验
	 * @param cameraJson
	 * @param isNullArr
	 * @return boolean
	 **/
	public static boolean isNullParameters(JSONObject cameraJson, String[] isNullArr) {
		Map<String, Object> checkMap = new HashMap<>();
		// 空值校验
		for (String key : isNullArr) {
			if (null == cameraJson.get(key) || "".equals(cameraJson.get(key))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @Title: isTrueIp
	 * @Description:接口参数ip格式校验
	 * @param ip
	 * @return boolean
	 **/
	public static boolean isTrueIp(String ip) {
		return ip.matches("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
	}

	/**
	 * @Title: isTrueTime
	 * @Description:接口参数时间格式校验
	 * @param time
	 * @return boolean
	 **/
	public static boolean isTrueTime(String time) {
		try {
			new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").parse(time);
			return true;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
	}

	/**
	 * @Title: getTime
	 * @Description:获取转换后的时间
	 * @param time
	 * @return String
	 **/
	public static String getTime(String time) {
		String timestamp = null;
		try {
			timestamp = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
		} catch (Exception e) {
			logger.error("时间格式化错误");
			e.printStackTrace();
		}
		return timestamp;
	}

	/**
	 * @Title: getStarttime
	 * @Description:获取回放开始时间
	 * @param starttime
	 * @return starttime
	 **/
	public static String getStarttime(String time) {
		String starttime = null;
		try {
			starttime = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time).getTime() - 60 * 1000);
		} catch (Exception e) {
			logger.error("时间格式化错误");
			e.printStackTrace();
		}
		return starttime;
	}

	/**
	 * @Title: getEndtime
	 * @Description:获取回放结束时间
	 * @param endString
	 * @return endString
	 **/
	public static String getEndtime(String time) {
		String endString = null;
		try {
			endString = new SimpleDateFormat("yyyyMMddHHmmss")
					.format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time).getTime() + 60 * 1000);
		} catch (Exception e) {
			logger.error("时间格式化错误");
			e.printStackTrace();
		}
		return endString;
	}
}
