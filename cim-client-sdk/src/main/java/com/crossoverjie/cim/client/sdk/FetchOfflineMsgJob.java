package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.client.sdk.impl.ClientConfigurationData;
import com.crossoverjie.cim.common.data.construct.RingBufferWheel;

public class FetchOfflineMsgJob extends RingBufferWheel.Task{
    private static final int INITIAL_DELAY_SECONDS = 5;

    private RouteManager routeManager;
    private ClientConfigurationData conf;

    public FetchOfflineMsgJob(RouteManager routeManager, ClientConfigurationData conf) {
        this.routeManager = routeManager;
        this.conf = conf;
        setKey(INITIAL_DELAY_SECONDS); //It will be sent with a 5-second delay
    }

    @Override
    public void run() {
        routeManager.fetchOfflineMsgs(conf.getAuth().getUserId());
    }
}
