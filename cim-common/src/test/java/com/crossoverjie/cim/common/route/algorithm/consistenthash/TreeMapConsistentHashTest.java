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
        strings.add("zhangsan") ;
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i) ;
        }
        String process = map.process(strings);
        System.out.println(process);
        Assert.assertEquals("127.0.0.8",process);
    }



    @Test
    public void getFirstNodeValue2() {
        AbstractConsistentHash map = new TreeMapConsistentHash() ;

        List<String> strings = new ArrayList<String>();
        strings.add("zhangsan2");
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i) ;
        }
        String process = map.process(strings);
        System.out.println(process);

        Assert.assertEquals("127.0.0.4",process);
    }


    @Test
    public void getFirstNodeValue3() {
        AbstractConsistentHash map = new TreeMapConsistentHash() ;

        List<String> strings = new ArrayList<String>();
        strings.add("1551253899106") ;
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i) ;
        }
        String process = map.process(strings);

        System.out.println(process);
        Assert.assertEquals("127.0.0.6",process);
    }
}