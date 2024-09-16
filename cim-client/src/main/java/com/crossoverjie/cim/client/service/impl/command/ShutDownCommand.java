package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.sdk.Client;
import com.crossoverjie.cim.client.sdk.Event;
import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.client.service.MsgLogger;
import com.crossoverjie.cim.client.service.ShutDownSign;
import com.crossoverjie.cim.common.data.construct.RingBufferWheel;
import jakarta.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-27 19:28
 * @since JDK 1.8
 */
@Slf4j
@Service
@ConditionalOnWebApplication
public class ShutDownCommand implements InnerCommand {

    @Resource
    private Client cimClient;

    @Resource
    private MsgLogger msgLogger;

    @Resource(name = "callBackThreadPool")
    private ThreadPoolExecutor callBackExecutor;

    @Resource
    private Event event;

    @Resource
    private ShutDownSign shutDownSign;

    @Resource
    private RingBufferWheel ringBufferWheel ;

    @Override
    public void process(String msg) throws Exception {
        event.info("cim client closing...");
        cimClient.close();
        shutDownSign.shutdown();
        msgLogger.stop();
        callBackExecutor.shutdown();
        ringBufferWheel.stop(false);
        try {
            while (!callBackExecutor.awaitTermination(1, TimeUnit.SECONDS)) {
                event.info("thread pool closing");
            }
        } catch (Exception e) {
            log.error("exception", e);
        }
        event.info("cim close success!");
        System.exit(0);
    }
}
