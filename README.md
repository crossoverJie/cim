

<div align="center">  

<img src="https://ws4.sinaimg.cn/large/006tNbRwly1fylahz0rrgj30p008ca9x.jpg"  /> 
<br/>

[![Build Status](https://img.shields.io/badge/cim-cross--im-brightgreen.svg)](https://github.com/crossoverJie/cim)
[![QQ群](https://img.shields.io/badge/QQ%E7%BE%A4-787381170-yellowgreen.svg)](https://jq.qq.com/?_wv=1027&k=5HPYvQk)



</div>
<br/>


## 介绍

`CIM(CROSS IM)` 一款面向开发者的 `IM(即时通讯)`系统；同时提供了一些组件帮助开发者构建一款属于自己可水平扩展的 `IM` 系统。

借助 `CIM` 你可以实现以下需求：

- `IM` 即时通讯系统。
- 适用于 `APP` 的消息推送中间件。
- `IOT` 海量连接场景中的消息透传中间件。

> 老版本已经归档到[这里](https://github.com/crossoverJie/cim/releases/tag/archive-20181227)。

### cim-server

`IM` 服务端；用于接收 `client` 连接、消息透传、消息推送等功能。

**支持集群部署。**

### cim-forward-route

消息路由服务器；用于处理消息路由、消息转发、用户登录、用户下线以及一些运营工具（获取在线用户数等）。

### cim-client

`IM` 客户端；给用户使用的消息终端，一个命令即可启动并向其他人发起通讯（群聊、私聊）。



## TODO LIST

* [x] 群聊。
* [x] 私聊。
* [x] 内置命令。
* [x] 使用 `Google Protocol Buffer` 高效编解码。
* [ ] 协议支持消息加密。
* [ ] 弱网情况下的客户端自动上线。
* [ ] 更多的客户端路由策略。
* [ ] 远程通信更换为 `SpringCloud`。


## 系统架构

![](https://ws2.sinaimg.cn/large/006tNbRwly1fylev4s5w9j30ry0i440m.jpg)

- `CIM` 中的各个组件均采用 `SpringBoot` 构建。
-  采用 `Netty` 构建底层通信。
-  `Redis` 存放各个客户端的路由信息、账号信息、在线状态等。
-  `Zookeeper` 用于 `cim-server` 服务的注册与发现。

## 流程图




# 联系作者
- [crossoverJie@gmail.com](mailto:crossoverJie@gmail.com)
- 微信公众号

![](https://ws1.sinaimg.cn/large/006tKfTcly1ftmfdo6mhmj30760760t7.jpg)

