package com.junction.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.junction.cache.CacheUtil;
import com.junction.controller.CameraController;
import com.junction.pojo.Config;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

/**
 * @description: 定时任务
 * @author: banmajio
 * @date: 2023/9/8 11:07
 */
@Component
public class CameraTimer implements CommandLineRunner {

    private final static Logger logger = LoggerFactory.getLogger(CameraTimer.class);

    @Resource
    private Config config;

    private ScheduledExecutorService executorService;

    @Override
    public void run(String... args) {
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() -> {
            logger.info("定时任务  当前有" + CameraController.JOBMAP.size() + "个推流任务正在进行推流");
            if (null != CacheUtil.STREATMAP && !CacheUtil.STREATMAP.isEmpty()) {
                Set<String> keys = CacheUtil.STREATMAP.keySet();
                for (String key : keys) {
                    try {
                        long openTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                                .parse(CacheUtil.STREATMAP.get(key).getOpentime()).getTime();
                        long newTime = System.currentTimeMillis();

                        if (CacheUtil.STREATMAP.get(key).getCount() == 0) {
                            CameraController.JOBMAP.get(key).setInterrupted(key);
                            logger.info("定时任务 当前设备使用人数为0结束推流 设备信息：[ip：" + CacheUtil.STREATMAP.get(key).getIp()
                                    + " channel:" + CacheUtil.STREATMAP.get(key).getChannel() + " stream:"
                                    + CacheUtil.STREATMAP.get(key).getStream() + " starttime:"
                                    + CacheUtil.STREATMAP.get(key).getStarttime() + " endtime:"
                                    + CacheUtil.STREATMAP.get(key).getEndTime() + " rtsp:"
                                    + CacheUtil.STREATMAP.get(key).getRtsp() + " url:"
                                    + CacheUtil.STREATMAP.get(key).getUrl() + "]");
                        } else if (null == CacheUtil.STREATMAP.get(key).getStarttime()
                                && (newTime - openTime) / 1000 / 60 >= Integer.parseInt(config.getKeepalive())) {
                            CameraController.JOBMAP.get(key).setInterrupted(key);
                            logger.info("定时任务 当前设备使用时间超时结束推流 设备信息：[ip:" + CacheUtil.STREATMAP.get(key).getIp()
                                    + " channel:" + CacheUtil.STREATMAP.get(key).getChannel() + " stream:"
                                    + CacheUtil.STREATMAP.get(key).getStream() + " starttime:"
                                    + CacheUtil.STREATMAP.get(key).getStarttime() + " endtime:"
                                    + CacheUtil.STREATMAP.get(key).getEndTime() + " rtsp:"
                                    + CacheUtil.STREATMAP.get(key).getRtsp() + " url:"
                                    + CacheUtil.STREATMAP.get(key).getUrl() + "]");
                        }
                    } catch (ParseException e) {
                        logger.error(e.getMessage());
                    }
                }
            }
        }, 0, 60, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void cleanup() {
        executorService.shutdown();
    }
}

