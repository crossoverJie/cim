package com.crossoverjie.cim.server.decorator;

import com.crossoverjie.cim.server.pojo.OfflineMsg;

import java.util.List;

/**
 * @author zhongcanyu
 * @date 2025/5/10
 * @description
 */
public abstract class StoreDecorator implements OfflineStore {
    protected final OfflineStore delegate;

    protected StoreDecorator(OfflineStore delegate) {
        this.delegate = delegate;
    }

    @Override
    public void save(OfflineMsg msg) {
        delegate.save(msg);
    }

    @Override
    public List<OfflineMsg> fetch(Long userId) {
        return delegate.fetch(userId);
    }

    public void markDelivered(Long userId, List<Long> messageIds) {
        delegate.markDelivered(userId, messageIds);
    }
}

