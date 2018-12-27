package com.crossoverjie.cim.server.test;


import com.alibaba.fastjson.JSON;
import com.crossoverjie.cim.client.vo.res.CIMServerResVO;
import com.crossoverjie.cim.client.vo.res.OnlineUsersResVO;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 18:44
 * @since JDK 1.8
 */
public class CommonTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(CommonTest.class);
    @Test
    public void test() {

        String json = "{\"code\":\"9000\",\"message\":\"成功\",\"reqNo\":null,\"dataBody\":{\"ip\":\"127.0.0.1\",\"port\":8081}}" ;

        CIMServerResVO cimServerResVO = JSON.parseObject(json, CIMServerResVO.class);

        System.out.println(cimServerResVO.toString());

        String text = "nihaoaaa" ;
        String[] split = text.split(" ");
        System.out.println(split.length);
    }

    @Test
    public void onlineUser(){
        List<OnlineUsersResVO.DataBodyBean> onlineUsers = new ArrayList<>(64) ;

        OnlineUsersResVO.DataBodyBean bodyBean = new OnlineUsersResVO.DataBodyBean() ;

        bodyBean.setUserId(100L);
        bodyBean.setUserName("zhangsan");
        onlineUsers.add(bodyBean) ;

        bodyBean = new OnlineUsersResVO.DataBodyBean();
        bodyBean.setUserId(200L);
        bodyBean.setUserName("crossoverJie");
        onlineUsers.add(bodyBean) ;

        LOGGER.info("list={}",JSON.toJSONString(onlineUsers));

        LOGGER.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");

        for (OnlineUsersResVO.DataBodyBean onlineUser : onlineUsers) {

            LOGGER.info("userId={}=====userName={}",onlineUser.getUserId(),onlineUser.getUserName());
        }
        LOGGER.info("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }
}
