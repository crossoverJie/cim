package com.crossoverjie.cim.client.sdk;

import com.crossoverjie.cim.common.core.proxy.RpcProxyManager;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.route.api.RouteApi;
import com.crossoverjie.cim.route.api.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.api.vo.res.CIMServerResVO;
import okhttp3.OkHttpClient;

public class RouteLookup {


    private final RouteApi routeApi;
    private final Event event;

    public RouteLookup(String routeUrl, OkHttpClient okHttpClient, Event event) {
        routeApi = RpcProxyManager.create(RouteApi.class, routeUrl, okHttpClient);
        this.event = event;
    }

    public CIMServerResVO getServer(LoginReqVO loginReqVO) throws Exception {
        BaseResponse<CIMServerResVO> cimServerResVO = routeApi.login(loginReqVO);

        // repeat fail
        if (!cimServerResVO.getCode().equals(StatusEnum.SUCCESS.getCode())) {
            event.info(cimServerResVO.getMessage());

            // when client in reConnect state, could not exit.
            if (ConnectionState.getReConnect()) {
                event.warn("###{}###", StatusEnum.RECONNECT_FAIL.getMessage());
                throw new CIMException(StatusEnum.RECONNECT_FAIL);
            }
        }


        return cimServerResVO.getDataBody();
    }
}
