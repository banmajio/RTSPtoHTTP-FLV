package com.junction.controller;

import com.alibaba.fastjson.JSONObject;
import com.junction.cache.CacheUtil;
import com.junction.pojo.CameraPojo;
import com.junction.pojo.Config;
import com.junction.thread.CameraThread;
import com.junction.util.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @description: CameraController
 * @author: banmajio
 * @date: 2023/8/23 14:33
 */
@RestController
public class CameraController {

    private final String ERRORCODE = "errorCode";

    private final String LOCALHOST = "127.0.0.1";


    private final static Logger logger = LoggerFactory.getLogger(CameraController.class);

    @Resource
    public Config config;

    /**
     * @description: 存放任务 线程
     * @author: banmajio
     * @date: 2023/9/4 10:52
     */
    public static Map<String, CameraThread.MyRunnable> JOBMAP = new HashMap<>();

    /**
     * @description: 开启视频流
     * @author: banmajio
     * @date: 2023/8/23 14:34
     * @param: [pojo]
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     */
    @RequestMapping(value = "/cameras", method = RequestMethod.POST)
    public Map<String, Object> openCamera(@RequestBody CameraPojo pojo) {
        // 返回结果
        Map<String, Object> map = new LinkedHashMap<>();
        // openStream返回结果
        Map<String, Object> openMap;
        JSONObject cameraJson = JSONObject.parseObject(JSONObject.toJSON(pojo).toString());
        // 需要校验非空的参数
        String[] isNullArr = {"ip", "username", "password", "channel", "stream"};
        // 空值校验
        if (!Utils.isNullParameters(cameraJson, isNullArr)) {
            map.put("msg", "输入参数不完整");
            map.put("code", 1);
            return map;
        }
        // ip格式校验
        if (!Utils.isTrueIp(pojo.getIp())) {
            map.put("msg", "ip格式输入错误");
            map.put("code", 2);
            return map;
        }
        if (null != pojo.getStarttime()) {
            // 开始时间校验
            if (Utils.isTrueTime(pojo.getStarttime())) {
                map.put("msg", "startTime格式输入错误");
                map.put("code", 3);
                return map;
            }
            if (null != pojo.getEndTime()) {
                if (Utils.isTrueTime(pojo.getEndTime())) {
                    map.put("msg", "endTime格式输入错误");
                    map.put("code", 4);
                    return map;
                }
                // 结束时间要大于开始时间
                try {
                    long startTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").parse(pojo.getStarttime()).getTime();
                    long endTime = new SimpleDateFormat("yyyy-MM-dd HH:ss:mm").parse(pojo.getEndTime()).getTime();
                    if (endTime - startTime < 0) {
                        map.put("msg", "endTime需要大于startTime");
                        map.put("code", 5);
                        return map;
                    }
                } catch (ParseException e) {
                    logger.error(e.getMessage());
                }
            }
        }

        CameraPojo cameraPojo = new CameraPojo();
        // 获取当前时间
        String openTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis());
        Set<String> keys = CacheUtil.STREATMAP.keySet();
        // 缓存是否为空
        if (keys.isEmpty()) {
            // 开始推流
            openMap = openStream(pojo.getIp(), pojo.getUsername(), pojo.getPassword(), pojo.getChannel(),
                    pojo.getStream(), pojo.getStarttime(), pojo.getEndTime(), openTime);
            if (Integer.parseInt(openMap.get(ERRORCODE).toString()) == 0) {
                map.put("url", ((CameraPojo) openMap.get("pojo")).getUrl());
                map.put("token", ((CameraPojo) openMap.get("pojo")).getToken());
                map.put("msg", "打开视频流成功");
                map.put("code", 0);
            } else {
                map.put("msg", openMap.get("message"));
                map.put("code", openMap.get("errorCode"));
            }
        } else {
            // 是否存在的标志；false：不存在；true：存在
            boolean sign = false;
            if (null == pojo.getStarttime()) {
                // 直播流
                for (String key : keys) {
                    if (pojo.getIp().equals(CacheUtil.STREATMAP.get(key).getIp())
                            && pojo.getChannel().equals(CacheUtil.STREATMAP.get(key).getChannel())
                            && null == CacheUtil.STREATMAP.get(key).getStarttime()) {
                        // 存在直播流
                        cameraPojo = CacheUtil.STREATMAP.get(key);
                        sign = true;
                        break;
                    }
                }
                if (sign) {
                    // 存在
                    cameraPojo.setCount(cameraPojo.getCount() + 1);
                    cameraPojo.setOpentime(openTime);
                    map.put("url", cameraPojo.getUrl());
                    map.put("token", cameraPojo.getToken());
                    map.put("msg", "打开视频流成功");
                    map.put("code", 0);
                } else {
                    openMap = openStream(pojo.getIp(), pojo.getUsername(), pojo.getPassword(), pojo.getChannel(),
                            pojo.getStream(), pojo.getStarttime(), pojo.getEndTime(), openTime);
                    if (Integer.parseInt(openMap.get(ERRORCODE).toString()) == 0) {
                        map.put("url", ((CameraPojo) openMap.get("pojo")).getUrl());
                        map.put("token", ((CameraPojo) openMap.get("pojo")).getToken());
                        map.put("msg", "打开视频流成功");
                        map.put("code", 0);
                    } else {
                        map.put("msg", openMap.get("message"));
                        map.put("code", openMap.get(ERRORCODE));
                    }
                }

            } else {
                // 历史流
                for (String key : keys) {
                    if (pojo.getIp().equals(CacheUtil.STREATMAP.get(key).getIp())
                            && CacheUtil.STREATMAP.get(key).getStarttime() != null) {
                        // 存在历史流
                        sign = true;
                        cameraPojo = CacheUtil.STREATMAP.get(key);
                        break;
                    }
                }
                if (sign && cameraPojo.getCount() == 0) {
                    map.put("msg", "设备正在结束回放,请稍后再试");
                    map.put("code", 9);
                } else if (sign) {
                    map.put("msg", "设备正在进行回放,请稍后再试");
                    map.put("code", 8);
                } else {
                    openMap = openStream(pojo.getIp(), pojo.getUsername(), pojo.getPassword(), pojo.getChannel(),
                            pojo.getStream(), pojo.getStarttime(), pojo.getEndTime(), openTime);
                    if (Integer.parseInt(openMap.get(ERRORCODE).toString()) == 0) {
                        map.put("url", ((CameraPojo) openMap.get("pojo")).getUrl());
                        map.put("token", ((CameraPojo) openMap.get("pojo")).getToken());
                        map.put("msg", "打开视频流成功");
                        map.put("code", 0);
                    } else {
                        map.put("msg", openMap.get("message"));
                        map.put("code", openMap.get(ERRORCODE));
                    }
                }
            }
        }
        return map;
    }

    /**
     * @description: 推流器
     * @author: banmajio
     * @date: 2023/8/23 14:37
     * @param: [ip, username, password, channel, stream, starttime, endtime, opentime]
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     */
    private Map<String, Object> openStream(String ip, String username, String password, String channel, String stream,
                                           String starttime, String endtime, String opentime) {
        Map<String, Object> map = new HashMap<>();
        CameraPojo cameraPojo = new CameraPojo();
        // 生成token
        String token = UUID.randomUUID().toString();
        String rtsp;
        String rtmp;
        String IP = Utils.ipConvert(ip);
        String url;
        // 该nvr是否再回放，true：在回放；false： 没在回放
        // 历史流
        if (null != starttime && !starttime.isEmpty()) {
            if (null != endtime && !endtime.isEmpty()) {
                rtsp = "rtsp://" + username + ":" + password + "@" + IP + ":554/Streaming/tracks/"
                        + (Integer.parseInt(channel) - 32) + "01?starttime=" + Utils.getTime(starttime).substring(0, 8)
                        + "t" + Utils.getTime(starttime).substring(8) + "z'&'endtime="
                        + Utils.getTime(endtime).substring(0, 8) + "t" + Utils.getTime(endtime).substring(8) + "z";
                cameraPojo.setStarttime(Utils.getTime(starttime));
                cameraPojo.setEndTime(Utils.getTime(endtime));
            } else {
                String startTime = Utils.getStartTime(starttime);
                String endTime = Utils.getEndtime(starttime);
                rtsp = "rtsp://" + username + ":" + password + "@" + IP + ":554/Streaming/tracks/"
                        + (Integer.parseInt(channel) - 32) + "01?starttime=" + startTime.substring(0, 8) + "t"
                        + startTime.substring(8) + "z'&'endtime=" + endTime.substring(0, 8) + "t" + endTime.substring(8)
                        + "z";
                cameraPojo.setStarttime(Utils.getStartTime(starttime));
                cameraPojo.setEndTime(Utils.getEndtime(starttime));
            }
//			rtmp = "rtmp://" + Utils.ipConvert(config.getPush_host()) + ":" + config.getPush_port() + "/history/"
//					+ token;
            rtmp = "rtmp://" + Utils.ipConvert(config.getPushHost()) + ":" + config.getPushPort() + "/history/test";
            if (LOCALHOST.equals(config.getHostExtra())) {
                url = rtmp;
            } else {
                url = "rtmp://" + Utils.ipConvert(config.getHostExtra()) + ":" + config.getPushPort() + "/history/"
                        + token;
            }
        } else {
            // 直播流
            rtsp = "rtsp://" + username + ":" + password + "@" + IP + ":554/h264/ch" + channel + "/" + stream
                    + "/av_stream";
            rtmp = "rtmp://" + Utils.ipConvert(config.getPushHost()) + ":" + config.getPushPort() + "/live/" + token;
            if (config.getHostExtra().equals(LOCALHOST)) {
                url = rtmp;
            } else {
                url = "rtmp://" + Utils.ipConvert(config.getHostExtra()) + ":" + config.getPushPort() + "/live/"
                        + token;
            }
        }

        cameraPojo.setUsername(username);
        cameraPojo.setPassword(password);
        cameraPojo.setIp(IP);
        cameraPojo.setChannel(channel);
        cameraPojo.setStream(stream);
        cameraPojo.setRtsp(rtsp);
        cameraPojo.setRtmp(rtmp);
        cameraPojo.setUrl(url);
        cameraPojo.setOpentime(opentime);
        cameraPojo.setCount(1);
        cameraPojo.setToken(token);

        // 解决ip输入错误时，grabber.start();出现阻塞无法释放grabber而导致后续推流无法进行；
        Socket rtspSocket = new Socket();
        Socket rtmpSocket = new Socket();

        // 建立TCP Scoket连接，超时时间1s，如果成功继续执行，否则return
        try {
            rtspSocket.connect(new InetSocketAddress(cameraPojo.getIp(), 554), 1000);
        } catch (IOException e) {
            logger.error("与拉流IP：   " + cameraPojo.getIp() + "   端口：   554    建立TCP连接失败！");
            map.put("pojo", cameraPojo);
            map.put(ERRORCODE, 6);
            map.put("message", "与拉流IP：   " + cameraPojo.getIp() + "   端口：   554    建立TCP连接失败！");
            return map;
        }
        try {
            rtmpSocket.connect(new InetSocketAddress(Utils.ipConvert(config.getPushHost()),
                    Integer.parseInt(config.getPushPort())), 1000);
        } catch (IOException e) {
            logger.error("与推流IP：   " + config.getPushHost() + "   端口：   " + config.getPushPort() + " 建立TCP连接失败！");
            map.put("pojo", cameraPojo);
            map.put(ERRORCODE, 7);
            map.put("message",
                    "与推流IP:" + config.getPushHost() + " 端口: " + config.getPushPort() + " 建立连接失败,请检查nginx服务");
            return map;
        }
        // 执行任务
        CameraThread.MyRunnable job = new CameraThread.MyRunnable(cameraPojo);
        CameraThread.MyRunnable.es.execute(job);
        JOBMAP.put(token, job);
        map.put("pojo", cameraPojo);
        map.put(ERRORCODE, 0);
        map.put("message", "打开视频流成功");
        return map;
    }

    /**
     * @description: 关闭视频流
     * @author: banmajio
     * @date: 2023/8/23 14:39
     * @param: [tokens]
     * @return: void
     */
    @RequestMapping(value = "/cameras/{tokens}", method = RequestMethod.DELETE)
    public void closeCamera(@PathVariable("tokens") String tokens) {
        if (null != tokens && !tokens.isEmpty()) {
            String[] tokenArr = tokens.split(",");
            for (String token : tokenArr) {
                if (JOBMAP.containsKey(token) && CacheUtil.STREATMAP.containsKey(token)) {
                    // 回放手动关闭
                    if (null != CacheUtil.STREATMAP.get(token).getStarttime()) {
                        if (0 == CacheUtil.STREATMAP.get(token).getCount() - 1) {
                            CacheUtil.PUSHMAP.get(token).setExitCode(1);
                            CacheUtil.STREATMAP.get(token).setCount(CacheUtil.STREATMAP.get(token).getCount() - 1);
                        } else {
                            CacheUtil.STREATMAP.get(token).setCount(CacheUtil.STREATMAP.get(token).getCount() - 1);
                            logger.info("当前设备正在进行回放，使用人数为" + CacheUtil.STREATMAP.get(token).getCount() + " 设备信息：[ip："
                                    + CacheUtil.STREATMAP.get(token).getIp() + " channel:"
                                    + CacheUtil.STREATMAP.get(token).getChannel() + " stream:"
                                    + CacheUtil.STREATMAP.get(token).getStream() + " startTime:"
                                    + CacheUtil.STREATMAP.get(token).getStarttime() + " endTime:"
                                    + CacheUtil.STREATMAP.get(token).getEndTime() + " url:"
                                    + CacheUtil.STREATMAP.get(token).getUrl() + "]");
                        }
                    } else {
                        if (0 < CacheUtil.STREATMAP.get(token).getCount()) {
                            // 人数-1
                            CacheUtil.STREATMAP.get(token).setCount(CacheUtil.STREATMAP.get(token).getCount() - 1);
                            logger.info("关闭成功 当前设备使用人数为" + CacheUtil.STREATMAP.get(token).getCount() + " 设备信息：[ip："
                                    + CacheUtil.STREATMAP.get(token).getIp() + " channel:"
                                    + CacheUtil.STREATMAP.get(token).getChannel() + " stream:"
                                    + CacheUtil.STREATMAP.get(token).getStream() + " startTime:"
                                    + CacheUtil.STREATMAP.get(token).getStarttime() + " endTime:"
                                    + CacheUtil.STREATMAP.get(token).getEndTime() + " url:"
                                    + CacheUtil.STREATMAP.get(token).getUrl() + "]");
                        }
                    }

                }
            }
        }
    }

    /**
     * @description: 获取视频流
     * @author: banmajio
     * @date: 2023/8/23 14:39
     * @param: []
     * @return: java.util.Map<java.lang.String, com.junction.pojo.CameraPojo>
     */
    @RequestMapping(value = "/cameras", method = RequestMethod.GET)
    public Map<String, CameraPojo> getCameras() {
        logger.info("获取视频流信息：" + CacheUtil.STREATMAP.toString());
        return CacheUtil.STREATMAP;
    }

    /**
     * @description: 视频流保活
     * @author: banmajio
     * @date: 2023/8/23 14:39
     * @param: [tokens]
     * @return: void
     */
    @RequestMapping(value = "/cameras/{tokens}", method = RequestMethod.PUT)
    public void keepAlive(@PathVariable("tokens") String tokens) {
        // 校验参数
        if (null != tokens && !tokens.isEmpty()) {
            String[] tokenArr = tokens.split(",");
            for (String token : tokenArr) {
                CameraPojo cameraPojo;
                // 直播流token
                if (null != CacheUtil.STREATMAP.get(token)) {
                    cameraPojo = CacheUtil.STREATMAP.get(token);
                    // 更新当前系统时间
                    cameraPojo.setOpentime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(System.currentTimeMillis()));
                    logger.info("保活成功 设备信息：[ip：" + cameraPojo.getIp() + " channel:" + cameraPojo.getChannel()
                            + " stream:" + cameraPojo.getStream() + " startTime:" + cameraPojo.getStarttime()
                            + " endTime:" + cameraPojo.getEndTime() + " url:" + cameraPojo.getUrl() + "]");
                }
            }
        }
    }

    /**
     * @description: 获取服务信息
     * @author: banmajio
     * @date: 2023/8/23 14:40
     * @param: []
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     */
    @RequestMapping(value = "/status", method = RequestMethod.GET)
    public Map<String, Object> getConfig() {
        // 获取当前时间
        long nowTime = System.currentTimeMillis();
        String upTime = (nowTime - CacheUtil.STARTTIME) / (1000 * 60 * 60) + "h"
                + (nowTime - CacheUtil.STARTTIME) % (1000 * 60 * 60) / (1000 * 60) + "m"
                + (nowTime - CacheUtil.STARTTIME) % (1000 * 60 * 60) / (1000) + "s";
        logger.info("获取服务信息：" + config.toString() + ";服务运行时间：" + upTime);
        Map<String, Object> status = new HashMap<>();
        status.put("config", config);
        status.put("uptime", upTime);
        return status;
    }

}
