package com.junction.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Ip工具类
 *
 * @author wuguodong
 * @date
 */
public class IpUtil {

    /**
     * ip转换
     *
     * @param domainName 域名
     * @return {@link String}
     */
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

}
