package com.crossoverjie.cim.service.impl;

import com.alibaba.fastjson.JSON;
import com.crossoverjie.cim.common.core.proxy.ProxyManager;
import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.util.HttpClient;
import com.crossoverjie.cim.route.api.RouteApi;
import com.crossoverjie.cim.service.RouteRequest;
import com.crossoverjie.cim.vo.req.LoginReqVO;
import com.crossoverjie.cim.vo.res.CIMServerResVO;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
@Service
public class RouteRequestImpl implements RouteRequest {

    private final static Logger LOGGER = LoggerFactory.getLogger(RouteRequestImpl.class);

    @Autowired
    private OkHttpClient okHttpClient;

    @Value("${cim.route.url}")
    private String routeUrl;



    @Override
    public CIMServerResVO.ServerInfo getCIMServer(LoginReqVO loginReqVO) throws Exception {

        RouteApi routeApi = new ProxyManager<>(RouteApi.class, routeUrl, okHttpClient).getInstance();
        com.crossoverjie.cim.route.api.vo.req.LoginReqVO vo = new com.crossoverjie.cim.route.api.vo.req.LoginReqVO();
        vo.setUserId(loginReqVO.getUserId());
        vo.setUserName(loginReqVO.getUserName());

        Response response = null;
        CIMServerResVO cimServerResVO = null;
        try {
            response = (Response) routeApi.login(vo);
            String json = response.body().string();
            cimServerResVO = JSON.parseObject(json, CIMServerResVO.class);

            //重复失败
            if (!cimServerResVO.getCode().equals(StatusEnum.SUCCESS.getCode())) {
                System.exit(-1);
            }

        } catch (Exception e) {
            LOGGER.error("exception", e);
        } finally {
            HttpClient.closeBody(response);
        }

        return cimServerResVO.getDataBody();
    }


}
