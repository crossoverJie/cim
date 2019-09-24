package com.crossoverjie.cim.common;

import org.junit.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-09-23 14:21
 * @since JDK 1.8
 */
public class CommonTest {


    @Test
    public void test2(){
        System.out.println(LocalDate.now().toString());
        System.out.println(LocalTime.now().withNano(0).toString());
    }

    @Test
    public void test() throws InterruptedException {


        System.out.println(is2(9));

        System.out.println(Integer.bitCount(64-1));

        int target = 1569312600 ;
        int mod = 64 ;
        System.out.println(target % mod);

        System.out.println(mod(target,mod));
        System.out.println("============");

        System.out.println(cycleNum(256,64)) ;

        cycle();
    }


    private int mod(int target, int mod){
        // equals target % mod
        return target & (mod -1) ;
    }

    private int cycleNum(int target,int mod){
        //equals target/mod
        return target >> Integer.bitCount(mod-1) ;
    }

    private boolean is2(int target){
        if (target < 0){
            return false ;
        }

        int value = target & (target - 1) ;
        if (value != 0){
            return false ;
        }

        return true ;
    }


    private void cycle() throws InterruptedException {
        int index = 0  ;
        while (true){
            System.out.println("=======" + index);

            if (++index > 63){
                index = 0 ;
            }
            TimeUnit.MILLISECONDS.sleep(200);
        }
    }

}
