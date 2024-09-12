package com.crossoverjie.cim.client.sdk;

import java.util.concurrent.atomic.AtomicReference;

public abstract class ClientState {

    private static final AtomicReference<State> STATE = new AtomicReference<>(State.Initialized);

    public enum State {
        /**
         * Client state
         */
        Initialized, Reconnecting, Ready, Closed, Failed
    }

    public void setState(State s) {
        STATE.set(s);
    }

    public State getState() {
        return STATE.get();
    }
}
