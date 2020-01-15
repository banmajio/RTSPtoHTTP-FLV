package com.junction.thread;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import com.junction.cache.CacheUtil;
import com.junction.controller.CameraController;
import com.junction.pojo.CameraPojo;
import com.junction.util.CameraPush;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author wuguodong
 * @Title CameraThread.java
 * @description TODO
 * @time 2019年12月16日 上午9:32:43
 **/
public class CameraThread {

    private static final Log logger = LogFactory.get();

    public static class MyRunnable implements Runnable {

        /**
         * 创建线程池
         */
        public static ExecutorService es = Executors.newCachedThreadPool();

        private CameraPojo cameraPojo;
        private Thread nowThread;

        public MyRunnable(CameraPojo cameraPojo) {
            this.cameraPojo = cameraPojo;
        }

        /**
         * 中断线程
         */
        public void setInterrupted() {
            nowThread.interrupt();
        }

        @Override
        public void run() {
            // 直播流
            try {
                // 获取当前线程存入缓存
                nowThread = Thread.currentThread();
                CacheUtil.STREAMMAP.put(cameraPojo.getToken(), cameraPojo);
                // 执行转流推流任务
                CameraPush push = new CameraPush(cameraPojo).from();
                if (push != null) {
                    push.to().go(nowThread);
                }
                // 清除缓存
                CacheUtil.STREAMMAP.remove(cameraPojo.getToken());
                CameraController.jobMap.remove(cameraPojo.getToken());
            } catch (Exception e) {
                logger.info("当前线程： {} 当前任务： {} 停止...", Thread.currentThread().getName(),
                        cameraPojo.getRtsp());
                CacheUtil.STREAMMAP.remove(cameraPojo.getToken());
                CameraController.jobMap.remove(cameraPojo.getToken());
                e.printStackTrace();
            }
        }
    }
}
