package com.crossoverjie.cim.common.protocol;

import com.google.protobuf.InvalidProtocolBufferException;
import com.crossoverjie.cim.common.protocol.Request;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 2018/8/1 12:24
 * @since JDK 1.8
 */
public class ProtocolUtil {

    public static void main(String[] args) throws InvalidProtocolBufferException {
        Request protocol = Request.newBuilder()
                .setRequestId(123L)
                .setReqMsg("你好啊")
                .setType(1)
                .build();

        byte[] encode = encode(protocol);

        Request parseFrom = decode(encode);

        System.out.println(protocol);
        System.out.println(protocol.toString().equals(parseFrom.toString()));
    }

    /**
     * 编码
     * @param protocol
     * @return
     */
    public static byte[] encode(Request protocol){
        return protocol.toByteArray() ;
    }

    /**
     * 解码
     * @param bytes
     * @return
     * @throws InvalidProtocolBufferException
     */
    public static Request decode(byte[] bytes) throws InvalidProtocolBufferException {
        return Request.parseFrom(bytes);
    }
}
