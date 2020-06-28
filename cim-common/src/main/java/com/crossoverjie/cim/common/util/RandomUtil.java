package com.crossoverjie.cim.common.util;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 17:10
 * @since JDK 1.8
 */
public class RandomUtil {

    public static int getRandom() {

        double random = Math.random();
        return (int) (random * 10000000);
    }
}
