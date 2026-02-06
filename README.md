


<div align="center">

<img src="https://i.loli.net/2020/02/21/rfOGvKlTcHCmM92.png"  />
<br/>

[![codecov](https://codecov.io/gh/crossoverJie/cim/graph/badge.svg?token=oW5Gd1oKmf)](https://codecov.io/gh/crossoverJie/cim)
[![Build Status](https://img.shields.io/badge/cim-cross--im-brightgreen.svg)](https://github.com/crossoverJie/cim)
[![](https://badge.juejin.im/entry/5c2c000e6fb9a049f5713e26/likes.svg?style=flat-square)](https://juejin.im/post/5c2bffdc51882509181395d7)

📘[Introduction](#introduction) |📽[Video Demo](#video-demo) | 🏖[TODO LIST](#todo-list) | 🌈[Architecture](#architecture) |💡[Flow Chart](#flow-chart)|🌁[Quick Start](#quick-start)|👨🏻‍✈️[Built-in Commands](#built-in-commands)|🎤[Chat](#group-chatprivate-chat)|❓[QA](https://github.com/crossoverJie/cim/blob/master/doc/QA.md)|💌[Contact](#contact)

[中文文档](README-zh.md)

</div>
<br/>

# V2.0
- [x] Upgrade to JDK17 & springboot3.0
- [x] Client SDK
- [ ] Client use [picocli](https://picocli.info/) instead of springboot.
- [x] Support integration testing.
- [ ] Integrate OpenTelemetry .
- [ ] Support single node startup(Contains no components).
- [ ] Third-party components support replacement(Redis/Zookeeper, etc.).
- [ ] Support web client(websocket).
- [x] Support docker container.
- [ ] Support kubernetes operation.
- [ ] Supports binary client(build with golang).

## Introduction

`CIM(CROSS-IM)` is an `IM (instant messaging)` system for developers; it also provides some components to help developers build their own scalable `IM`.
Using `CIM`, you can achieve the following requirements:
- `IM` instant messaging system.
- Message push middleware for `APP`.
- Message middleware for `IOT` massive connection scenarios.

> If you have any questions during use or development, you can [contact the author](#contact).

## Video Demo

> Click the links below to watch the video demo.

| YouTube | Bilibili|
| :------:| :------: |
| [Group Chat](https://youtu.be/_9a4lIkQ5_o) [Private Chat](https://youtu.be/kfEfQFPLBTQ) | [Group Chat](https://www.bilibili.com/video/av39405501) [Private Chat](https://www.bilibili.com/video/av39405821) |
| <img src="https://i.loli.net//2019//05//08//5cd1d9e788004.jpg"  height="295px" />  | <img src="https://i.loli.net//2019//05//08//5cd1da2f943c5.jpg" height="295px" />

![demo.gif](pic/demo.gif)

## TODO LIST

* [x] [Group Chat](#group-chat)
* [x] [Private Chat](#private-chat)
* [x] [Built-in Commands](#built-in-commands)
* [x] [Chat History Query](#chat-history-query)
* [x] [AI Mode](#ai-mode)
* [x] Efficient encoding/decoding with `Google Protocol Buffer`
* [x] Flexible horizontal scaling based on actual needs
* [x] Server-side automatic removal of offline clients
* [x] Client automatic reconnection
* [x] [Delayed Messages](#delayed-messages)
* [x] SDK development package
* [ ] Group categorization
* [ ] Offline messages
* [ ] Message encryption



## Architecture

![](pic/architecture.png)

- Each component in `CIM` is built using `SpringBoot`
  - Client build with [cim-client-sdk](https://github.com/crossoverJie/cim/tree/master/cim-client-sdk)
- Use `Netty` to build the underlying communication.
- `MetaStore` is used for registration and discovery of `IM-server` services.


### cim-server
IM server is used to receive client connections, message forwarding, message push, etc.
Support cluster deployment.

### cim-route

Route server; used to process message routing, message forwarding, user login, user offline, and some operation tools (get the number of online users, etc.).

### cim-client
IM client terminal, a command can be started and initiated to communicate with others (group chat, private chat).

## Flow Chart

![](https://s2.loli.net/2024/10/13/8teMn7BSa5VWuvi.png)

- Server register to `MetaStore`
- Route subscribe `MetaStore`
- Client login to Route
  - Route get Server info from `MetaStore`
- Client open connection to Server
- Client1 send message to Route
- Route select Server and forward message to Server
- Server push message to Client2


## Quick Start

### Docker

The `allin1` image comes with Zookeeper, Redis, cim-server, and cim-forward-route pre-installed, all managed by [Supervisor](http://supervisord.org/) for an out-of-the-box experience.

**Supported platforms:** linux/amd64, linux/arm64, linux/arm/v7

**Port mapping:**

| Port | Service | Description |
|------|---------|-------------|
| 2181 | Zookeeper | Service registration & discovery |
| 6379 | Redis | Data caching |
| 8083 | Route Server | HTTP API routing service |

Pull the image and start the container:

```shell
docker pull ghcr.io/crossoverjie/allin1-ubuntu:latest
docker run -p 2181:2181 -p 6379:6379 -p 8083:8083 --rm --name cim-allin1 ghcr.io/crossoverjie/allin1-ubuntu:latest
```

After the container starts, refer to the [Register Account](#register-account) and [Start Client](#start-client) sections below to experience the full IM workflow.

### Build Docker Image Locally

To build the Docker image from source:

```shell
# Run from the project root directory
docker build -t cim-allin1:latest -f docker/allin1-ubuntu.Dockerfile .
docker run -p 2181:2181 -p 6379:6379 -p 8083:8083 --rm --name cim-allin1 cim-allin1:latest
```

### Build from Source

First, install `Zookeeper` and `Redis` and ensure the network is accessible.

```shell
docker run --rm --name zookeeper -d -p 2181:2181 zookeeper:3.9.2
docker run --rm --name redis -d -p 6379:6379 redis:7.4.0
```

```shell
git clone https://github.com/crossoverJie/cim.git
cd cim
mvn clean install -DskipTests=true
cd cim-server && cim-client && cim-forward-route
mvn clean package spring-boot:repackage -DskipTests=true
```

### Deploy IM-server (cim-server)

```shell
cp /cim/cim-server/target/cim-server-1.0.0-SNAPSHOT.jar /xx/work/server0/
cd /xx/work/server0/
nohup java -jar  /root/work/server0/cim-server-1.0.0-SNAPSHOT.jar --cim.server.port=9000 --app.zk.addr=<zk-address>  > /root/work/server0/log.file 2>&1 &
```

> For cim-server cluster deployment, just ensure all instances point to the same Zookeeper address.

### Deploy Route Server (cim-forward-route)

```shell
cp /cim/cim-server/cim-forward-route/target/cim-forward-route-1.0.0-SNAPSHOT.jar /xx/work/route0/
cd /xx/work/route0/
nohup java -jar  /root/work/route0/cim-forward-route-1.0.0-SNAPSHOT.jar --app.zk.addr=<zk-address> --spring.redis.host=<redis-address> --spring.redis.port=6379  > /root/work/route/log.file 2>&1 &
```

> cim-forward-route is stateless and can be deployed on multiple nodes; use Nginx as a reverse proxy.


### Start Client

```shell
cp /cim/cim-client/target/cim-client-1.0.0-SNAPSHOT.jar /xx/work/route0/
cd /xx/work/route0/
java -jar cim-client-1.0.0-SNAPSHOT.jar --server.port=8084 --cim.user.id=<unique-client-id> --cim.user.userName=<username> --cim.route.url=http://<route-server>:8083/
```

![](https://ws2.sinaimg.cn/large/006tNbRwly1fylgxjgshfj31vo04m7p9.jpg)
![](https://ws1.sinaimg.cn/large/006tNbRwly1fylgxu0x4uj31hy04q75z.jpg)

As shown above, two clients can communicate with each other.

### Local Client Startup

#### Register Account
```shell
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{
  "reqNo": "1234567890",
  "timeStamp": 0,
  "userName": "zhangsan"
}' 'http://<route-server>:8083/registerAccount'
```

Get the `userId` from the response:

```json
{
    "code":"9000",
    "message":"success",
    "reqNo":null,
    "dataBody":{
        "userId":1547028929407,
        "userName":"test"
    }
}
```

#### Start Local Client
```shell
# Start local client
cp /cim/cim-client/target/cim-client-1.0.0-SNAPSHOT.jar /xx/work/route0/
cd /xx/work/route0/
java -jar cim-client-1.0.0-SNAPSHOT.jar --server.port=8084 --cim.user.id=<userId-from-above> --cim.user.userName=<username> --cim.route.url=http://<route-server>:8083/
```

## Built-in Commands

| Command | Description |
| ------ | ------ |
| `:q!` | Quit the client |
| `:olu` | List all online users |
| `:all` | Show all available commands |
| `:q [keyword]` | Search chat history by keyword |
| `:ai` | Enable AI mode |
| `:qai` | Disable AI mode |
| `:pu` | Fuzzy search users |
| `:info` | Show client information |
| `:emoji [option]` | Browse emoji list [option: page number] |
| `:delay [msg] [delayTime]` | Send a delayed message |
| `:` | More commands are under development... |

![](https://ws3.sinaimg.cn/large/006tNbRwly1fylh7bdlo6g30go01shdt.gif)

### Chat History Query

![](https://i.loli.net/2019/05/08/5cd1c310cb796.jpg)

Use the command `:q keyword` to search chat history related to you.

> Client chat history is stored in `/opt/logs/cim/` by default, so write permission is required for this directory. You can also customize the directory by adding `--cim.msg.logger.path=/custom/path` to the startup command.



### AI Mode

![](https://i.loli.net/2019/05/08/5cd1c30e47d95.jpg)

Use the command `:ai` to enable AI mode. After that, all messages will be responded to by `AI`.

Use `:qai` to exit AI mode.

### Prefix Match Username

![](https://i.loli.net/2019/05/08/5cd1c32ac3397.jpg)

Use the command `:qu prefix` to search user information by prefix.

> This feature is primarily designed for searching users in input fields on mobile clients.

### Group Chat/Private Chat

#### Group Chat

![](https://ws1.sinaimg.cn/large/006tNbRwly1fyli54e8e1j31t0056x11.jpg)
![](https://ws3.sinaimg.cn/large/006tNbRwly1fyli5yyspmj31im06atb8.jpg)
![](https://ws3.sinaimg.cn/large/006tNbRwly1fyli6sn3c8j31ss06qmzq.jpg)

For group chat, simply type a message in the console and press Enter to send. All online clients will receive the message.

#### Private Chat

To send a private message, you need to know the recipient's `userID`.

Use the command `:olu` to list all online users.

![](https://ws4.sinaimg.cn/large/006tNbRwly1fyli98mlf3j31ta06mwhv.jpg)

Then use the format `userId;;message content` to send a private message.

![](https://ws4.sinaimg.cn/large/006tNbRwly1fylib08qlnj31sk082zo6.jpg)
![](https://ws1.sinaimg.cn/large/006tNbRwly1fylibc13etj31wa0564lp.jpg)
![](https://ws3.sinaimg.cn/large/006tNbRwly1fylicmjj6cj31wg07c4qp.jpg)
![](https://ws1.sinaimg.cn/large/006tNbRwly1fylicwhe04j31ua03ejsv.jpg)

Meanwhile, the other account will not receive the message.
![](https://ws3.sinaimg.cn/large/006tNbRwly1fylie727jaj31t20dq1ky.jpg)



### Emoji Support

Use the command `:emoji 1` to list all available emojis. Use the emoji alias to send an emoji.

![](https://tva1.sinaimg.cn/large/006y8mN6ly1g6j910cqrzj30dn05qjw9.jpg)
![](https://tva1.sinaimg.cn/large/006y8mN6ly1g6j99hazg6j30ax03hq35.jpg)

### Delayed Messages

Send a message with a 10-second delay:

```shell
:delay delayMsg 10
```

![](pic/delay.gif)

## Contact

## Contributing

We welcome contributions! Before submitting a PR, please ensure your code passes the Checkstyle check.

### Code Style

This project uses [Checkstyle](https://checkstyle.org/) to enforce code style. The rules are defined in `checkstyle/checkstyle.xml`.

**Run Checkstyle locally before committing:**

```shell
mvn checkstyle:check
```

**Key rules:**
- Use spaces around `{`, `}`, and operators
- No trailing whitespace
- Files must end with a newline
- Remove unused imports
- Constants (`static final`) must be `UPPER_SNAKE_CASE`
- Use Java-style array declarations: `String[] args` (not `String args[]`)

**Skip Checkstyle for quick builds:**

```shell
mvn package -Dcheckstyle.skip=true
```

- [crossoverJie@gmail.com](mailto:crossoverJie@gmail.com)
