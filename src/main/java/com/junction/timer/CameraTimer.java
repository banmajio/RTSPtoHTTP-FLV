package com.junction.timer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class CameraTimer implements CommandLineRunner {

	private final static Logger logger = LoggerFactory.getLogger(CameraTimer.class);

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
				logger.info("定时任务  当前有" + CameraController.JOBMAP.size() + "个推流任务正在进行推流");
				// 管理缓存
				if (null != CacheUtil.STREATMAP && 0 != CacheUtil.STREATMAP.size()) {
					Set<String> keys = CacheUtil.STREATMAP.keySet();
					for (String key : keys) {
						try {
							// 最后打开时间
							long openTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
									.parse(CacheUtil.STREATMAP.get(key).getOpentime()).getTime();
							// 当前系统时间
							long newTime = new Date().getTime();

							// 如果通道使用人数为0，则关闭推流
							if (CacheUtil.STREATMAP.get(key).getCount() == 0) {
								// 结束线程
								CameraController.JOBMAP.get(key).setInterrupted(key);
								logger.info("定时任务 当前设备使用人数为0结束推流 设备信息：[ip：" + CacheUtil.STREATMAP.get(key).getIp()
										+ " channel:" + CacheUtil.STREATMAP.get(key).getChannel() + " stream:"
										+ CacheUtil.STREATMAP.get(key).getStream() + " starttime:"
										+ CacheUtil.STREATMAP.get(key).getStarttime() + " endtime:"
										+ CacheUtil.STREATMAP.get(key).getEndtime() + " rtsp:"
										+ CacheUtil.STREATMAP.get(key).getRtsp() + " url:"
										+ CacheUtil.STREATMAP.get(key).getUrl() + "]");
							} else if (null == CacheUtil.STREATMAP.get(key).getStarttime()
									&& (newTime - openTime) / 1000 / 60 >= Integer.valueOf(config.getKeepalive())) {
								CameraController.JOBMAP.get(key).setInterrupted(key);
								logger.info("定时任务 当前设备使用时间超时结束推流 设备信息：[ip:" + CacheUtil.STREATMAP.get(key).getIp()
										+ " channel:" + CacheUtil.STREATMAP.get(key).getChannel() + " stream:"
										+ CacheUtil.STREATMAP.get(key).getStream() + " starttime:"
										+ CacheUtil.STREATMAP.get(key).getStarttime() + " endtime:"
										+ CacheUtil.STREATMAP.get(key).getEndtime() + " rtsp:"
										+ CacheUtil.STREATMAP.get(key).getRtsp() + " url:"
										+ CacheUtil.STREATMAP.get(key).getUrl() + "]");
							}
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}, 1, 1000 * 60);
	}
}
