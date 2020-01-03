package com.junction.pojo;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Title ConfigPojo.java
 * @description 读取配置文件的bean
 * @time 2019年12月25日 下午5:11:21
 * @author wuguodong
 **/
@Component
@ConfigurationProperties(prefix = "config")
public class Config {
	private String keepalive;// 保活时长（分钟）
	private String push_ip;// 推送地址
	private String push_port;// 推送端口

	public String getKeepalive() {
		return keepalive;
	}

	public void setKeepalive(String keepalive) {
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
