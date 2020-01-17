# RTSPtoRTMP
# 使用JavaCV开发的rtsp流转rtmp流并进行推流的流媒体服务

## 觉得不错就点个**star**吧！！！

>**个人博客：[banmajio's blog](https://www.banmajio.com/)**
>**csdn博客：[banmajio's csdn](https://blog.csdn.net/weixin_40777510)**

> 参考：[javaCV开发详解之8：转封装在rtsp转rtmp流中的应用（无须转码，更低的资源消耗）](https://blog.csdn.net/eguid_1/article/details/83025621)

## 用到的技术：FFmpeg、JavaCV、ngingx

## 项目背景：将海康摄像头的rtsp流转为rtmp流，配合video.js实现web端播放。

## [注]：
该项目中的一些处理是为了满足公司项目需求添加完善的，如果需要改造扩展只需要在原来的基础上进行扩充或者剥离即可。最基本的核心操作在CameraPush.java这个类中，或者参考上述链接原作者的代码。

## 该项目需要搭配使用的nginx服务器下载地址：
[http://cdn.banmajio.com/nginx.rar](http://cdn.banmajio.com/nginx.rar)
下载后解压该文件，点击nginx.exe（闪退是正常的，可以通过任务管理器查看是否存在nginx进程，存在则说明启动成功了）启动nginx服务。nginx的配置文件存放在conf目录下的nginx.conf，根据需要修改。项目中的rtmp地址就是根据这个配置文件来的。

## 待优化之处：

1.如果服务部署在Docker环境下，本机ip是动态的，并非固定为127.0.0.1，所以需要动态获取nginx域名解析为ip，rtmp推送地址才能生效，可以使用InetAddress.getByName(www.baidu.com).getHostAddress();这样的方式获取解析到的ip地址。
2.目前出现的一个bug尚未解决，如果传入的设备ip填写错误，在JavaCV的FFmpegFrameGrabber构造器在调用start()方法是会出现阻塞现象，导致构造器无法释放，后续推流工作无法继续。【已优化：[JavaCV中FFmpegFrameGrabber调用start()方法时出现阻塞的解决办法](https://www.banmajio.com/post/9bf41e2c.html#more)】
3.目前项目进行历史回放的思路是直接通过rtsp命令添加starttime 和 endtime参数 拉取海康摄像头的rtsp流完成的，存在的问题就是会出现拉到的流解析出来的内容都是空的，而同样的指令在cmd下面确没有问题。该问题目前尚未解决，如果有好的思路可以联系我。

>**项目搭建过程请参考本人博文：[FFmpeg转封装rtsp到rtmp（无需转码，低资源消耗）](https://www.banmajio.com/post/638986b0.html#more)**

>如果有不错的建议或者反馈可以通过QQ：1402325991   邮箱：banmajio@163.com

**主分支只作为一个简单的模板供大家使用，所以一些新加的功能就不合并到主分支了，如果有pr，请提交到pr分支内。**

**感谢[nn200433](https://github.com/nn200433)小伙伴对本项目的支持，详细改动请参考rp分支内的提交内容**


