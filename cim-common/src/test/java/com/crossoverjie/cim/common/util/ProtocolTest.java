package com.crossoverjie.cim.common.util;

import com.crossoverjie.cim.common.protocol.BaseCommand;
import com.crossoverjie.cim.common.protocol.Request;
import com.google.protobuf.InvalidProtocolBufferException;
import org.junit.Test;

public class ProtocolTest {

    @Test
    public void testProtocol() throws InvalidProtocolBufferException {
        Request protocol = Request.newBuilder()
                .setRequestId(123L)
                .setReqMsg("你好啊")
                .setCmd(BaseCommand.LOGIN_REQUEST)
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
