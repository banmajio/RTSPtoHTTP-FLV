package com.junction;

import cn.hutool.core.date.DateUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.junction.cache.CacheUtil;
import com.junction.thread.CameraThread;
import com.junction.util.CameraPush;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PreDestroy;


/**
 * 摄像机服务器应用程序
 */
@EnableScheduling
@SpringBootApplication
public class CameraServerApplication {

    private static final Log logger = LogFactory.get();

    public static void main(String[] args) {
        // 将服务启动时间存入缓存
        CacheUtil.STARTTIME = DateUtil.current(false);
        final ApplicationContext applicationContext = SpringApplication.run(CameraServerApplication.class, args);
        // 将上下文传入CameraPush类中，用于检测tcp连接是否正常
        CameraPush.setApplicationContext(applicationContext);
    }

    @PreDestroy
    public void destory() {
        // 关闭线程池
        CameraThread.MyRunnable.es.shutdownNow();

        logger.info("======> 释放空间...");
    }
}
