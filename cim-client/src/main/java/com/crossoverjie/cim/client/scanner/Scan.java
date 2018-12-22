package com.crossoverjie.cim.client.scanner;

import com.crossoverjie.cim.client.client.CIMClient;
import com.crossoverjie.cim.client.vo.req.GoogleProtocolVO;
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
public class Scan implements Runnable{

    private final static Logger LOGGER = LoggerFactory.getLogger(Scan.class);

    private CIMClient heartbeatClient ;

    public Scan(CIMClient heartbeatClient) {
        this.heartbeatClient = heartbeatClient;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        String[] totalMsg ;
        GoogleProtocolVO vo ;
        while (true){
            String msg = sc.nextLine() ;

            totalMsg = msg.split(" ");

            vo = new GoogleProtocolVO() ;
            vo.setRequestId(Integer.parseInt(totalMsg[0]));
            vo.setMsg(totalMsg[1]);
            heartbeatClient.sendGoogleProtocolMsg(vo) ;
            LOGGER.info("scan =[{}]",msg);
        }
    }
}
