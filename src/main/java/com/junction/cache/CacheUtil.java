package com.junction.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.junction.pojo.CameraPojo;
import com.junction.push.CameraPush;

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
	public static Map<String, CameraPojo> STREATMAP = new ConcurrentHashMap<String, CameraPojo>();

	/*
	 * 保存push
	 */
	public static Map<String, CameraPush> PUSHMAP = new ConcurrentHashMap<>();
	/*
	 * 保存服务启动时间
	 */
	public static long STARTTIME;

}
