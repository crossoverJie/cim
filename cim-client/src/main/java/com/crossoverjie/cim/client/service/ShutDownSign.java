package com.crossoverjie.cim.client.service;

import org.springframework.stereotype.Component;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-02-27 16:17
 * @since JDK 1.8
 */
@Component
public class ShutDownSign {
    private boolean isCommand ;

    /**
     * Set user exit sign.
     */
    public void shutdown(){
        isCommand = true ;
    }

    public boolean checkStatus(){
        return isCommand ;
    }
}
