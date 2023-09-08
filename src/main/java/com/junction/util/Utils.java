package com.junction.util;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;

/**
 * @author wuguodong
 * @Title Utils.java
 * @description 工具类
 * @time 2020年10月27日 上午9:15:56
 **/
public class Utils {
    private final static Logger logger = LoggerFactory.getLogger(Utils.class);

    /**
     * @description: 域名转ip
     * @author: banmajio
     * @date: 2023/9/8 11:06
     * @param: [domainName]
     * @return: java.lang.String
     */
    public static String ipConvert(String domainName) {
        String ip;
        try {
            ip = InetAddress.getByName(domainName).getHostAddress();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage());
            return domainName;
        }
        return ip;
    }

    /**
     * @description: 接口参数非空校验
     * @author: banmajio
     * @date: 2023/9/8 11:05
     * @param: [cameraJson, isNullArr]
     * @return: boolean
     */
    public static boolean isNullParameters(JSONObject cameraJson, String[] isNullArr) {
        // 空值校验
        for (String key : isNullArr) {
            if (null == cameraJson.get(key) || "".equals(cameraJson.get(key))) {
                return false;
            }
        }
        return true;
    }

    /**
     * @description: 接口参数ip格式校验
     * @author: banmajio
     * @date: 2023/9/8 11:05
     * @param: [ip]
     * @return: boolean
     */
    public static boolean isTrueIp(String ip) {
        return ip.matches("([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}");
    }

    /**
     * @description: 接口参数时间格式校验
     * @author: banmajio
     * @date: 2023/9/8 11:05
     * @param: [time]
     * @return: boolean
     */
    public static boolean isTrueTime(String time) {
        try {
            new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").parse(time);
            return false;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return true;
        }
    }

    /**
     * @description: 获取转换后的时间
     * @author: banmajio
     * @date: 2023/9/8 11:05
     * @param: [time]
     * @return: java.lang.String
     */
    public static String getTime(String time) {
        String timestamp = null;
        try {
            timestamp = new SimpleDateFormat("yyyyMMddHHmmss")
                    .format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
        } catch (Exception e) {
            logger.error("时间格式化错误");
        }
        return timestamp;
    }

    /**
     * @description: 获取回放开始时间
     * @author: banmajio
     * @date: 2023/9/8 11:05
     * @param: [time]
     * @return: java.lang.String
     */
    public static String getStartTime(String time) {
        String startTime = null;
        try {
            startTime = new SimpleDateFormat("yyyyMMddHHmmss")
                    .format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time).getTime() - 60 * 1000);
        } catch (Exception e) {
            logger.error("时间格式化错误");
        }
        return startTime;
    }

    /**
     * @description: 获取回放结束时间
     * @author: banmajio
     * @date: 2023/9/8 11:06
     * @param: [time]
     * @return: java.lang.String
     */
    public static String getEndtime(String time) {
        String endString = null;
        try {
            endString = new SimpleDateFormat("yyyyMMddHHmmss")
                    .format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time).getTime() + 60 * 1000);
        } catch (Exception e) {
            logger.error("时间格式化错误");
        }
        return endString;
    }
}
