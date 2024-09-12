package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import com.crossoverjie.cim.common.data.construct.SortArrayMap;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.*;

public class SortArrayMapConsistentHashTest {

    @Test
    public void getFirstNodeValue() {
        AbstractConsistentHash map = new SortArrayMapConsistentHash() ;

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
        AbstractConsistentHash map = new SortArrayMapConsistentHash() ;

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
        AbstractConsistentHash map = new SortArrayMapConsistentHash() ;

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
        AbstractConsistentHash map = new SortArrayMapConsistentHash() ;

        List<String> strings = new ArrayList<>();
        strings.add("45.78.28.220:9000:8081") ;
        strings.add("45.78.28.220:9100:9081") ;


        String PROCESS = map.process(strings,"1551253899106");
        for (int i = 0; i < 100; i++) {
            String process = map.process(strings, "1551253899106");
            Assert.assertEquals(PROCESS, process);
        }
    }
    @Test
    public void getFirstNodeValue5() {
        AbstractConsistentHash map = new SortArrayMapConsistentHash() ;

        List<String> strings = new ArrayList<>();
        strings.add("45.78.28.220:9000:8081") ;
        strings.add("45.78.28.220:9100:9081") ;
        strings.add("45.78.28.220:9100:10081") ;

        String PROCESS = map.process(strings,"1551253899106");
        for (int i = 0; i < 100; i++) {
            String process = map.process(strings, "1551253899106");
            Assert.assertEquals(PROCESS, process);
        }
    }

    @Test
    public void getFirstNodeValue6() {
        AbstractConsistentHash map = new SortArrayMapConsistentHash() ;

        List<String> strings = new ArrayList<>();
        strings.add("45.78.28.220:9000:8081") ;
        strings.add("45.78.28.220:9100:9081") ;
        strings.add("45.78.28.220:9100:10081") ;

        String PROCESS = map.process(strings,"1551253899106");
        for (int i = 0; i < 100; i++) {
            String process = map.process(strings, "1551253899106");
            Assert.assertEquals(PROCESS, process);
        }
    }
    @Test
    public void getFirstNodeValue7() {
        AbstractConsistentHash map = new SortArrayMapConsistentHash() ;

        List<String> strings = new ArrayList<>();
        strings.add("45.78.28.220:9000:8081") ;
        strings.add("45.78.28.220:9100:9081") ;
        strings.add("45.78.28.220:9100:10081") ;
        strings.add("45.78.28.220:9100:00081") ;

        String PROCESS = map.process(strings,"1551253899106");
        for (int i = 0; i < 100; i++) {
            String process = map.process(strings, "1551253899106");
            Assert.assertEquals(PROCESS, process);
        }
    }

    @Test
    public void getFirstNodeValue8() {
        AbstractConsistentHash map = new SortArrayMapConsistentHash() ;

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
        SortArrayMapConsistentHash map = new SortArrayMapConsistentHash();

        List<String> strings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            strings.add("127.0.0." + i);
        }

        String process = map.process(strings,"zhangsan");

        SortArrayMap sortArrayMap = map.getSortArrayMap();
        int virtualNodeSize = 2;

        System.out.println("sortArrayMapSize = " + sortArrayMap.size() + "\n" + "virtualNodeSize = " + virtualNodeSize);
        Assert.assertEquals(sortArrayMap.size(), (virtualNodeSize + 1) * 10);
    }

}