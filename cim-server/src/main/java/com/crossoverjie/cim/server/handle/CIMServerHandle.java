package com.crossoverjie.cim.server.handle;

import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.protocol.CIMRequestProto;
import com.crossoverjie.cim.server.config.AppConfiguration;
import com.crossoverjie.cim.server.util.SessionSocketHolder;
import com.crossoverjie.cim.server.util.SpringBeanFactory;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 17/05/2018 18:52
 * @since JDK 1.8
 */
@ChannelHandler.Sharable
public class CIMServerHandle extends SimpleChannelInboundHandler<CIMRequestProto.CIMReqProtocol> {

    private final static Logger LOGGER = LoggerFactory.getLogger(CIMServerHandle.class);

    private final MediaType mediaType = MediaType.parse("application/json");
    /**
     * 取消绑定
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        CIMUserInfo userInfo = SessionSocketHolder.getUserId((NioSocketChannel) ctx.channel());
        LOGGER.info("用户[{}]下线",userInfo.getUserName());
        SessionSocketHolder.remove((NioSocketChannel) ctx.channel());
        SessionSocketHolder.removeSession(userInfo.getUserId());

        //清除路由关系
        clearRouteInfo(userInfo);
    }

    /**
     * 清除路由关系
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
        }finally {
            response.body().close();
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CIMRequestProto.CIMReqProtocol msg) throws Exception {
        LOGGER.info("收到msg={}", msg.toString());

        if (msg.getType() == Constants.CommandType.LOGIN){
            //保存客户端与 Channel 之间的关系
            SessionSocketHolder.put(msg.getRequestId(),(NioSocketChannel)ctx.channel()) ;
            SessionSocketHolder.saveSession(msg.getRequestId(),msg.getReqMsg());
            LOGGER.info("客户端[{}]上线成功",msg.getReqMsg());
        }

    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (CIMException.isResetByPeer(cause.getMessage())){
            return;
        }

        LOGGER.error(cause.getMessage(), cause);

    }

}
