package com.junction.pojo;

import java.io.Serializable;

/**
 * @Title JsonPojo.java
 * @description 通用POJO
 * @time 2019年12月16日 上午9:32:08
 * @author wuguodong
 **/
public class JsonPojo implements Serializable {
	private Object obj;// 对象
	private String code;// 状态码
	private String msg;// 信息

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

}
