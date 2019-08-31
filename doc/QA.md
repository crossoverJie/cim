# 以下问题由网友问答整理而来


## 部署 server 要不要加端口号？

![](https://ws2.sinaimg.cn/large/006tNbRwly1fymb41bob6j31g90c9dk6.jpg)

`server` 端口号通过 `cim-server.port` 设置，同一台服务器启动多个 `server` 只要保证端口号唯一即可。

## 部署路由服务器 `zk` 和 `redis` 地址加不加端口？

![](https://ws2.sinaimg.cn/large/006tNbRwly1fymb9wgo5hj31g909jjv6.jpg)


```
spring.redis.host=xx 
 spring.redis.port=6379
```

其实所有的配置都是通过 `SpringBoot` 来加载的，看这个配置就知道了。
如果不加会默认使用 jar 包里的配置。


## 本地启动 路由服务器写 `127.0.0.1` 吗？

![](https://ws4.sinaimg.cn/large/006tNbRwly1fymbc9lzidj31g908g0xb.jpg)

本地的路由服务器是多少就是多少，本机肯定就是 `127.0.0.1`.


## 本地启动如何注册账号？

`cim-forward-route` 服务启动之后有一个 `registerAccount` 接口可以注册账号。

![](https://ws2.sinaimg.cn/large/006tNbRwly1fymbjn98f6j31bn0u0aff.jpg)

账号信息会存放在 `Redis`。


## 本地如何模拟调试？

至少需要启动以下服务:

1. 服务端
2. 路由
3. 至少两个客户端
4. `redis`、`zk` 基础组件