package com.crossoverjie.cim.common.metastore;


import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.SneakyThrows;
import org.I0Itec.zkclient.ZkClient;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;

public class MetaStoreTest {

    private static final String connectionString = "127.0.0.1:2181";

    // TODO: 2024/8/30 integration test
    @SneakyThrows
//    @Test
    public void testZk() {
        ExponentialBackoffRetry retryPolicy = new ExponentialBackoffRetry(1000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder()
                .connectString(connectionString)
                .retryPolicy(retryPolicy)
                .connectionTimeoutMs(5000)
                .sessionTimeoutMs(5000)
                .build();
        client.start();

        Stat stat = client.checkExists().forPath("/cim");
        if (stat == null) {
            create(client, "/cim", null);
        }


        List<String> list = null;
        list = curatorWatcherGetChildren(client, "/cim", watchedEvent -> {
            String name = Thread.currentThread().getName();
            System.out.println("watchedEvent = " + watchedEvent + " name = " + name);
//            try {
//                List<String> children = watchedGetChildren(client, "/cim");
//                System.out.println("children = " + children);
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
        });

        System.out.println(list);

//        createEphemeral(client, "/cim/route1", null);
//        createEphemeral(client, "/cim/route2", null);
        TimeUnit.SECONDS.sleep(1000);
    }

    public static void createEphemeral(CuratorFramework client, String path, byte[] payload) throws Exception {
        // this will create the given EPHEMERAL ZNode with the given data
        client.create().withMode(CreateMode.EPHEMERAL).forPath(path, payload);
    }

    public static void create(CuratorFramework client, String path, byte[] payload) throws Exception {
        // this will create the given ZNode with the given data
        client.create().forPath(path, payload);
    }


    public static List<String> watchedGetChildren(CuratorFramework client, String path) throws Exception {
        /**
         * Get children and set a watcher on the node. The watcher notification will come through the
         * CuratorListener (see setDataAsync() above).
         */
        return client.getChildren().watched().forPath(path);
    }

    public static List<String> watchedGetChildren(CuratorFramework client, String path, Watcher watcher)
            throws Exception {
        /**
         * Get children and set the given watcher on the node.
         */
        return client.getChildren().usingWatcher(watcher).forPath(path);
    }

    public static List<String> curatorWatcherGetChildren(CuratorFramework client, String path, CuratorWatcher watcher)
            throws Exception {
        /**
         * Get children and set the given watcher on the node.
         */
        return client.getChildren().usingWatcher(watcher).forPath(path);
    }


    @SneakyThrows
//    @Test
    public void zkClientTest(){
        ZkClient zkClient = new ZkClient(connectionString, 5000);
        zkClient.subscribeChildChanges("/cim", (parentPath, currentChildren) -> {
            System.out.println("parentPath = " + parentPath);
            System.out.println("currentChildren = " + currentChildren);
        });
        TimeUnit.SECONDS.sleep(1000);
    }
}