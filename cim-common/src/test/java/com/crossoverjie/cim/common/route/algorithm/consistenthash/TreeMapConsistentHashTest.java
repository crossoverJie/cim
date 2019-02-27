package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TreeMapConsistentHashTest {

    @Test
    public void getFirstNodeValue() {
        AbstractConsistentHash map = new TreeMapConsistentHash() ;

        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i) ;
        }
        String process = map.process(strings,"zhangsan");
        System.out.println(process);
        Assert.assertEquals("127.0.0.2",process);
    }



    @Test
    public void getFirstNodeValue2() {
        AbstractConsistentHash map = new TreeMapConsistentHash() ;

        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i) ;
        }
        String process = map.process(strings,"zhangsan2");
        System.out.println(process);

        Assert.assertEquals("127.0.0.3",process);
    }


    @Test
    public void getFirstNodeValue3() {
        AbstractConsistentHash map = new TreeMapConsistentHash() ;

        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i) ;
        }
        String process = map.process(strings,"1551253899106");

        System.out.println(process);
        Assert.assertEquals("127.0.0.6",process);
    }
}