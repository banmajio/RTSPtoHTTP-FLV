package com.junction.util;

import static org.bytedeco.ffmpeg.global.avcodec.av_packet_unref;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import org.bytedeco.ffmpeg.avcodec.AVPacket;
import org.bytedeco.ffmpeg.avformat.AVFormatContext;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import com.junction.pojo.CameraPojo;
import com.junction.pojo.Config;

/**
 * @Title CameraPush.java
 * @description 拉流推流
 * @time 2019年12月16日 上午9:34:41
 * @author wuguodong
 **/
public class CameraPush {
	// 配置类
	private static Config config;

	// 通过applicationContext上下文获取Config类
	public static void setApplicationContext(ApplicationContext applicationContext) {
		config = applicationContext.getBean(Config.class);
	}

	public static Timer timer;

	protected FFmpegFrameGrabber grabber = null;// 解码器
	protected FFmpegFrameRecorder record = null;// 编码器
	int width;// 视频像素宽
	int height;// 视频像素高

	// 视频参数
	protected int audiocodecid;
	protected int codecid;
	protected double framerate;// 帧率
	protected int bitrate;// 比特率

	// 音频参数
	// 想要录制音频，这三个参数必须有：audioChannels > 0 && audioBitrate > 0 && sampleRate > 0
	private int audioChannels;
	private int audioBitrate;
	private int sampleRate;

	// 设备信息
	private CameraPojo cameraPojo;

	public CameraPush() {
		super();
	}

	public CameraPush(CameraPojo cameraPojo) {
		this.cameraPojo = cameraPojo;
	}

	/**
	 * 选择视频源
	 * 
	 * @author wuguodong
	 * @throws org.bytedeco.javacv.FrameGrabber.Exception
	 * @throws org.bytedeco.javacv.FrameGrabber.Exception
	 * @throws org.bytedeco.javacv.FrameGrabber.Exception
	 * @throws Exception
	 */
	public CameraPush from() throws Exception {
		// 采集/抓取器
		grabber = new FFmpegFrameGrabber(cameraPojo.getRtsp());

		// 解决ip输入错误时，grabber.start();出现阻塞无法释放grabber而导致后续推流无法进行；
		Socket rtspSocket = new Socket();
		Socket rtmpSocket = new Socket();
		// 建立TCP Scoket连接，超时时间1s，如果成功继续执行，否则return
		try {
			rtspSocket.connect(new InetSocketAddress(cameraPojo.getIp(), 554), 1000);
		} catch (IOException e) {
			grabber.stop();
			grabber.close();
			rtspSocket.close();
			System.err.println("与拉流地址建立连接失败...");
			return null;
		}

		try {
			rtmpSocket.connect(new InetSocketAddress(IpUtil.IpConvert(config.getPush_ip()),
					Integer.parseInt(config.getPush_port())), 1000);
		} catch (IOException e) {
			grabber.stop();
			grabber.close();
			rtspSocket.close();
			System.err.println("与推流地址建立连接失败...");
			return null;
		}

		if (cameraPojo.getRtsp().indexOf("rtsp") >= 0) {
			grabber.setOption("rtsp_transport", "tcp");// tcp用于解决丢包问题
		}
		// 设置采集器构造超时时间
		grabber.setOption("stimeout", "2000000");

		try {
			grabber.start();
			// 开始之后ffmpeg会采集视频信息，之后就可以获取音视频信息
			width = grabber.getImageWidth();
			height = grabber.getImageHeight();
			// 若视频像素值为0，说明采集器构造超时，程序结束
			if (width == 0 && height == 0) {
				System.err.println("[ERROR]   拉流超时...");
				grabber.stop();
				grabber.close();
				return null;
			}
			// 视频参数
			audiocodecid = grabber.getAudioCodec();
			System.err.println("音频编码：" + audiocodecid);
			codecid = grabber.getVideoCodec();
			framerate = grabber.getVideoFrameRate();// 帧率
			bitrate = grabber.getVideoBitrate();// 比特率
			// 音频参数
			// 想要录制音频，这三个参数必须有：audioChannels > 0 && audioBitrate > 0 && sampleRate > 0
			audioChannels = grabber.getAudioChannels();
			audioBitrate = grabber.getAudioBitrate();
			if (audioBitrate < 1) {
				audioBitrate = 128 * 1000;// 默认音频比特率
			}
		} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
			grabber.stop();
			grabber.close();
			return null;
		}

		return this;
	}

	/**
	 * 选择输出
	 * 
	 * @author wuguodong
	 * @throws Exception
	 */
	public CameraPush to() throws Exception {
		// 录制/推流器
		record = new FFmpegFrameRecorder(cameraPojo.getRtmp(), width, height);
		record.setVideoOption("crf", "28");// 画面质量参数，0~51；18~28是一个合理范围
		record.setGopSize(2);
		record.setFrameRate(framerate);
		record.setVideoBitrate(bitrate);

		record.setAudioChannels(audioChannels);
		record.setAudioBitrate(audioBitrate);
		record.setSampleRate(sampleRate);
		AVFormatContext fc = null;
		if (cameraPojo.getRtmp().indexOf("rtmp") >= 0 || cameraPojo.getRtmp().indexOf("flv") > 0) {
			// 封装格式flv
			record.setFormat("flv");
			record.setAudioCodecName("aac");
			record.setVideoCodec(codecid);
			fc = grabber.getFormatContext();
		}
		record.start(fc);
		return this;

	}

	/**
	 * 转封装
	 * 
	 * @author wuguodong
	 * @throws org.bytedeco.javacv.FrameGrabber.Exception
	 * @throws org.bytedeco.javacv.FrameRecorder.Exception
	 * @throws InterruptedException
	 */
	public CameraPush go(Thread nowThread)
			throws org.bytedeco.javacv.FrameGrabber.Exception, org.bytedeco.javacv.FrameRecorder.Exception {
		long err_index = 0;// 采集或推流导致的错误次数
		// 连续五次没有采集到帧则认为视频采集结束，程序错误次数超过5次即中断程序
		for (int no_frame_index = 0; no_frame_index < 5 || err_index < 5;) {
			try {
				// 用于中断线程时，结束该循环
				nowThread.sleep(1);
				AVPacket pkt = null;
				// 获取没有解码的音视频帧
				pkt = grabber.grabPacket();
				if (pkt == null || pkt.size() <= 0 || pkt.data() == null) {
					// 空包记录次数跳过
					no_frame_index++;
					err_index++;
					continue;
				}
				// 不需要编码直接把音视频帧推出去
				err_index += (record.recordPacket(pkt) ? 0 : 1);
				av_packet_unref(pkt);
			} catch (InterruptedException e) {
				// 销毁构造器
				grabber.stop();
				grabber.close();
				record.stop();
				record.close();
				System.err.println("设备中断推流成功...");
				break;
			} catch (org.bytedeco.javacv.FrameGrabber.Exception e) {
				err_index++;
			} catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
				err_index++;
			}
		}
		// 程序正常结束销毁构造器
		grabber.stop();
		grabber.close();
		record.stop();
		record.close();
		System.err.println("设备推流完毕...");
		return this;
	}
}
