package com.junction.controller;

import com.junction.cache.CacheUtil;
import com.junction.pojo.CameraPojo;
import com.junction.pojo.Config;
import com.junction.thread.CameraThread;
import com.junction.util.IpUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 摄像头接口
 *
 * @author wuguodong
 * @Title CameraController.java
 * @description controller
 * @time 2019年12月16日 上午9:00:27
 */
@Api(value = "摄像头接口", tags = "RTSP转RTMP")
@RestController
public class CameraController {

    /**
     * 配置文件bean
     */
    @Autowired
    public Config config;

    /**
     * 存放任务 线程
     */
    public static Map<String, CameraThread.MyRunnable> jobMap = new HashMap<String, CameraThread.MyRunnable>();

    /**
     * 开启视频流
     *
     * @param pojo pojo
     * @return Map<String, String>
     */
    @ApiOperation(value = "开启视频流", notes = "注意：请以application/json方式发送请求\r\n" +
            "请求示例：\r\n" +
            "   ip：设备IP\n" +
            "   username：设备用户名\n" +
            "   password：设备密码\n" +
            "   channel：通道号\n" +
            "   stream：码流(直播流需要指定码流；历史流不需要指定码流)\t\n" +
            "   starttime：开始时间(直播流没有开始时间)\n" +
            "   endtime：结束时间(直播流没有结束时间)")
    @PostMapping(value = "/cameras")
    public Map<String, String> openCamera(@RequestBody CameraPojo pojo) {
        // 返回结果
        Map<String, String> map = new HashMap<String, String>();
        // 校验参数
        if (null != pojo.getIp() && "" != pojo.getIp() && null != pojo.getUsername() && "" != pojo.getUsername()
                && null != pojo.getPassword() && "" != pojo.getPassword() && null != pojo.getChannel()
                && "" != pojo.getChannel()) {
            CameraPojo cameraPojo = new CameraPojo();
            // 获取当前时间
            String openTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime());
            Set<String> keys = CacheUtil.STREAMMAP.keySet();
            // 缓存是否为空
            if (0 == keys.size()) {
                // 开始推流
                cameraPojo = openStream(pojo.getIp(), pojo.getUsername(), pojo.getPassword(), pojo.getChannel(),
                        pojo.getStream(), pojo.getStartTime(), pojo.getEndTime(), openTime);
                map.put("token", cameraPojo.getToken());
                map.put("url", cameraPojo.getRtmp());
            } else {
                // 是否存在的标志；0：不存在；1：存在
                int sign = 0;
                if (null == pojo.getStartTime()) {// 直播流
                    for (String key : keys) {
                        if (pojo.getIp().equals(CacheUtil.STREAMMAP.get(key).getIp())
                                && pojo.getChannel().equals(CacheUtil.STREAMMAP.get(key).getChannel())
                                && null == CacheUtil.STREAMMAP.get(key).getStartTime()) {// 存在直播流
                            cameraPojo = CacheUtil.STREAMMAP.get(key);
                            sign = 1;
                            break;
                        }
                    }
                    if (sign == 1) {// 存在
                        cameraPojo.setCount(cameraPojo.getCount() + 1);
                        cameraPojo.setOpenTime(openTime);
                        map.put("token", cameraPojo.getToken());
                        map.put("url", cameraPojo.getRtmp());
                    } else {
                        cameraPojo = openStream(pojo.getIp(), pojo.getUsername(), pojo.getPassword(), pojo.getChannel(),
                                pojo.getStream(), pojo.getStartTime(), pojo.getEndTime(), openTime);
                        map.put("token", cameraPojo.getToken());
                        map.put("url", cameraPojo.getRtmp());
                    }

                } else {// 历史流
                    for (String key : keys) {
                        if (pojo.getIp().equals(CacheUtil.STREAMMAP.get(key).getIp())
                                && pojo.getChannel().equals(CacheUtil.STREAMMAP.get(key).getChannel())
                                && null != CacheUtil.STREAMMAP.get(key).getStartTime()) {// 存在历史流
                            cameraPojo = CacheUtil.STREAMMAP.get(key);
                            sign = 1;
                            break;
                        }
                    }
                    if (sign == 1) {
                        cameraPojo.setCount(cameraPojo.getCount() + 1);
                        cameraPojo.setOpenTime(openTime);
                        map.put("message", "当前视频正在使用中...");
                    } else {
                        cameraPojo = openStream(pojo.getIp(), pojo.getUsername(), pojo.getPassword(), pojo.getChannel(),
                                pojo.getStream(), pojo.getStartTime(), pojo.getEndTime(), openTime);
                        map.put("token", cameraPojo.getToken());
                        map.put("url", cameraPojo.getRtmp());
                    }
                }
            }
        }

        return map;
    }

    /**
     * 推流器
     *
     * @param ip        设备IP
     * @param username  设备用户名
     * @param password  设备密码
     * @param channel   通道号
     * @param stream    码流(直播流需要指定码流；历史流不需要指定码流)
     * @param starttime 开始时间(直播流没有开始时间）
     * @param endtime   结束时间(直播流没有结束时间)
     * @param openTime  打开时间
     * @return CameraPojo
     */
    private CameraPojo openStream(String ip, String username, String password, String channel, String stream,
                                  String starttime, String endtime, String openTime) {
        CameraPojo cameraPojo = new CameraPojo();
        // 生成token
        String token = UUID.randomUUID().toString();
        String rtsp = "";
        String rtmp = "";
        String IP = IpUtil.IpConvert(ip);
        // 历史流
        if (null != starttime && "" != starttime) {
            if (null != endtime && "" != endtime) {
                rtsp = "rtsp://" + username + ":" + password + "@" + IP + ":554/Streaming/tracks/" + channel
                        + "01?starttime=" + starttime.substring(0, 8) + "t" + starttime.substring(8) + "z'&'endtime="
                        + endtime.substring(0, 8) + "t" + endtime.substring(8) + "z";
                cameraPojo.setStartTime(starttime);
                cameraPojo.setEndTime(endtime);
            } else {
                try {
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                    String startTime = df.format(df.parse(starttime).getTime() - 60 * 1000);
                    String endTime = df.format(df.parse(starttime).getTime() + 60 * 1000);
                    rtsp = "rtsp://" + username + ":" + password + "@" + IP + ":554/Streaming/tracks/" + channel
                            + "01?starttime=" + startTime.substring(0, 8) + "t" + startTime.substring(8)
                            + "z'&'endtime=" + endTime.substring(0, 8) + "t" + endTime.substring(8) + "z";
                    cameraPojo.setStartTime(startTime);
                    cameraPojo.setEndTime(endTime);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            rtmp = "rtmp://" + IpUtil.IpConvert(config.getPush_ip()) + ":" + config.getPush_port() + "/history/"
                    + token;
        } else {
            // 直播流
            rtsp = "rtsp://" + username + ":" + password + "@" + IP + ":554/h264/ch" + channel + "/" + stream
                    + "/av_stream";
            rtmp = "rtmp://" + IpUtil.IpConvert(config.getPush_ip()) + ":" + config.getPush_port() + "/live/" + token;
        }

        cameraPojo.setUsername(username);
        cameraPojo.setPassword(password);
        cameraPojo.setIp(IP);
        cameraPojo.setChannel(channel);
        cameraPojo.setStream(stream);
        cameraPojo.setRtsp(rtsp);
        cameraPojo.setRtmp(rtmp);
        cameraPojo.setOpenTime(openTime);
        cameraPojo.setCount(1);
        cameraPojo.setToken(token);

        // 执行任务
        CameraThread.MyRunnable job = new CameraThread.MyRunnable(cameraPojo);
        CameraThread.MyRunnable.es.execute(job);
        jobMap.put(token, job);

        return cameraPojo;
    }

    /**
     * 关闭视频流
     *
     * @param tokens 令牌
     */
    @ApiOperation(value = "关闭视频流", notes = "")
    @ApiImplicitParam(name = "tokens", value = "已打开视频流的令牌", dataType = "string", paramType = "query")
    @DeleteMapping(value = "/cameras/{tokens}")
    public void closeCamera(@PathVariable("tokens") String tokens) {
        if (null != tokens && "" != tokens) {
            String[] tokenArr = tokens.split(",");
            for (String token : tokenArr) {
                if (jobMap.containsKey(token) && CacheUtil.STREAMMAP.containsKey(token)) {
                    if (0 < CacheUtil.STREAMMAP.get(token).getCount()) {
                        // 人数-1
                        CacheUtil.STREAMMAP.get(token).setCount(CacheUtil.STREAMMAP.get(token).getCount() - 1);
                    }
                }
            }
        }
    }

    /**
     * 获取视频流
     *
     * @return Map<String, CameraPojo>
     **/
    @ApiOperation(value = "获取视频流", notes = "")
    @GetMapping(value = "/cameras")
    public Map<String, CameraPojo> getCameras() {
        return CacheUtil.STREAMMAP;
    }

    /**
     * 视频流保活
     *
     * @param tokens 令牌
     */
    @ApiOperation(value = "视频流保活", notes = "")
    @ApiImplicitParam(name = "tokens", value = "已打开视频流的令牌", dataType = "string", paramType = "query")
    @PutMapping(value = "/cameras/{tokens}")
    public void keepAlive(@PathVariable("tokens") String tokens) {
        // 校验参数
        if (null != tokens && "" != tokens) {
            String[] tokenArr = tokens.split(",");
            for (String token : tokenArr) {
                CameraPojo cameraPojo = new CameraPojo();
                // 直播流token
                if (null != CacheUtil.STREAMMAP.get(token)) {
                    cameraPojo = CacheUtil.STREAMMAP.get(token);
                    // 更新当前系统时间
                    cameraPojo.setOpenTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date().getTime()));
                }
            }
        }
    }

    /**
     * 获取服务信息
     *
     * @return Map<String, Object>
     **/
    @ApiOperation(value = "获取服务信息", notes = "")
    @GetMapping(value = "/status")
    public Map<String, Object> getConfig() {
        // 获取当前时间
        long nowTime = new Date().getTime();
        String upTime = (nowTime - CacheUtil.STARTTIME) / (1000 * 60 * 60) + "h"
                + (nowTime - CacheUtil.STARTTIME) % (1000 * 60 * 60) / (1000 * 60) + "m"
                + (nowTime - CacheUtil.STARTTIME) % (1000 * 60 * 60) / (1000) + "s";
        Map<String, Object> status = new HashMap<String, Object>();
        status.put("config", config);
        status.put("uptime", upTime);
        return status;
    }

}
