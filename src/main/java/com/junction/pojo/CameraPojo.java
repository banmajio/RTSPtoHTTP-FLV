package com.junction.pojo;

import java.io.Serializable;

/**
 * 相机Pojo
 * 摄像头Pojo
 *
 * @author wuguodong
 * @date
 */
public class CameraPojo implements Serializable {

    private static final long serialVersionUID = 8183688502930584159L;

    /**
     * 摄像头账号
     */
    private String username;

    /**
     * 摄像头密码
     */
    private String password;

    /**
     * 摄像头ip
     */
    private String ip;

    /**
     * 摄像头通道
     */
    private String channel;

    /**
     * 摄像头码流
     */
    private String stream;

    /**
     * rtsp地址
     */
    private String rtsp;

    /**
     * rtmp地址
     */
    private String rtmp;

    /**
     * 回放开始时间
     */
    private String startTime;

    /**
     * 回放结束时间
     */
    private String endTime;

    /**
     * 打开时间
     */
    private String openTime;

    /**
     * 使用人数
     */
    private int count = 0;

    /**
     * token令牌
     */
    private String token;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getStream() {
        return stream;
    }

    public void setStream(String stream) {
        this.stream = stream;
    }

    public String getRtsp() {
        return rtsp;
    }

    public void setRtsp(String rtsp) {
        this.rtsp = rtsp;
    }

    public String getRtmp() {
        return rtmp;
    }

    public void setRtmp(String rtmp) {
        this.rtmp = rtmp;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
