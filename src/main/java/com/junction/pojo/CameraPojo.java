package com.junction.pojo;

import java.io.Serializable;

public class CameraPojo implements Serializable {
	private static final long serialVersionUID = 8183688502930584159L;
	private String username;// 摄像头账号
	private String password;// 摄像头密码
	private String ip;// 摄像头ip
	private String channel;// 摄像头通道
	private String stream;// 摄像头码流
	private String rtsp;// rtsp地址
	private String rtmp;// rtmp地址
	private String url;// 播放地址
	private String starttime;// 回放开始时间
	private String endtime;// 回放结束时间
	private String opentime;// 打开时间
	private int count = 0;// 使用人数
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

	public String getStarttime() {
		return starttime;
	}

	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	public String getEndtime() {
		return endtime;
	}

	public void setEndTime(String endtime) {
		this.endtime = endtime;
	}

	public String getOpentime() {
		return opentime;
	}

	public void setOpentime(String opentime) {
		this.opentime = opentime;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "CameraPojo [username=" + username + ", password=" + password + ", ip=" + ip + ", channel=" + channel
				+ ", stream=" + stream + ", rtsp=" + rtsp + ", rtmp=" + rtmp + ", url=" + url + ", starttime="
				+ starttime + ", endtime=" + endtime + ", opentime=" + opentime + ", count=" + count + ", token="
				+ token + "]";
	}

}
