package com.crossoverjie.cim.client.service.impl;

import com.crossoverjie.cim.client.config.AppConfiguration;
import com.crossoverjie.cim.client.service.EchoService;
import com.crossoverjie.cim.client.service.RouteRequest;
import com.crossoverjie.cim.client.thread.ContextHolder;
import com.crossoverjie.cim.common.core.proxy.RpcProxyManager;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.common.res.NULLBody;
import com.crossoverjie.cim.route.api.RouteApi;
import com.crossoverjie.cim.route.api.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.api.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2018/12/22 22:27
 * @since JDK 1.8
 */
@Slf4j
@Service
public class RouteRequestImpl implements RouteRequest {

    @Autowired
    private OkHttpClient okHttpClient;

    @Value("${cim.route.url}")
    private String routeUrl;

    @Autowired
    private EchoService echoService;


    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public void sendGroupMsg(ChatReqVO chatReqVO) throws Exception {
        RouteApi routeApi = RpcProxyManager.create(RouteApi.class, routeUrl, okHttpClient);
        routeApi.groupRoute(chatReqVO);
    }

    @Override
    public void sendP2PMsg(P2PReqVO p2PReqVO) throws Exception {
        RouteApi routeApi = RpcProxyManager.create(RouteApi.class, routeUrl, okHttpClient);
        BaseResponse<NULLBody> response = routeApi.p2pRoute(p2PReqVO);
        // account offline.
        if (response.getCode().equals(StatusEnum.OFF_LINE.getCode())) {
            log.error(p2PReqVO.getReceiveUserId() + ":" + StatusEnum.OFF_LINE.getMessage());
        }
    }

    @Override
    public CIMServerResVO getCIMServer(LoginReqVO loginReqVO) throws Exception {

        RouteApi routeApi = RpcProxyManager.create(RouteApi.class, routeUrl, okHttpClient);
        BaseResponse<CIMServerResVO> cimServerResVO = routeApi.login(loginReqVO);

        // repeat fail
        if (!cimServerResVO.getCode().equals(StatusEnum.SUCCESS.getCode())) {
            echoService.echo(cimServerResVO.getMessage());

            // when client in reConnect state, could not exit.
            if (ContextHolder.getReconnect()) {
                echoService.echo("###{}###", StatusEnum.RECONNECT_FAIL.getMessage());
                throw new CIMException(StatusEnum.RECONNECT_FAIL);
            }

            System.exit(-1);
        }


        return cimServerResVO.getDataBody();
    }

    @Override
    public Set<CIMUserInfo> onlineUsers() throws Exception {
        RouteApi routeApi = RpcProxyManager.create(RouteApi.class, routeUrl, okHttpClient);
        BaseResponse<Set<CIMUserInfo>> onlineUsersResVO = routeApi.onlineUser();
        return onlineUsersResVO.getDataBody();
    }

    @Override
    public void offLine() throws Exception {
        RouteApi routeApi = RpcProxyManager.create(RouteApi.class, routeUrl, okHttpClient);
        ChatReqVO vo = new ChatReqVO(appConfiguration.getUserId(), "offLine");
        routeApi.offLine(vo);
    }
}
