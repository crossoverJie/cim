package com.crossoverjie.cim.client.service.impl.command;

import com.crossoverjie.cim.client.client.CIMClient;
import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.client.service.MsgLogger;
import com.crossoverjie.cim.client.service.RouteRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-27 19:28
 * @since JDK 1.8
 */
@Service
public class ShutDownCommand implements InnerCommand {
    private final static Logger LOGGER = LoggerFactory.getLogger(ShutDownCommand.class);

    @Autowired
    private RouteRequest routeRequest ;

    @Autowired
    private CIMClient cimClient;

    @Autowired
    private MsgLogger msgLogger;

    @Resource(name = "callBackThreadPool")
    private ThreadPoolExecutor executor;

    @Override
    public void process(String msg) {
        LOGGER.info("系统关闭中。。。。");
        routeRequest.offLine();
        msgLogger.stop();
        executor.shutdown();
        try {
            while (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                LOGGER.info("线程池关闭中。。。。");
            }
            cimClient.close();
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException", e);
        }
        System.exit(0);
    }
}
