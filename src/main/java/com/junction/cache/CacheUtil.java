package com.junction.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.junction.pojo.CameraPojo;
import com.junction.pojo.Config;

/**
 * 缓存工具类
 *
 * @author wuguodong
 * @Title CacheUtil.java
 * @description 推流缓存信息
 * @time 2019年12月17日 下午3:12:45
 * @date
 */
public final class CacheUtil {

    /**
     * 保存已经开始推的流
     */
    public static Map<String, CameraPojo> STREAMMAP = new ConcurrentHashMap<String, CameraPojo>();

    /**
     * 保存服务启动时间
     */
    public static long STARTTIME;

}
