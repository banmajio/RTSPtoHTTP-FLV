package com.junction.pojo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author banmajio
 * @Title ConfigPojo.java
 * @description 读取配置文件的bean
 * @time 2019年12月25日 下午5:11:21
 **/
@Component
@ConfigurationProperties(prefix = "config")
@Data
public class Config {
    /**
     * @description: 保活时长（分钟）
     * @author: banmajio
     * @date: 2023/8/30 09:26
     */
    private String keepalive;
    /**
     * @description: 推送地址
     * @author: banmajio
     * @date: 2023/8/30 09:26
     */
    private String pushHost;
    /**
     * @description: 额外地址
     * @author: banmajio
     * @date: 2023/8/30 09:26
     */
    private String hostExtra;
    /**
     * @description: 推送端口
     * @author: banmajio
     * @date: 2023/8/30 09:26
     */
    private String pushPort;
    /**
     * @description: 主码流最大码率
     * @author: banmajio
     * @date: 2023/8/30 09:26
     */
    private String mainCode;
    /**
     * @description: 主码流最大码率
     * @author: banmajio
     * @date: 2023/8/30 09:26
     */
    private String subCode;
    /**
     * @description: 版本信息
     * @author: banmajio
     * @date: 2023/8/30 09:26
     */
    private String version;

}
