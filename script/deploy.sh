#!/usr/bin/env bash

git pull

cd ..

mvn -Dmaven.test.skip=true clean package

# 分发路由
mv /root/work/netty-action/cim-forward-route/target/cim-forward-route-1.0.0-SNAPSHOT.jar /root/work/route/

appname="route" ;
PID=$(ps -ef | grep $appname | grep -v grep | awk '{print $2}')

# 遍历杀掉 pid
for var in ${PID[@]};
do
    echo "loop pid= $var"
    kill -9 $var
done

echo "开始部署路由。。。。"

sh /root/work/route/route-startup.sh

echo "部署路由成功！"

#=======================================
# 部署server
mv /root/work/netty-action/cim-server/target/cim-server-1.0.0-SNAPSHOT.jar /root/work/server/

appname="cim-server" ;
PID=$(ps -ef | grep $appname | grep -v grep | awk '{print $2}')

# 遍历杀掉 pid
for var in ${PID[@]};
do
    echo "loop pid= $var"
    kill -9 $var
done

echo "开始部署服务。。。。"
sh /root/work/server/server-startup.sh
echo "部署服务成功！"


