package com.crossoverjie.cim.common.metastore;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author chenqwwq
 * @date 2025/6/8
 **/
public class NoMetaStoreImpl implements MetaStore {
    @Override
    public void initialize(AbstractConfiguration<?> configuration) throws Exception {

    }

    @Override
    public Set<String> getAvailableServerList() throws Exception {
        return Sets.newHashSet();
    }

    @Override
    public void addServer(String ip, int cimServerPort, int httpPort) throws Exception {

    }

    @Override
    public void listenServerList(ChildListener childListener) throws Exception {

    }

    @Override
    public void rebuildCache() throws Exception {

    }
}
