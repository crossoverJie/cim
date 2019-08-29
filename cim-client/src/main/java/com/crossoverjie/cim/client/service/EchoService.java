package com.crossoverjie.cim.client.service;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-08-27 22:35
 * @since JDK 1.8
 */
public interface EchoService {

    /**
     * echo msg to terminal
     * @param msg message
     * @param replace
     */
    void echo(String msg, Object... replace) ;
}
