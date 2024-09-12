package com.crossoverjie.cim.client.sdk;

public class ConnectionState {
    private static volatile boolean reConnect = false;

    public static void setReConnect(boolean reConnect) {
        ConnectionState.reConnect = reConnect;
    }
    public static boolean getReConnect() {
        return ConnectionState.reConnect;
    }
}
