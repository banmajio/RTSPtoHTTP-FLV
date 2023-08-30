package com.junction.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.junction.pojo.CameraPojo;
import com.junction.push.CameraPush;

/**
 * @Title CacheUtil.java
 * @description 推流缓存信息
 * @time 2019年12月17日 下午3:12:45
 * @author banmajio
 **/
public final class CacheUtil {
	/**
	 * @description: 保存已经开始的推流
	 * @author: banmajio
	 * @date: 2023/8/30 09:11
	 */
	public static Map<String, CameraPojo> STREATMAP = new ConcurrentHashMap<>();

	/**
	 * @description: 保存push
	 * @author: banmajio
	 * @date: 2023/8/30 09:12
	 */
	public static Map<String, CameraPush> PUSHMAP = new ConcurrentHashMap<>();
	
	/**
	 * @description: 保存服务启动时间
	 * @author: banmajio 
	 * @date: 2023/8/30 09:13
	 */
	public static long STARTTIME;

}
