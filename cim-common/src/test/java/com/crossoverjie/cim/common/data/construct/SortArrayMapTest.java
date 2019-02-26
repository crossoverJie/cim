package com.crossoverjie.cim.common.data.construct;

import org.junit.Test;

public class SortArrayMapTest {

    @Test
    public void ad() {
        SortArrayMap map = new SortArrayMap() ;
        for (int i = 0; i < 9; i++) {
            map.add(i ,"127.0.0." + i);
        }
        map.print();
        System.out.println(map.size());
    }

    @Test
    public void add() {
        SortArrayMap map = new SortArrayMap() ;
        for (int i = 0; i < 10; i++) {
            map.add(i ,"127.0.0." + i);
        }
        map.print();
        System.out.println(map.size());
    }

    @Test
    public void add2() {
        SortArrayMap map = new SortArrayMap() ;
        for (int i = 0; i < 20; i++) {
            map.add(i ,"127.0.0." + i);
        }
        map.sort();
        map.print();
        System.out.println(map.size());
    }

    @Test
    public void add3() {
        SortArrayMap map = new SortArrayMap() ;

        map.add(100,"127.0.0.100");
        map.add(10,"127.0.0.10");
        map.add(8,"127.0.0.8");
        map.add(1000,"127.0.0.1000");

        map.print();
        System.out.println(map.size());
    }

    @Test
    public void firstNode() {
        SortArrayMap map = new SortArrayMap() ;

        map.add(100,"127.0.0.100");
        map.add(10,"127.0.0.10");
        map.add(8,"127.0.0.8");
        map.add(1000,"127.0.0.1000");

        map.sort();
        map.print();
        String value = map.firstNodeValue(101);
        System.out.println(value);
    }
    @Test
    public void firstNode2() {
        SortArrayMap map = new SortArrayMap() ;

        map.add(100,"127.0.0.100");
        map.add(10,"127.0.0.10");
        map.add(8,"127.0.0.8");
        map.add(1000,"127.0.0.1000");

        map.sort();
        map.print();
        String value = map.firstNodeValue(1);
        System.out.println(value);
    }
    @Test
    public void firstNode3() {
        SortArrayMap map = new SortArrayMap() ;

        map.add(100,"127.0.0.100");
        map.add(10,"127.0.0.10");
        map.add(8,"127.0.0.8");
        map.add(1000,"127.0.0.1000");

        map.sort();
        map.print();
        String value = map.firstNodeValue(1001);
        System.out.println(value);
    }
    @Test
    public void firstNode4() {
        SortArrayMap map = new SortArrayMap() ;

        map.add(100,"127.0.0.100");
        map.add(10,"127.0.0.10");
        map.add(8,"127.0.0.8");
        map.add(1000,"127.0.0.1000");

        map.sort();
        map.print();
        String value = map.firstNodeValue(9);
        System.out.println(value);
    }

    @Test
    public void add4() {
        SortArrayMap map = new SortArrayMap() ;

        map.add(100,"127.0.0.100");
        map.add(10,"127.0.0.10");
        map.add(8,"127.0.0.8");
        map.add(1000,"127.0.0.1000");

        map.sort();
        map.print();
        System.out.println(map.size());
    }

    int count = 1000000 ;
    @Test
    public void add5() {
        SortArrayMap map = new SortArrayMap() ;


        long star = System.currentTimeMillis() ;
        for (int i = 0; i < count; i++) {
            double d = Math.random();
            int ran = (int)(d*100);
            map.add(i + ran ,"127.0.0." + i);
        }
        map.sort();
        long end = System.currentTimeMillis() ;
        System.out.println("排序耗时 " + (end -star));
        System.out.println(map.size());



    }

    @Test
    public void add6(){

        SortArrayMap map = new SortArrayMap() ;
        long star = System.currentTimeMillis() ;
        for (int i = 0; i < count; i++) {
            double d = Math.random();
            int ran = (int)(d*100);
            map.add(i + ran ,"127.0.0." + i);
        }
        long end = System.currentTimeMillis() ;
        System.out.println("不排耗时 " + (end -star));
        System.out.println(map.size());
    }
}