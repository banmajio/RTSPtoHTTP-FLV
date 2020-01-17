package com.junction;

import java.util.Date;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import com.junction.cache.CacheUtil;
import com.junction.thread.CameraThread;
import com.junction.util.CameraPush;
import com.junction.util.TimerUtil;

@SpringBootApplication
public class CameraServerApplication {

	private final static Logger logger = LoggerFactory.getLogger(CameraServerApplication.class);

	public static void main(String[] args) {
		// 将服务启动时间存入缓存
		CacheUtil.STARTTIME = new Date().getTime();
		final ApplicationContext applicationContext = SpringApplication.run(CameraServerApplication.class, args);
		// 将上下文传入CameraPush类中，用于检测tcp连接是否正常
		CameraPush.setApplicationContext(applicationContext);
	}

	@PreDestroy
	public void destory() {
		logger.info("服务结束，开始释放空间...");
		// 关闭线程池
		CameraThread.MyRunnable.es.shutdownNow();
		// 销毁定时器
		TimerUtil.timer.cancel();
	}
}
