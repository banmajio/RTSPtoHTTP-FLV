package com.junction.task;

import cn.hutool.core.date.DateUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.junction.cache.CacheUtil;
import com.junction.controller.CameraController;
import com.junction.pojo.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * 定时任务
 *
 * @author 猎隼丶止戈
 * @date
 */
@Component
public class TimedTask {

    private static final Log logger = LogFactory.get();

    /**
     * 配置文件bean
     */
    @Autowired
    private Config config;

    /**
     * 结束推流
     * <pre>
     * 每分钟执行一次
     * </pre>
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void finishPushStream() {
        // 超过配置时间则结束推流
        // 管理缓存
        if (null != CacheUtil.STREAMMAP && !CacheUtil.STREAMMAP.isEmpty()) {
            Set<String> keys = CacheUtil.STREAMMAP.keySet();
            for (String key : keys) {
                // 最后打开时间
                long openTime = DateUtil.parse(CacheUtil.STREAMMAP.get(key).getOpenTime()).getTime();
                // 当前系统时间
                long newTime = DateUtil.current(false);
                // 如果通道使用人数为0，则关闭推流
                if (CacheUtil.STREAMMAP.get(key).getCount() == 0) {
                    // 结束线程
                    CameraController.jobMap.get(key).setInterrupted();
                    // 清除缓存
                    CacheUtil.STREAMMAP.remove(key);
                    CameraController.jobMap.remove(key);
                } else if (((newTime - openTime) / 1000 / 60) > config.getKeepalive()) {
                    CameraController.jobMap.get(key).setInterrupted();
                    CameraController.jobMap.remove(key);
                    CacheUtil.STREAMMAP.remove(key);
                    logger.info("======> 【定时任务】  关闭 {} 摄像头...", key);
                }
            }
        }
    }
}
