package com.crossoverjie.cim.client.scanner;

import com.crossoverjie.cim.client.client.CIMClient;
import com.crossoverjie.cim.client.config.AppConfiguration;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.util.SpringBeanFactory;
import com.crossoverjie.cim.client.vo.req.GoogleProtocolVO;
import com.crossoverjie.cim.client.vo.req.GroupReqVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Scanner;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/21 16:44
 * @since JDK 1.8
 */
public class Scan implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(Scan.class);

    private CIMClient heartbeatClient;

    private RouteRequest routeRequest;

    private AppConfiguration configuration;

    public Scan() {
        this.configuration = SpringBeanFactory.getBean(AppConfiguration.class);
        this.heartbeatClient = SpringBeanFactory.getBean(CIMClient.class);
        this.routeRequest = SpringBeanFactory.getBean(RouteRequest.class);
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        String[] totalMsg;
        GoogleProtocolVO vo;
        while (true) {
            String msg = sc.nextLine();

            //单聊
            totalMsg = msg.split("><");
            if (totalMsg.length > 1) {
                vo = new GoogleProtocolVO();
                vo.setRequestId(Integer.parseInt(totalMsg[0]));
                vo.setMsg(totalMsg[1]);
                heartbeatClient.sendGoogleProtocolMsg(vo);
            } else {
                //群聊
                try {
                    GroupReqVO groupReqVO = new GroupReqVO(configuration.getUserId(), msg);
                    routeRequest.sendGroupMsg(groupReqVO);
                } catch (Exception e) {
                    LOGGER.error("Exception", e);
                }
            }


            LOGGER.info("{}:【{}】", configuration.getUserName(), msg);
        }
    }
}
