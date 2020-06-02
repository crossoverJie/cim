package com.crossoverjie.cim.server.api.vo.res;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020-06-03 00:16
 * @since JDK 1.8
 */
public class OnlineUserCount {
    private int count ;

    public OnlineUserCount(int count) {
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
