package com.junction.cache;

import java.util.HashMap;
import java.util.Map;

import com.junction.pojo.CameraPojo;

/**
 * @Title CacheUtil.java
 * @description 推流缓存信息
 * @time 2019年12月17日 下午3:12:45
 * @author wuguodong
 **/
public final class CacheUtil {
	/*
	 * 保存已经开始推的流
	 */
	public static Map<String, CameraPojo> STREAMMAP = new HashMap<String, CameraPojo>();

	/*
	 * 保存服务启动时间
	 */
	public static long STARTTIME;

}
