package com.crossoverjie.cim.client.service.impl;

import com.crossoverjie.cim.client.service.MsgHandle;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.vo.req.GroupReqVO;
import com.crossoverjie.cim.client.vo.req.P2PReqVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/12/26 11:15
 * @since JDK 1.8
 */
@Service
public class MsgHandler implements MsgHandle {

    @Autowired
    private RouteRequest routeRequest ;

    @Override
    public void groupChat(GroupReqVO groupReqVO) throws Exception {
        routeRequest.sendGroupMsg(groupReqVO);
    }

    @Override
    public void p2pChat(P2PReqVO p2PReqVO) throws Exception {

    }
}
