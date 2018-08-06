package com.crossoverjie.netty.action.common.protocol;

import com.google.protobuf.InvalidProtocolBufferException;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/8/1 12:24
 * @since JDK 1.8
 */
public class ProtocolUtil {

    public static void main(String[] args) throws InvalidProtocolBufferException {
        BaseRequestProto.RequestProtocol protocol = BaseRequestProto.RequestProtocol.newBuilder()
                .setRequestId(123)
                .setReqMsg("你好啊")
                .build();

        byte[] encode = encode(protocol);

        BaseRequestProto.RequestProtocol parseFrom = decode(encode);

        System.out.println(protocol.toString());
        System.out.println(protocol.toString().equals(parseFrom.toString()));
    }

    /**
     * 编码
     * @param protocol
     * @return
     */
    public static byte[] encode(BaseRequestProto.RequestProtocol protocol){
        return protocol.toByteArray() ;
    }

    /**
     * 解码
     * @param bytes
     * @return
     * @throws InvalidProtocolBufferException
     */
    public static BaseRequestProto.RequestProtocol decode(byte[] bytes) throws InvalidProtocolBufferException {
        return BaseRequestProto.RequestProtocol.parseFrom(bytes);
    }
}
