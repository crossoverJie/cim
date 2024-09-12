package com.crossoverjie.cim.client.sdk;

import java.util.concurrent.atomic.AtomicReference;

public abstract class ClientState {

    private static final AtomicReference<State> STATE = new AtomicReference<>(State.Initialized);

    public enum State {
        Initialized, // Client is created
        Connecting, // Client connecting to server
        Ready,
        Closed,
        Failed,
    };

    public void setState(State s) {
        STATE.set(s);
    }

    public State getState() {
        return STATE.get();
    }
}
