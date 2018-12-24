#!/usr/bin/env bash

git pull

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

sh route-startup.sh

echo "启动路由成功！"