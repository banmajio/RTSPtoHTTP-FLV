package com.junction.push;

import com.junction.pojo.CameraPojo;
import com.junction.pojo.Config;
import lombok.Data;
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

import java.util.HashMap;
import java.util.Map;

import static org.bytedeco.ffmpeg.global.avcodec.av_packet_unref;

/**
 * @author banmajio
 * @Title RtmpPush.java
 * @description javacv推数据帧
 * @time 2020年3月17日 下午2:32:42
 **/
@Data
public class CameraPush {
    private final static Logger logger = LoggerFactory.getLogger(CameraPush.class);
    private static Config config;

    /**
     * @description: 通过applicationContext上下文获取Config类
     * @author: banmajio
     */
    public static void setApplicationContext(ApplicationContext applicationContext) {
        config = applicationContext.getBean(Config.class);
    }

    /**
     * @description: 设备信息
     * @author: banmajio
     * @date: 2023/8/30 09:43
     */
    private CameraPojo pojo;
    /**
     * @description: 解码器
     * @author: banmajio
     * @date: 2023/8/30 09:43
     */
    private FFmpegFrameRecorder recorder;
    /**
     * @description: 采集器
     * @author: banmajio
     * @date: 2023/8/30 09:43
     */
    private FFmpegFrameGrabber grabber;
    /**
     * @description: 推流过程中出现错误的次数
     * @author: banmajio
     * @date: 2023/8/30 09:43
     */
    private int errIndex = 0;
    /**
     * @description: 退出状态码：0-正常退出;1-手动中断;
     * @author: wuguodong
     * @date: 2023/8/30 09:44
     */
    private int exitCode = 0;
    /**
     * @description: 帧率
     * @author: wuguodong
     * @date: 2023/8/30 09:44
     */
    private double frameRate = 0;

    public CameraPush(CameraPojo pojo) {
        this.pojo = pojo;
    }

    /**
     * @return void
     * @Title: release
     * @Description:资源释放
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
     * @return void
     * @Title: push
     * @Description:推送视频流数据包
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
                grabber.start(config.getSubCode());
            } else if ("main".equals(pojo.getStream())) {
                grabber.start(config.getMainCode());
            } else {
                grabber.start(config.getMainCode());
            }

            // 部分监控设备流信息里携带的帧率为9000，如出现此问题，会导致dts、pts时间戳计算失败，播放器无法播放，故出现错误的帧率时，默认为25帧
            if (grabber.getFrameRate() > 0 && grabber.getFrameRate() < 100) {
                frameRate = grabber.getFrameRate();
            } else {
                frameRate = 25.0;
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
            recorder.setGopSize((int) frameRate * 2);
            // 视频帧率(保证视频质量的情况下最低25，低于25会出现闪屏)
            recorder.setFrameRate(frameRate);
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
                    + pojo.getStream() + " starttime:" + pojo.getStarttime() + " endtime:" + pojo.getEndTime()
                    + " rtsp:" + pojo.getRtsp() + " url:" + pojo.getUrl() + "]");
            // 清空探测时留下的缓存
            grabber.flush();

            AVPacket pkt;
            long dts = 0;
            long pts = 0;
            int timebase = 0;
            for (int noFrameIndex = 0; noFrameIndex < 5 && errIndex < 5; ) {
                long time1 = System.currentTimeMillis();
                if (exitCode == 1) {
                    break;
                }
                pkt = grabber.grabPacket();
                if (pkt == null || pkt.size() == 0 || pkt.data() == null) {
                    // 空包记录次数跳过
                    logger.warn("JavaCV 出现空包 设备信息：[ip:" + pojo.getIp() + " channel:" + pojo.getChannel() + " stream:"
                            + pojo.getStream() + " starttime:" + pojo.getStarttime() + " endtime:" + " rtsp:"
                            + pojo.getRtsp() + pojo.getEndTime() + " url:" + pojo.getUrl() + "]");
                    noFrameIndex++;
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
                errIndex += (recorder.recordPacket(pkt) ? 0 : 1);
                // pts,dts累加
                timebase = grabber.getFormatContext().streams(pkt.stream_index()).time_base().den();
                pts += timebase / (int) frameRate;
                dts += timebase / (int) frameRate;
                // 将缓存空间的引用计数-1，并将Packet中的其他字段设为初始值。如果引用计数为0，自动的释放缓存空间。
                av_packet_unref(pkt);

                long endTime = System.currentTimeMillis();
                if ((long) (1000 / frameRate) - (endTime - time1) > 0) {
                    Thread.sleep((long) (1000 / frameRate) - (endTime - time1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
        } finally {
            release();
            logger.info("推流结束 设备信息：[ip:" + pojo.getIp() + " channel:" + pojo.getChannel() + " stream:"
                    + pojo.getStream() + " starttime:" + pojo.getStarttime() + " endtime:" + pojo.getEndTime()
                    + " rtsp:" + pojo.getRtsp() + " url:" + pojo.getUrl() + "]");
        }
    }
}
