#!/usr/bin/env bash
nohup java -jar  /root/work/server/cim-server-1.0.0-SNAPSHOT.jar --cim.server.port=9000  > /root/work/server/log.file 2>&1 &