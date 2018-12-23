package com.crossoverjie.cim.server.test;


import com.alibaba.fastjson.JSON;
import com.crossoverjie.cim.client.vo.res.CIMServerResVO;
import org.junit.Test;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 18:44
 * @since JDK 1.8
 */
public class CommonTest {

    @Test
    public void test() {

        String json = "{\"code\":\"9000\",\"message\":\"成功\",\"reqNo\":null,\"dataBody\":{\"ip\":\"127.0.0.1\",\"port\":8081}}" ;

        CIMServerResVO cimServerResVO = JSON.parseObject(json, CIMServerResVO.class);

        System.out.println(cimServerResVO.toString());

        String text = "nihaoaaa" ;
        String[] split = text.split(" ");
        System.out.println(split.length);
    }
}
