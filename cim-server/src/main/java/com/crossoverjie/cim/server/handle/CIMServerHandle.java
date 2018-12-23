package com.crossoverjie.cim.server.handle;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.protocol.CIMRequestProto;
import com.crossoverjie.cim.server.util.SessionSocketHolder;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


    /**
     * 取消绑定
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String userName = SessionSocketHolder.getUserName((NioSocketChannel) ctx.channel());
        LOGGER.info("用户[{}]断开",userName);
        SessionSocketHolder.remove((NioSocketChannel) ctx.channel());
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CIMRequestProto.CIMReqProtocol msg) throws Exception {
        LOGGER.info("收到msg={}", msg.toString());

        if (msg.getType() == Constants.CommandType.LOGIN){
            //保存客户端与 Channel 之间的关系
            SessionSocketHolder.put(msg.getRequestId(),(NioSocketChannel)ctx.channel()) ;
            SessionSocketHolder.saveSession(msg.getRequestId(),msg.getReqMsg());
            LOGGER.info("客户端[{}]注册成功",msg.getReqMsg());
        }

    }
}
