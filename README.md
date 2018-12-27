

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

> 点击下图可以查看视频版 Demo。


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
* [x] 根据实际情况灵活的水平扩容、缩容。
* [x] 路由(`cim-forward-route`)服务器自身是无状态，可用 `Nginx` 代理支持高可用。
* [ ] 协议支持消息加密。
* [ ] 弱网情况下客户端自动上线。
* [ ] 更多的客户端路由策略。
* [ ] 远程通信更换为 `SpringCloud`。


## 系统架构

![](https://ws1.sinaimg.cn/large/006tNbRwly1fyldgiizhuj315o0r4n0k.jpg)

- `CIM` 中的各个组件均采用 `SpringBoot` 构建。
-  采用 `Netty` 构建底层通信。
-  `Redis` 存放各个客户端的路由信息、账号信息、在线状态等。
-  `Zookeeper` 用于 `IM-server` 服务的注册与发现。

## 流程图

![](https://ws1.sinaimg.cn/large/006tNbRwly1fylfxevl2ij30it0etaau.jpg)

- 客户端向 `route` 发起登录。
- 登录成功从 `Zookeeper` 中选择可用 `IM-server` 返回给客户端，并保存登录、路由信息到 `Redis`。
- 客户端向 `IM-server` 发起长连接，成功后保持心跳。
- 客户端下线时通过 `route` 清除状态信息。


## 快速启动

首先需要安装 `Zookeeper、Redis` 并保证网络通畅。

```shell
git clone https://github.com/crossoverJie/cim.git
cd cim
mvn -Dmaven.test.skip=true clean package
```

### 部署 IM-server(cim-server)

```shell
cp /cim/cim-server/target/cim-server-1.0.0-SNAPSHOT.jar /xx/work/server0/
cd /xx/work/server0/
nohup java -jar  /root/work/server/cim-server-1.0.0-SNAPSHOT.jar --cim.server.port=9000 --app.zk.addr=zk地址  > /root/work/server/log.file 2>&1 &
```

> cim-server 集群部署同理，只要保证 Zookeeper 地址相同即可。

### 部署路由服务器(cim-forward-route)

```shell
cp /cim/cim-server/cim-forward-route/target/cim-forward-route-1.0.0-SNAPSHOT.jar /xx/work/route0/
cd /xx/work/route0/
nohup java -jar  /root/work/route/cim-forward-route-1.0.0-SNAPSHOT.jar --app.zk.addr=zk地址 --spring.redis.host=redis地址  > /root/work/route/log.file 2>&1 &
```

> cim-forward-route 本身就是无状态，可以部署多台；使用 Nginx 代理即可。


### 启动客户端

```shell
cp /cim/cim-client/target/cim-client-1.0.0-SNAPSHOT.jar /xx/work/route0/
cd /xx/work/route0/
java -jar cim-client-1.0.0-SNAPSHOT.jar --server.port=8084 --cim.user.id=唯一客户端ID --cim.user.userName=用户名 --cim.group.route.request.url=http://路由服务器:8083/groupRoute --cim.server.route.request.url=http://路由服务器:8083/login
```

![](https://ws2.sinaimg.cn/large/006tNbRwly1fylgxjgshfj31vo04m7p9.jpg)
![](https://ws1.sinaimg.cn/large/006tNbRwly1fylgxu0x4uj31hy04q75z.jpg)

如上图，启动两个客户端可以互相通信即可。



## 客户端内置命令

| 命令 | 描述|
| ------ | ------ | 
| `:q` | 退出客户端| 
| `:olu` | 获取所有在线用户信息 | 
| `:all` | 获取所有命令 | 
| `:` | 更多命令真正开发中。。 | 

![](https://ws3.sinaimg.cn/large/006tNbRwly1fylh7bdlo6g30go01shdt.gif)

# 联系作者
- [crossoverJie@gmail.com](mailto:crossoverJie@gmail.com)
- 微信公众号

![](https://ws1.sinaimg.cn/large/006tKfTcly1ftmfdo6mhmj30760760t7.jpg)

