package com.junction.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.junction.cache.CacheUtil;
import com.junction.controller.CameraController;
import com.junction.pojo.Config;

/**
 * @Title TimerUtil.java
 * @description 定时任务
 * @time 2019年12月16日 下午3:10:08
 * @author wuguodong
 **/
@Component
public class TimerUtil implements CommandLineRunner {

	@Autowired
	private Config config;// 配置文件bean

	public static Timer timer;

	@Override
	public void run(String... args) throws Exception {
		// 超过5分钟，结束推流
		timer = new Timer("timeTimer");
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				System.err.println("开始执行定时任务...");
				// 管理缓存
				if (null != CacheUtil.STREAMMAP && 0 != CacheUtil.STREAMMAP.size()) {
					Set<String> keys = CacheUtil.STREAMMAP.keySet();
					for (String key : keys) {
						try {
							// 最后打开时间
							long openTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.parse(CacheUtil.STREAMMAP.get(key).getOpenTime()).getTime();
							// 当前系统时间
							long newTime = new Date().getTime();
							// 如果通道使用人数为0，则关闭推流
							if (CacheUtil.STREAMMAP.get(key).getCount() == 0) {
								// 结束线程
								CameraController.jobMap.get(key).setInterrupted();
								// 清除缓存
								CacheUtil.STREAMMAP.remove(key);
								CameraController.jobMap.remove(key);
							} else if ((newTime - openTime) / 1000 / 60 > Integer.valueOf(config.getKeepalive())) {
								CameraController.jobMap.get(key).setInterrupted();
								CameraController.jobMap.remove(key);
								CacheUtil.STREAMMAP.remove(key);
								System.err.println("[定时任务]  关闭" + key + "摄像头...");
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
				System.err.println("定时任务执行完毕...");
			}
		}, 1, 1000 * 60);
	}
}
