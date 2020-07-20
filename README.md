# RTSPtoRTMP
# 使用JavaCV开发的rtsp流转rtmp流并进行推流的流媒体服务

## 点个star吧！
**本着贡献社区的想法将此项目开源出来，虽然对很多大佬没什么参考价值，但是对于一些像我一样的小白来说，可以少走一些弯路，更快的入门。（回想之前自己刚接触流媒体的时候，三个多月的开发之路几乎都是在自闭中度过的；网上资料太少，找不到合适的参考demo）因此将此项目开源出来，将自己在开发过程中遇到的一些问题更新到博客中，让大家在遇到同样问题的时候不会束手无策。**

**开源不易，开发不易！！ 如果该项目对你有所帮助，有钱的捧个钱场打赏一下，没钱的捧个人场给小弟的博客点点关注；项目点点star。感激不尽！！！**

>**个人博客：[banmajio's blog](https://www.banmajio.com/)**
>**csdn博客：[banmajio's csdn](https://blog.csdn.net/weixin_40777510)**
>**gitee地址：[RTSPtoRTMP](https://gitee.com/banmajio/RTSPtoRTMP)**

> 参考：[javaCV开发详解之8：转封装在rtsp转rtmp流中的应用（无须转码，更低的资源消耗）](https://blog.csdn.net/eguid_1/article/details/83025621)

## 用到的技术：FFmpeg、JavaCV、nginx

## 项目背景：将海康摄像头的rtsp流转为rtmp流，配合video.js实现web端播放。

## [注]：
该项目中的一些处理是为了满足公司项目需求添加完善的，如果需要改造扩展只需要在原来的基础上进行扩充或者剥离即可。最基本的核心操作在CameraPush.java这个类中，或者参考上述链接原作者的代码。

## 该项目需要搭配使用的nginx服务器下载地址：
[http://cdn.banmajio.com/nginx.rar](http://cdn.banmajio.com/nginx.rar)
下载后解压该文件，点击nginx.exe（闪退是正常的，可以通过任务管理器查看是否存在nginx进程，存在则说明启动成功了）启动nginx服务。nginx的配置文件存放在conf目录下的nginx.conf，根据需要修改。项目中的rtmp地址就是根据这个配置文件来的。

## 存在的问题：

1.因为海康nvr的限制，在进行历史回放的时候，会存在报错：**453 Not Enough Bandwidth（带宽不足）；**目前无法解决该问题。
但是如果仅仅为了直播，该项目可以满足需求。
>**出现此问题的原因参考：**[使用rtsp带starttime和endtime进行历史回放报453 Not Enough Bandwidth（带宽不足）](https://blog.csdn.net/weixin_40777510/article/details/106802234) 

2.对于上述历史回放的问题，现在已经通过对接海康的sdk进行二次开发，通过sdk回调的码流数据自行处理推到rtmp。
>**实现思路参考：**[海康sdk捕获码流数据通过JavaCV推成rtmp流的实现思路(PS流转封装RTMP)](https://blog.csdn.net/weixin_40777510/article/details/105840823)


>**项目搭建过程请参考本人博文：[FFmpeg转封装rtsp到rtmp（无需转码，低资源消耗）](https://www.banmajio.com/post/638986b0.html#more)**

>**开发过程的遇到的一些问题和解决方法，会发布到csdn博客中，[banmajio csdn](https://blog.csdn.net/weixin_40777510)**

>如果有不错的建议或者反馈可以通过QQ群：1091194974   邮箱：banmajio@163.com

**主分支只作为一个简单的模板供大家使用，所以一些新加的功能就不合并到主分支了，如果有pr，请提交到pr分支内。**

**感谢[nn200433](https://github.com/nn200433)小伙伴对本项目的支持，详细改动请参考rp分支内的提交内容**

## 碎银打赏，以资奖励
<img src="https://images.gitee.com/uploads/images/2020/0421/174552_a862b4ed_5186477.jpeg" width="200px" />

<img src="https://images.gitee.com/uploads/images/2020/0421/174726_cb99c1d6_5186477.jpeg" width="200px" />
