# netty-action


## 安装

```shell
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

