package com.junction.push;

import static org.bytedeco.ffmpeg.global.avcodec.av_packet_unref;

import java.util.HashMap;
import java.util.Map;

import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.FFmpegLogCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import com.junction.pojo.CameraPojo;
import com.junction.pojo.Config;

/**
 * @Title RtmpPush.java
 * @description javacv推数据帧
 * @time 2020年3月17日 下午2:32:42
 * @author wuguodong
 **/
public class CameraPush {
	private final static Logger logger = LoggerFactory.getLogger(CameraPush.class);
	// 配置类
	private static Config config;

	// 通过applicationContext上下文获取Config类
	public static void setApplicationContext(ApplicationContext applicationContext) {
		config = applicationContext.getBean(Config.class);
	}

	private CameraPojo pojo;// 设备信息
	private FFmpegFrameRecorder recorder;// 解码器
	private FFmpegFrameGrabber grabber;// 采集器
	private int err_index = 0;// 推流过程中出现错误的次数
	private int exitcode = 0;// 退出状态码：0-正常退出;1-手动中断;
	private double framerate = 0;// 帧率

	public void setExitcode(int exitcode) {
		this.exitcode = exitcode;
	}

	public int getExitcode() {
		return exitcode;
	}

	public CameraPush(CameraPojo cameraPojo) {
		this.pojo = cameraPojo;
	}

	/**
	 * @Title: release
	 * @Description:资源释放
	 * @return void
	 **/
	public void release() {
		try {
			grabber.stop();
			grabber.close();
			if (recorder != null) {
				recorder.stop();
				recorder.release();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @Title: push
	 * @Description:推送视频流数据包
	 * @return void
	 **/
	public void push() {
		try {
			avutil.av_log_set_level(avutil.AV_LOG_INFO);
			FFmpegLogCallback.set();
			grabber = new FFmpegFrameGrabber(pojo.getRtsp());
			grabber.setOption("rtsp_transport", "tcp");
			// 设置采集器构造超时时间
			grabber.setOption("stimeout", "2000000");
			if ("sub".equals(pojo.getStream())) {
				grabber.start(config.getSub_code());
			} else if ("main".equals(pojo.getStream())) {
				grabber.start(config.getMain_code());
			} else {
				grabber.start(config.getMain_code());
			}

			// 部分监控设备流信息里携带的帧率为9000，如出现此问题，会导致dts、pts时间戳计算失败，播放器无法播放，故出现错误的帧率时，默认为25帧
			if (grabber.getFrameRate() > 0 && grabber.getFrameRate() < 100) {
				framerate = grabber.getFrameRate();
			} else {
				framerate = 25.0;
			}
			int width = grabber.getImageWidth();
			int height = grabber.getImageHeight();
			// 若视频像素值为0，说明拉流异常，程序结束
			if (width == 0 && height == 0) {
				logger.error(pojo.getRtsp() + "  拉流异常！");
				grabber.stop();
				grabber.close();
				release();
				return;
			}
			recorder = new FFmpegFrameRecorder(pojo.getRtmp(), grabber.getImageWidth(), grabber.getImageHeight());
			recorder.setInterleaved(true);
			// 关键帧间隔，一般与帧率相同或者是视频帧率的两倍
			recorder.setGopSize((int) framerate * 2);
			// 视频帧率(保证视频质量的情况下最低25，低于25会出现闪屏)
			recorder.setFrameRate(framerate);
			// 设置比特率
			recorder.setVideoBitrate(grabber.getVideoBitrate());
			// 封装flv格式
			recorder.setFormat("flv");
			// h264编/解码器
			recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
			recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
			Map<String, String> videoOption = new HashMap<>();

			// 该参数用于降低延迟
			videoOption.put("tune", "zerolatency");
			/**
			 ** 权衡quality(视频质量)和encode speed(编码速度) values(值)： *
			 * ultrafast(终极快),superfast(超级快), veryfast(非常快), faster(很快), fast(快), *
			 * medium(中等), slow(慢), slower(很慢), veryslow(非常慢) *
			 * ultrafast(终极快)提供最少的压缩（低编码器CPU）和最大的视频流大小；而veryslow(非常慢)提供最佳的压缩（高编码器CPU）的同时降低视频流的大小
			 */
			videoOption.put("preset", "ultrafast");
			// 画面质量参数，0~51；18~28是一个合理范围
			videoOption.put("crf", "28");
			recorder.setOptions(videoOption);
			AVFormatContext fc = grabber.getFormatContext();
			recorder.start(fc);
			logger.debug("开始推流 设备信息：[ip:" + pojo.getIp() + " channel:" + pojo.getChannel() + " stream:"
					+ pojo.getStream() + " starttime:" + pojo.getStarttime() + " endtime:" + pojo.getEndtime()
					+ " rtsp:" + pojo.getRtsp() + " url:" + pojo.getUrl() + "]");
			// 清空探测时留下的缓存
			grabber.flush();

			AVPacket pkt = null;
			long dts = 0;
			long pts = 0;
			int timebase = 0;
			for (int no_frame_index = 0; no_frame_index < 5 && err_index < 5;) {
				long time1 = System.currentTimeMillis();
				if (exitcode == 1) {
					break;
				}
				pkt = grabber.grabPacket();
				if (pkt == null || pkt.size() == 0 || pkt.data() == null) {
					// 空包记录次数跳过
					logger.warn("JavaCV 出现空包 设备信息：[ip:" + pojo.getIp() + " channel:" + pojo.getChannel() + " stream:"
							+ pojo.getStream() + " starttime:" + pojo.getStarttime() + " endtime:" + " rtsp:"
							+ pojo.getRtsp() + pojo.getEndtime() + " url:" + pojo.getUrl() + "]");
					no_frame_index++;
					continue;
				}
				// 过滤音频
				if (pkt.stream_index() == 1) {
					av_packet_unref(pkt);
					continue;
				}

				// 矫正sdk回调数据的dts，pts每次不从0开始累加所导致的播放器无法续播问题
				pkt.pts(pts);
				pkt.dts(dts);
				err_index += (recorder.recordPacket(pkt) ? 0 : 1);
				// pts,dts累加
				timebase = grabber.getFormatContext().streams(pkt.stream_index()).time_base().den();
				pts += timebase / (int) framerate;
				dts += timebase / (int) framerate;
				// 将缓存空间的引用计数-1，并将Packet中的其他字段设为初始值。如果引用计数为0，自动的释放缓存空间。
				av_packet_unref(pkt);

				long endtime = System.currentTimeMillis();
				if ((long) (1000 /framerate) - (endtime - time1) > 0) {
					Thread.sleep((long) (1000 / framerate) - (endtime - time1));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			release();
			logger.info("推流结束 设备信息：[ip:" + pojo.getIp() + " channel:" + pojo.getChannel() + " stream:"
					+ pojo.getStream() + " starttime:" + pojo.getStarttime() + " endtime:" + pojo.getEndtime()
					+ " rtsp:" + pojo.getRtsp() + " url:" + pojo.getUrl() + "]");
		}
	}
}