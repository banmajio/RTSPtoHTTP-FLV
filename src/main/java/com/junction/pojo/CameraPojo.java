package com.junction.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CameraPojo implements Serializable {
    private static final long serialVersionUID = 8183688502930584159L;
    /**
     * @description: 摄像头账号
     * @author: banmajio
     * @date: 09:20
     */
    private String username;
    /**
     * @description: 摄像头密码
     * @author: banmajio
     * @date: 2023/8/30 09:21
     */
    private String password;
    /**
     * @description: 摄像头ip
     * @author: banmajio
     * @date: 2023/8/30 09:21
     */
    private String ip;
    /**
     * @description: 摄像头通道
     * @author: banmajio
     * @date: 2023/8/30 09:21
     */
    private String channel;
    /**
     * @description: 摄像头码流
     * @author: banmajio
     * @date: 2023/8/30 09:21
     */
    private String stream;
    /**
     * @description: rtsp地址
     * @author: banmajio
     * @date: 2023/8/30 09:21
     */
    private String rtsp;
    /**
     * @description: rtmp地址
     * @author: banmajio
     * @date: 2023/8/30 09:22
     */
    private String rtmp;
    /**
     * @description: 播放地址
     * @author: banmajio
     * @date: 2023/8/30 09:22
     */
    private String url;
    /**
     * @description: 回放开始时间
     * @author: banmajio
     * @date: 2023/8/30 09:22
     */
    private String starttime;
    /**
     * @description: 回放结束时间
     * @author: banmajio
     * @date: 2023/8/30 09:22
     */
    private String endTime;
    /**
     * @description: 打开时间
     * @author: banmajio
     * @date: 2023/8/30 09:22
     */
    private String opentime;
    /**
     * @description: 使用人数
     * @author: banmajio
     * @date: 2023/8/30 09:22
     */
    private int count = 0;
    /**
     * @description: token
     * @author: banmajio
     * @date: 2023/8/30 09:22
     */
    private String token;
}
