## RTSPtoHTTP-FLV 使用JavaCV开发的rtsp流转http-flv流（rtmp已不推荐）并进行推流的流媒体服务

**求star！！！**

#### 提问求助等优先提交issues，让其他遇到同样问题的朋友可以很方便找到解决方式，尽量避免直接加微信qq咨询。业务合作可发邮件到banmajio@163.com或添加微信qq咨询。

### 各大浏览器目前均已不再支持flash，故推荐使用http-flv来代替rtmp使用。
>[参考资料](https://blog.csdn.net/weixin_40777510/article/details/106693408)
>只需修改本项目controller中rtmp地址生成的地方改为生成http-flv地址即可，各流媒体服务器对于http-flv地址规则可能会有差异，根据所选流媒体服务器来制定http-flv地址。

>**个人博客：[banmajio's blog](https://www.banmajio.com/)**
>**csdn博客：[banmajio's csdn](https://blog.csdn.net/weixin_40777510)**
>**gitee地址：[RTSPtoRTMP](https://gitee.com/banmajio/RTSPtoRTMP)**

### 可以实现各h264编码的监控设备rtsp流转rtmp流（只需要改动controller中rtsp指令的拼接格式）

**接口调用方式：[接口文档](https://github.com/banmajio/RTSPtoRTMP/wiki/%E6%8E%A5%E5%8F%A3%E6%96%87%E6%A1%A3)**

#### [注]：
该项目中的一些处理是为了满足公司项目需求添加完善的，如果需要改造扩展只需要在原来的基础上进行扩充或者剥离即可。最基本的核心操作在CameraPush.java这个类中。

#### 该项目需要搭配使用的nginx服务器下载地址：
[http://cdn.banmajio.com/nginx.rar](http://cdn.banmajio.com/nginx.rar)
下载后解压该文件，点击nginx.exe（闪退是正常的，可以通过任务管理器查看是否存在nginx进程，存在则说明启动成功了）启动nginx服务。nginx的配置文件存放在conf目录下的nginx.conf，根据需要修改。项目中的rtmp地址就是根据这个配置文件来的。

### 存在的问题：
1.部分设备或NVR在进行历史回放时，会出现带宽不足的报错，暂不清楚造成该情况的具体原因。如果出现rtsp地址带时间戳参数进行历史回放出现报错或者无法播放的情况，请考虑使用厂家提供的sdk进行二次开发，捕获码流数据自行处理推成rtmp流。
>**出现此问题的原因参考：**[使用rtsp带starttime和endtime进行历史回放报453 Not Enough Bandwidth（带宽不足）](https://blog.csdn.net/weixin_40777510/article/details/106802234) 

2.对于上述历史回放的问题，现在已经通过对接海康的sdk进行二次开发，通过sdk回调的码流数据自行处理推到rtmp。
>**实现思路参考：**[海康sdk捕获码流数据通过JavaCV推成rtmp流的实现思路(PS流转封装RTMP)](https://blog.csdn.net/weixin_40777510/article/details/105840823)

>**项目搭建过程请参考本人博文：[FFmpeg转封装rtsp到rtmp（无需转码，低资源消耗）](https://www.banmajio.com/post/638986b0.html#more)**

>**开发过程的遇到的一些问题和解决方法，会发布到csdn博客中，[banmajio csdn](https://blog.csdn.net/weixin_40777510)**

**感谢[nn200433](https://github.com/nn200433)小伙伴对本项目的支持，详细改动请参考rp分支内的提交内容**

### 碎银打赏，以资奖励
<img src="https://images.gitee.com/uploads/images/2020/0421/174552_a862b4ed_5186477.jpeg" width="200px" />

<img src="https://images.gitee.com/uploads/images/2020/0421/174726_cb99c1d6_5186477.jpeg" width="200px" />
