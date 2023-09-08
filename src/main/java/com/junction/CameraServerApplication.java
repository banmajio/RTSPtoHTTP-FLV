package com.junction;

import com.junction.cache.CacheUtil;
import com.junction.controller.CameraController;
import com.junction.push.CameraPush;
import com.junction.thread.CameraThread;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import javax.annotation.PreDestroy;
import java.util.Set;

@SpringBootApplication
public class CameraServerApplication {

	private final static Logger logger = LoggerFactory.getLogger(CameraServerApplication.class);

	public static void main(String[] args) {
		// 服务启动执行FFmpegFrameGrabber和FFmpegFrameRecorder的tryLoad()，以免导致第一次推流时耗时。
		try {
			FFmpegFrameGrabber.tryLoad();
			FFmpegFrameRecorder.tryLoad();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		// 将服务启动时间存入缓存
		CacheUtil.STARTTIME = System.currentTimeMillis();
		final ApplicationContext applicationContext = SpringApplication.run(CameraServerApplication.class, args);
		// 将上下文传入RealPlay类中,以使其使用config中的变量
		CameraPush.setApplicationContext(applicationContext);
	}

	@PreDestroy
	public void destory() {
		logger.info("服务结束，开始释放空间...");
		// 结束正在进行的任务
		Set<String> keys = CameraController.JOBMAP.keySet();
		for (String key : keys) {
			CameraController.JOBMAP.get(key).setInterrupted(key);
		}
		// 关闭线程池
		CameraThread.MyRunnable.es.shutdown();
	}
}
