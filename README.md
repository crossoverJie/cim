

<div align="center">  

<img src="https://i.loli.net/2020/02/21/rfOGvKlTcHCmM92.png"  /> 
<br/>

[![Build Status](https://img.shields.io/badge/cim-cross--im-brightgreen.svg)](https://github.com/crossoverJie/cim)
[![](https://badge.juejin.im/entry/5c2c000e6fb9a049f5713e26/likes.svg?style=flat-square)](https://juejin.im/post/5c2bffdc51882509181395d7)

📘[介绍](#介绍) |📽[视频演示](#视频演示) | 🏖[TODO LIST](#todo-list) | 🌈[系统架构](#系统架构) |💡[流程图](#流程图)|🌁[快速启动](#快速启动)|👨🏻‍✈️[内置命令](#客户端内置命令)|🎤[通信](#群聊私聊)|❓[QA](https://github.com/crossoverJie/cim/blob/master/doc/QA.md)|💌[联系作者](#联系作者)


</div>
<br/>


## 介绍

`CIM(CROSS-IM)` 一款面向开发者的 `IM(即时通讯)`系统；同时提供了一些组件帮助开发者构建一款属于自己可水平扩展的 `IM` 。

借助 `CIM` 你可以实现以下需求：

- `IM` 即时通讯系统。
- 适用于 `APP` 的消息推送中间件。
- `IOT` 海量连接场景中的消息透传中间件。

> 在使用或开发过程中有任何疑问都可[联系我](#联系作者)。

## 视频演示

> 点击下方链接可以查看视频版 Demo。

| YouTube | Bilibili|
| :------:| :------: | 
| [群聊](https://youtu.be/_9a4lIkQ5_o) [私聊](https://youtu.be/kfEfQFPLBTQ) | [群聊](https://www.bilibili.com/video/av39405501) [私聊](https://www.bilibili.com/video/av39405821) | 
| <img src="https://i.loli.net//2019//05//08//5cd1d9e788004.jpg"  height="295px" />  | <img src="https://i.loli.net//2019//05//08//5cd1da2f943c5.jpg" height="295px" />


## TODO LIST

* [x] [群聊](#群聊)
* [x] [私聊](#私聊)
* [x] [内置命令](#客户端内置命令)
* [x] [聊天记录查询](#聊天记录查询)。
* [x] [一键开启价值 2 亿的 `AI` 模式](#ai-模式)
* [x] 使用 `Google Protocol Buffer` 高效编解码
* [x] 根据实际情况灵活的水平扩容、缩容
* [x] 服务端自动剔除离线客户端
* [x] 客户端自动重连
* [x] [延时消息](#延时消息)
* [ ] 分组群聊
* [ ] SDK 开发包
* [ ] 离线消息
* [ ] 协议支持消息加密



## 系统架构

![](https://i.loli.net/2019/05/08/5cd1d45a156f1.jpg)

- `CIM` 中的各个组件均采用 `SpringBoot` 构建。
-  采用 `Netty` 构建底层通信。
-  `Redis` 存放各个客户端的路由信息、账号信息、在线状态等。
-  `Zookeeper` 用于 `IM-server` 服务的注册与发现。


### cim-server

`IM` 服务端；用于接收 `client` 连接、消息透传、消息推送等功能。

**支持集群部署。**

### cim-forward-route

消息路由服务器；用于处理消息路由、消息转发、用户登录、用户下线以及一些运营工具（获取在线用户数等）。

### cim-client

`IM` 客户端；给用户使用的消息终端，一个命令即可启动并向其他人发起通讯（群聊、私聊）。

## 流程图

![](https://i.loli.net/2019/05/08/5cd1d45b982b3.jpg)

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
nohup java -jar  /root/work/server0/cim-server-1.0.0-SNAPSHOT.jar --cim.server.port=9000 --app.zk.addr=zk地址  > /root/work/server0/log.file 2>&1 &
```

> cim-server 集群部署同理，只要保证 Zookeeper 地址相同即可。

### 部署路由服务器(cim-forward-route)

```shell
cp /cim/cim-server/cim-forward-route/target/cim-forward-route-1.0.0-SNAPSHOT.jar /xx/work/route0/
cd /xx/work/route0/
nohup java -jar  /root/work/route0/cim-forward-route-1.0.0-SNAPSHOT.jar --app.zk.addr=zk地址 --spring.redis.host=redis地址 --spring.redis.port=6379  > /root/work/route/log.file 2>&1 &
```

> cim-forward-route 本身就是无状态，可以部署多台；使用 Nginx 代理即可。


### 启动客户端

```shell
cp /cim/cim-client/target/cim-client-1.0.0-SNAPSHOT.jar /xx/work/route0/
cd /xx/work/route0/
java -jar cim-client-1.0.0-SNAPSHOT.jar --server.port=8084 --cim.user.id=唯一客户端ID --cim.user.userName=用户名 --cim.route.url=http://路由服务器:8083/
```

![](https://ws2.sinaimg.cn/large/006tNbRwly1fylgxjgshfj31vo04m7p9.jpg)
![](https://ws1.sinaimg.cn/large/006tNbRwly1fylgxu0x4uj31hy04q75z.jpg)

如上图，启动两个客户端可以互相通信即可。

### 本地启动客户端

#### 注册账号
```shell
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{
  "reqNo": "1234567890",
  "timeStamp": 0,
  "userName": "zhangsan"
}' 'http://路由服务器:8083/registerAccount'
```

从返回结果中获取 `userId`

```json
{
    "code":"9000",
    "message":"成功",
    "reqNo":null,
    "dataBody":{
        "userId":1547028929407,
        "userName":"test"
    }
}
```

#### 启动本地客户端
```shell
# 启动本地客户端
cp /cim/cim-client/target/cim-client-1.0.0-SNAPSHOT.jar /xx/work/route0/
cd /xx/work/route0/
java -jar cim-client-1.0.0-SNAPSHOT.jar --server.port=8084 --cim.user.id=上方返回的userId --cim.user.userName=用户名 --cim.route.url=http://路由服务器:8083/
```

## 客户端内置命令

| 命令 | 描述|
| ------ | ------ | 
| `:q!` | 退出客户端| 
| `:olu` | 获取所有在线用户信息 | 
| `:all` | 获取所有命令 | 
| `:q [option]` | 【:q 关键字】查询聊天记录 | 
| `:ai` | 开启 AI 模式 | 
| `:qai` | 关闭 AI 模式 | 
| `:pu` | 模糊匹配用户 | 
| `:info` | 获取客户端信息 | 
| `:emoji [option]` | 查询表情包 [option:页码] | 
| `:delay [msg] [delayTime]` | 发送延时消息 | 
| `:` | 更多命令正在开发中。。 | 

![](https://ws3.sinaimg.cn/large/006tNbRwly1fylh7bdlo6g30go01shdt.gif)

### 聊天记录查询

![](https://i.loli.net/2019/05/08/5cd1c310cb796.jpg)

使用命令 `:q 关键字` 即可查询与个人相关的聊天记录。

> 客户端聊天记录默认存放在 `/opt/logs/cim/`，所以需要这个目录的写入权限。也可在启动命令中加入 `--cim.msg.logger.path = /自定义` 参数自定义目录。



### AI 模式

![](https://i.loli.net/2019/05/08/5cd1c30e47d95.jpg)

使用命令 `:ai` 开启 AI 模式，之后所有的消息都会由 `AI` 响应。

`:qai` 退出 AI 模式。

### 前缀匹配用户名

![](https://i.loli.net/2019/05/08/5cd1c32ac3397.jpg)

使用命令 `:qu prefix` 可以按照前缀的方式搜索用户信息。

> 该功能主要用于在移动端中的输入框中搜索用户。 

### 群聊/私聊

#### 群聊

![](https://ws1.sinaimg.cn/large/006tNbRwly1fyli54e8e1j31t0056x11.jpg)
![](https://ws3.sinaimg.cn/large/006tNbRwly1fyli5yyspmj31im06atb8.jpg)
![](https://ws3.sinaimg.cn/large/006tNbRwly1fyli6sn3c8j31ss06qmzq.jpg)

群聊只需要在控制台里输入消息回车后即可发送，同时所有在线客户端都可收到消息。

#### 私聊

私聊首先需要知道对方的 `userID` 才能进行。

输入命令 `:olu` 可列出所有在线用户。

![](https://ws4.sinaimg.cn/large/006tNbRwly1fyli98mlf3j31ta06mwhv.jpg)

接着使用 `userId;;消息内容` 的格式即可发送私聊消息。

![](https://ws4.sinaimg.cn/large/006tNbRwly1fylib08qlnj31sk082zo6.jpg)
![](https://ws1.sinaimg.cn/large/006tNbRwly1fylibc13etj31wa0564lp.jpg)
![](https://ws3.sinaimg.cn/large/006tNbRwly1fylicmjj6cj31wg07c4qp.jpg)
![](https://ws1.sinaimg.cn/large/006tNbRwly1fylicwhe04j31ua03ejsv.jpg)

同时另一个账号收不到消息。
![](https://ws3.sinaimg.cn/large/006tNbRwly1fylie727jaj31t20dq1ky.jpg)



### emoji 表情支持

使用命令 `:emoji 1` 查询出所有表情列表，使用表情别名即可发送表情。

![](https://tva1.sinaimg.cn/large/006y8mN6ly1g6j910cqrzj30dn05qjw9.jpg)
![](https://tva1.sinaimg.cn/large/006y8mN6ly1g6j99hazg6j30ax03hq35.jpg)
 
### 延时消息

发送 10s 的延时消息：

```shell
:delay delayMsg 10
```

![](https://tva1.sinaimg.cn/large/006y8mN6ly1g7brppmokqg30gn07gafj.gif)

## 联系作者
- [crossoverJie@gmail.com](mailto:crossoverJie@gmail.com)
- 微信公众号

![index.jpg](https://i.loli.net/2021/10/12/ckQW9LYXSxFogJZ.jpg)



