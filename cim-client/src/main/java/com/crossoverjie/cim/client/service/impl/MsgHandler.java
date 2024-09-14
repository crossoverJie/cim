package com.crossoverjie.cim.client.service.impl;

import com.crossoverjie.cim.client.sdk.Client;
import com.crossoverjie.cim.client.service.InnerCommand;
import com.crossoverjie.cim.client.service.InnerCommandContext;
import com.crossoverjie.cim.client.service.MsgHandle;
import com.crossoverjie.cim.common.util.StringUtil;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/26 11:15
 * @since JDK 1.8
 */
@Slf4j
@Service
public class MsgHandler implements MsgHandle {


    @Resource
    private InnerCommandContext innerCommandContext ;

    @Resource
    private Client client;

    private boolean aiModel = false;

    @Override
    public void sendMsg(String msg) throws Exception {
        if (aiModel) {
            aiChat(msg);
        } else {
            normalChat(msg);
        }
    }

    private void normalChat(String msg) throws Exception {
        String[] totalMsg = msg.split(";;");
        if (totalMsg.length > 1) {
            P2PReqVO p2PReqVO = new P2PReqVO();
            p2PReqVO.setReceiveUserId(Long.parseLong(totalMsg[0]));
            p2PReqVO.setMsg(totalMsg[1]);
            client.sendP2P(p2PReqVO);

        } else {
            client.sendGroup(msg);
        }
    }

    /**
     * AI model
     *
     * @param msg
     */
    private void aiChat(String msg) {
        msg = msg.replace("吗", "");
        msg = msg.replace("嘛", "");
        msg = msg.replace("?", "!");
        msg = msg.replace("？", "!");
        msg = msg.replace("你", "我");
        System.out.println("AI:\033[31;4m" + msg + "\033[0m");
    }

    @Override
    public boolean checkMsg(String msg) {
        if (StringUtil.isEmpty(msg)) {
            log.warn("不能发送空消息！");
            return true;
        }
        return false;
    }

    @Override
    public boolean innerCommand(String msg) throws Exception {

        if (msg.startsWith(":")) {

            InnerCommand instance = innerCommandContext.getInstance(msg);
            instance.process(msg) ;

            return true;

        } else {
            return false;
        }
    }

    @Override
    public void openAIModel() {
        aiModel = true;
    }

    @Override
    public void closeAIModel() {
        aiModel = false ;
    }

}
