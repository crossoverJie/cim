package com.crossoverjie.cim.server.util;



import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

public class NettyAttrUtilTest {

    @Test
    public void test() throws InterruptedException {
        long heartbeat = 2 * 1000 ;

        long now = System.currentTimeMillis();
        TimeUnit.SECONDS.sleep(1);

        long end = System.currentTimeMillis();

        if ((end - now) > heartbeat){
            System.out.println("超时");
        }else {
            System.out.println("没有超时");
        }
    }

}