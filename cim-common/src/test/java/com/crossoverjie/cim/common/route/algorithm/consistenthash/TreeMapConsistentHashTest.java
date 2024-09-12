package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.*;

public class TreeMapConsistentHashTest {



    @Test
    public void getFirstNodeValue() {
        AbstractConsistentHash map = new TreeMapConsistentHash() ;

        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i) ;
        }
        String PROCESS = map.process(strings, "zhangsan");
        for (int i = 0; i < 100; i++) {
            String process = map.process(strings, "zhangsan");
            Assert.assertEquals(PROCESS, process);
        }
    }



    @Test
    public void getFirstNodeValue2() {
        AbstractConsistentHash map = new TreeMapConsistentHash() ;

        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i) ;
        }
        String PROCESS = map.process(strings,"zhangsan2");
        for (int i = 0; i < 100; i++) {
            String process = map.process(strings, "zhangsan2");
            Assert.assertEquals(PROCESS, process);
        }
    }


    @Test
    public void getFirstNodeValue3() {
        AbstractConsistentHash map = new TreeMapConsistentHash() ;

        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i) ;
        }
        String PROCESS = map.process(strings,"1551253899106");
        for (int i = 0; i < 100; i++) {
            String process = map.process(strings, "1551253899106");
            Assert.assertEquals(PROCESS, process);
        }
    }

    @Test
    public void getFirstNodeValue4() {
        AbstractConsistentHash map = new TreeMapConsistentHash() ;

        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i);
        }
        Set<String> processes = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            String process = map.process(strings,"zhangsan" + i);
            processes.add(process);
        }
        RangeCheckTestUtil.assertInRange(processes.size(), 2, 10);
    }

    @Test
    public void testVirtualNode() throws NoSuchFieldException, IllegalAccessException {
        TreeMapConsistentHash map = new TreeMapConsistentHash();

        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i);
        }

        String process = map.process(strings,"zhangsan");

        TreeMap treeMap = map.getTreeMap();
        int virtualNodeSize = 2;

        System.out.println("treeMapSize = " + treeMap.size() + "\n" + "virtualNodeSize = " + virtualNodeSize);
        Assert.assertEquals(treeMap.size(), (virtualNodeSize + 1) * 10);
    }
}