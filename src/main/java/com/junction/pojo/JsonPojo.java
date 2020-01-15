package com.junction.pojo;

import java.io.Serializable;

/**
 * @author wuguodong
 * @Title JsonPojo.java
 * @description 通用POJO
 * @time 2019年12月16日 上午9:32:08
 **/
public class JsonPojo implements Serializable {

    /**
     * 对象
     */
    private Object obj;

    /**
     * 状态码
     */
    private String code;

    /**
     * 信息
     */
    private String msg;

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
