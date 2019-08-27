package com.crossoverjie.cim.client.service.impl;

import com.crossoverjie.cim.client.config.AppConfiguration;
import com.crossoverjie.cim.client.service.EchoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-08-27 22:37
 * @since JDK 1.8
 */
@Service
public class EchoServiceImpl implements EchoService {

    private static final String PREFIX = "$";

    @Autowired
    private AppConfiguration appConfiguration;

    @Override
    public void echo(String msg,String... replace) {
        msg = "\033[31;4m" + appConfiguration.getUserName() + PREFIX + "\033[0m" + " " + msg;

        for (String str : replace) {
//            msg.replaceAll("{}",str) ;
        }
        System.out.println(msg);
    }
}
