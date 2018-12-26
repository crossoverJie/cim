package com.crossoverjie.cim.client.scanner;

import com.crossoverjie.cim.client.config.AppConfiguration;
import com.crossoverjie.cim.client.service.MsgHandle;
import com.crossoverjie.cim.client.util.SpringBeanFactory;
import com.crossoverjie.cim.client.vo.req.GroupReqVO;
import com.crossoverjie.cim.client.vo.req.P2PReqVO;
import com.crossoverjie.cim.common.enums.SystemCommandEnumType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
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

    /**
     * 系统参数
     */
    private AppConfiguration configuration;

    private MsgHandle msgHandle ;

    public Scan() {
        this.configuration = SpringBeanFactory.getBean(AppConfiguration.class);
        this.msgHandle = SpringBeanFactory.getBean(MsgHandle.class) ;
    }

    @Override
    public void run() {
        Scanner sc = new Scanner(System.in);
        String[] totalMsg;
        while (true) {
            String msg = sc.nextLine();

            //检查消息
            if (msgHandle.checkMsg(msg)) {
                continue;
            }

            //系统内置命令
            if (msgHandle.innerCommand(msg)){
                continue;
            }


            //单聊
            totalMsg = msg.split("><");
            if (totalMsg.length > 1) {
                //私聊
                p2pChat(totalMsg);
            } else {
                //群聊
                groupChat(msg);
            }


            LOGGER.info("{}:【{}】", configuration.getUserName(), msg);
        }
    }

    /**
     * 处理内置函数
     */
    private void innerCommand(String msg) {
        Map<String, String> allStatusCode = SystemCommandEnumType.getAllStatusCode();

        if (SystemCommandEnumType.QUIT.getCommandType().equals(msg)){
            LOGGER.info("系统关闭中。。。。");
            System.exit(0);
        }else {
            LOGGER.warn("====================================");
            for (Map.Entry<String, String> stringStringEntry : allStatusCode.entrySet()) {
                String key = stringStringEntry.getKey();
                String value = stringStringEntry.getValue();
                LOGGER.warn(key + "----->" + value);
            }
            LOGGER.warn("====================================");
        }

        return;
    }

    private void p2pChat(String[] totalMsg) {
        P2PReqVO p2PReqVO = new P2PReqVO();
        p2PReqVO.setUserId(configuration.getUserId());
        p2PReqVO.setReceiveUserId(Long.parseLong(totalMsg[0]));
        p2PReqVO.setMsg(totalMsg[1]);
        try {
            msgHandle.p2pChat(p2PReqVO);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
    }

    private void groupChat(String msg) {
        GroupReqVO groupReqVO = new GroupReqVO(configuration.getUserId(), msg);
        try {
            msgHandle.groupChat(groupReqVO);
        } catch (Exception e) {
            LOGGER.error("Exception", e);
        }
    }

}
