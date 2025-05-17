package com.crossoverjie.cim.server.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class SafeRunnable implements Runnable{
    @Override
    public void run() {
        try {
            run0();
        } catch (Exception e) {
            log.error("[SafeRunnable] run failed", e);
        }
    }

    protected abstract void run0();
}
