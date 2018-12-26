package com.crossoverjie.cim.common.metastore;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author crossoverJie
 */
public interface MetaStore {

    void initialize(AbstractConfiguration<?> configuration) throws Exception;

    /**
     * Get available server list
     * @return available server list
     * @throws Exception exception
     */
    Set<String> getAvailableServerList() throws Exception;

    /**
     * Add server to meta store
     * @throws Exception exception
     */
    void addServer(String ip, int cimServerPort, int httpPort) throws Exception;

    /**
     * Subscribe server list
     * @param childListener child listener
     * @throws Exception exception
     */
    void listenServerList(ChildListener childListener) throws Exception;


    /**
     * @throws Exception
     */
    void rebuildCache() throws Exception;

    interface ChildListener {
        /**
         * Child changed
         * @param parentPath parent path(eg. for zookeeper: [/cim])
         * @param currentChildren current children
         * @throws Exception exception
         */
        void childChanged(String parentPath, List<String> currentChildren) throws Exception;
    }
}
