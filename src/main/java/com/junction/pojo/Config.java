package com.junction.pojo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author wuguodong
 * @Title ConfigPojo.java
 * @description 读取配置文件的bean
 * @time 2019年12月25日 下午5:11:21
 **/
@Component
@ConfigurationProperties(prefix = "config")
public class Config {

    /**
     * 保活时长（分钟）
     */
    private Integer keepalive;

    /**
     * 推送地址
     */
    private String push_ip;

    /**
     * 推送端口
     */
    private String push_port;

    public Integer getKeepalive() {
        return keepalive;
    }

    public void setKeepalive(Integer keepalive) {
        this.keepalive = keepalive;
    }

    public String getPush_ip() {
        return push_ip;
    }

    public void setPush_ip(String push_ip) {
        this.push_ip = push_ip;
    }

    public String getPush_port() {
        return push_port;
    }

    public void setPush_port(String push_port) {
        this.push_port = push_port;
    }

    @Override
    public String toString() {
        return "Config [keepalive=" + keepalive + ", push_ip=" + push_ip + ", push_port=" + push_port + "]";
    }

}
