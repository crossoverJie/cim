# netty-action

[![QQ群](https://img.shields.io/badge/QQ%E7%BE%A4-787381170-yellowgreen.svg)](https://jq.qq.com/?_wv=1027&k=5HPYvQk)

Netty 实战相关


## TODO LIST

* [x] [Netty(一) SpringBoot 整合长连接心跳机制](https://crossoverjie.top/2018/05/24/netty/Netty(1)TCP-Heartbeat/)
* [x] [Netty(二) 从线程模型的角度看 Netty 为什么是高性能的？](https://crossoverjie.top/2018/07/04/netty/Netty(2)Thread-model/)


## 安装

```shell
git clone https://github.com/crossoverJie/netty-action.git

cd netty-action

mvn -Dmaven.test.skip=true clean package
``` 

## 启动

```shell
-- 启动 SBA
java -jar springboot-admin-1.0.0-SNAPSHOT.jar

-- 启动 服务端
java -jar netty-action-hearbeat-1.0.0-SNAPSHOT.jar

-- 启动 客户端
java -jar netty-action-heartbeat-client-1.0.0-SNAPSHOT.jar

-- 启动 第二个客户端
java -jar netty-action-heartbeat-client-1.0.0-SNAPSHOT.jar --server.port=8083 --spring.application.name=netty-heartbeat-client2 --logging.level.root=info --channel.id=101
```

## 截图
![show](https://github.com/crossoverJie/netty-action/blob/master/pic/show.gif)
![](https://ws4.sinaimg.cn/large/006tKfTcgy1frqfwembi6j31kw0o0dot.jpg)


# 联系作者
- [crossoverJie@gmail.com](mailto:crossoverJie@gmail.com)
- 微信公众号

![](https://ws1.sinaimg.cn/large/006tKfTcly1frz6eaf3s4j308c0au0ss.jpg)

