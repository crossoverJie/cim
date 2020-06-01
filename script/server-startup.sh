#!/usr/bin/env bash
nohup java -jar  /data/work/cim/server/cim-server-1.0.0-SNAPSHOT.jar --cim.server.port=9000  > /data/work/cim/server/log.file 2>&1 &