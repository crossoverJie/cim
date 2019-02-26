package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class SortArrayMapConsistentHashTest {

    @Test
    public void getFirstNodeValue() {
        AbstractConsistentHash map = new SortArrayMapConsistentHash() ;

        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i) ;
        }
        map.process(strings);

        String firstNodeValue = map.getFirstNodeValue("zhangsan");
        System.out.println(firstNodeValue);
    }

    @Test
    public void getFirstNodeValue2() {
        AbstractConsistentHash map = new SortArrayMapConsistentHash() ;

        List<String> strings = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i) ;
        }
        map.process(strings);

        String firstNodeValue = map.getFirstNodeValue("zhangsan2");
        System.out.println(firstNodeValue);
    }
}