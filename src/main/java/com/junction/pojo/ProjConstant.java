package com.junction.pojo;

/**
 * 项目常量类
 *
 * @Author: 猎隼丶止戈
 * @Date: 2020年01月10日 0010 16:13:41
 */
public class ProjConstant {

    /**
     * 海康威视直播流 RTSP地址
     */
    public static final String HIKVISION_RTSP_LIVE_URL = "rtsp://{}:{}@{}:554/h264/ch{}/{}/av_stream";

    /**
     * 海康威视直播流 RTMP地址
     */
    public static final String HIKVISION_RTMP_LIVE_URL = "rtmp://{}:{}/live/{}";

    /**
     * 海康威视回看流 RTSP地址
     */
    public static final String HIKVISION_RTSP_HISTORY_URL = "rtsp://{}:{}@{}:554/Streaming/tracks/{}01?starttime={}t{}z'&'endtime={}t{}z";

    /**
     * 海康威视回看流 RTMP地址
     */
    public static final String HIKVISION_RTMP_HISTORY_URL = "rtmp://{}:{}/history/{}";

    /**
     * 大华直播流 RTSP地址
     * <pre>
     * 例子：
     *  rtsp://username:password@ip:port/cam/realmonitor?channel=1&subtype=0
     *  username：账号
     *  password：密码
     *  ip：设备IP地址
     *  port：端口（默认是554）
     *  channel：通道
     *  subtype：码流类型，主码流为0
     * </pre>
     */
    public static final String DAHUATECH_RTSP_LIVE_URL = "rtsp://{}:{}@{}:554/cam/realmonitor?channel={}&subtype={}";

}
