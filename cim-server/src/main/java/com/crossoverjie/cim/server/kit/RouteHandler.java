package com.crossoverjie.cim.server.kit;

import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.util.SessionSocketHolder;
import com.crossoverjie.cim.server.util.SpringBeanFactory;
import io.netty.channel.socket.nio.NioSocketChannel;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-01-20 17:20
 * @since JDK 1.8
 */
@Component
public class RouteHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(RouteHandler.class);


    private final MediaType mediaType = MediaType.parse("application/json");

    /**
     * 用户下线
     * @param userInfo
     * @param channel
     * @throws IOException
     */
    public void userOffLine(CIMUserInfo userInfo, NioSocketChannel channel) throws IOException {
        if (userInfo != null){
            LOGGER.info("用户[{}]下线", userInfo.getUserName());
            SessionSocketHolder.removeSession(userInfo.getUserId());
            //清除路由关系
            clearRouteInfo(userInfo);
        }
        SessionSocketHolder.remove(channel);

    }


    /**
     * 清除路由关系
     *
     * @param userInfo
     * @throws IOException
     */
    private void clearRouteInfo(CIMUserInfo userInfo) throws IOException {
        OkHttpClient okHttpClient = SpringBeanFactory.getBean(OkHttpClient.class);
        AppConfiguration configuration = SpringBeanFactory.getBean(AppConfiguration.class);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("userId", userInfo.getUserId());
        jsonObject.put("msg", "offLine");
        RequestBody requestBody = RequestBody.create(mediaType, jsonObject.toString());

        Request request = new Request.Builder()
                .url(configuration.getClearRouteUrl())
                .post(requestBody)
                .build();

        Response response = null;
        try {
            response = okHttpClient.newCall(request).execute();
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
        } finally {
            response.body().close();
        }
    }

}
