package com.crossoverjie.cim.thread;

import com.crossoverjie.cim.common.constant.Constants;
import com.crossoverjie.cim.common.protocol.CIMRequestProto;
import com.crossoverjie.cim.init.CIMClientHandleInitializer;
import com.crossoverjie.cim.vo.req.LoginReqVO;
import com.crossoverjie.cim.vo.res.CIMServerResVO;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020-06-14 22:35
 * @since JDK 1.8
 */
public class ConnectJob implements Runnable {

    private final static Logger LOGGER = LoggerFactory.getLogger(ConnectJob.class);

    private EventLoopGroup group = new NioEventLoopGroup(0, new DefaultThreadFactory("cim-work"));
    private SocketChannel channel;


    private LoginReqVO loginReqVO ;

    private CIMServerResVO.ServerInfo serverInfo;

    public ConnectJob(LoginReqVO loginReqVO, CIMServerResVO.ServerInfo serverInfo) {
        this.loginReqVO = loginReqVO;
        this.serverInfo = serverInfo ;
    }

    @Override
    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException", e);
        }

        startClient(this.serverInfo);

        loginCIMServer();
    }






    private void startClient(CIMServerResVO.ServerInfo cimServer) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new CIMClientHandleInitializer())
        ;

        ChannelFuture future = null;
        try {
            future = bootstrap.connect(cimServer.getIp(), cimServer.getCimServerPort()).sync();
        } catch (Exception e) {
            System.err.println(e);
            System.exit(-1);
        }

        channel = (SocketChannel) future.channel();
    }


    private void loginCIMServer() {
        CIMRequestProto.CIMReqProtocol login = CIMRequestProto.CIMReqProtocol.newBuilder()
                .setRequestId(loginReqVO.getUserId())
                .setReqMsg(loginReqVO.getUserName())
                .setType(Constants.CommandType.LOGIN)
                .build();
        ChannelFuture future = channel.writeAndFlush(login);
        future.addListener((ChannelFutureListener) channelFuture ->
                System.out.println("Registry cim server success!")
        );
    }
}
