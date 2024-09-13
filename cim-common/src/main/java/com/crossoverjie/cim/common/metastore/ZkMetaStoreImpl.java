package com.crossoverjie.cim.common.metastore;

import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.common.util.RouteInfoParseUtil;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;

/**
 * @author crossovreJie
 */
@Slf4j
public class ZkMetaStoreImpl implements MetaStore {
    public static final String ROOT = "/cim";

    private ZkClient client;

    LoadingCache<String, String> cache;

    @Override
    public void initialize(AbstractConfiguration<?> configuration) throws Exception {
        // TODO: 2024/8/19 Change to set or caffeine?
        cache = CacheBuilder.newBuilder()
                .concurrencyLevel(3)
                .build(new CacheLoader<>() {
                    @Override
                    public String load(String s) {
                        return null;
                    }
                });
        client = new ZkClient(configuration.getMetaServiceUri(), configuration.getTimeoutMs());
    }

    @Override
    public Set<String> getAvailableServerList() throws Exception {
        if (cache.size() > 0) {
            return cache.asMap().keySet();
        }
        List<String> coll = client.getChildren(ROOT);
        Map<String, String> voidMap = coll.stream().collect(Collectors.toMap(
                Function.identity(),
                Function.identity()
        ));
        cache.putAll(voidMap);
        return voidMap.keySet();
    }

    @Override
    public void addServer(String ip, int cimServerPort, int httpPort) throws Exception {
        boolean exists = client.exists(ROOT);
        if (!exists) {
            client.createPersistent(ROOT);
        }
        String zkParse = RouteInfoParseUtil.parse(RouteInfo.builder()
                .ip(ip)
                .cimServerPort(cimServerPort)
                .httpPort(httpPort)
                .build());
        String serverPath = String.format("%s/%s", ROOT, zkParse);
        client.createEphemeral(serverPath);
        log.info("Add server to zk [{}]", serverPath);
    }

    @Override
    public void listenServerList(ChildListener childListener) throws Exception {
        client.subscribeChildChanges(ROOT, (parentPath, currentChildren) -> {
            log.info("Clear and update local cache parentPath=[{}],current server list=[{}]", parentPath, currentChildren.toString());
            childListener.childChanged(parentPath, currentChildren);

            // TODO: 2024/8/19 maybe can reuse currentChildren.
            // Because rebuildCache() will re-fetch the server list from zk.
            rebuildCache();
        });
    }

    @Override
    public synchronized void rebuildCache() throws Exception {
        cache.invalidateAll();

        // Because of calling invalidateAll, this method will re-fetch the server list from zk.
        this.getAvailableServerList();

    }


    private List<String> watchedGetChildren(CuratorFramework client, String path) throws Exception {
        /**
         * Get children and set a watcher on the node. The watcher notification will come through the
         * CuratorListener (see setDataAsync() above).
         */
        return client.getChildren().watched().forPath(path);
    }

    private void createEphemeral(CuratorFramework client, String path, byte[] payload) throws Exception {
        // this will create the given EPHEMERAL ZNode with the given data
        client.create().withMode(CreateMode.EPHEMERAL).forPath(path, payload);
    }

    private void create(CuratorFramework client, String path, byte[] payload) throws Exception {
        // this will create the given ZNode with the given data
        client.create().forPath(path, payload);
    }

    private void watchedGetChildren(CuratorFramework client, String path, Watcher watcher)
            throws Exception {
        // Get children and set the given watcher on the node.
        client.getChildren().usingWatcher(watcher).forPath(path);
    }
}
