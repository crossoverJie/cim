package com.crossoverjie.cim.common.data.construct;

import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Function:根据 key 排序的 Map
 *
 * @author crossoverJie
 * Date: 2019-02-25 18:17
 * @since JDK 1.8
 */
public class SortArrayMap {

    private Node[] buckets;

    private static final int DEFAULT_SIZE = 10;
    private int size = 0;

    public SortArrayMap() {
        buckets = new Node[DEFAULT_SIZE];
    }

    public void add(int key, String value) {
        checkSize(size + 1);
        Node node = new Node(key, value);
        buckets[size++] = node;
    }

    private void checkSize(int size) {
        if (size >= buckets.length) {
            //扩容自身的 3/2
            int oldLen = buckets.length;
            int newLen = oldLen + (oldLen >> 1);
            buckets = Arrays.copyOf(buckets, newLen);
        }
    }

    public void sort(){
        Arrays.sort(buckets, 0, size, new Comparator<Node>() {
            @Override
            public int compare(Node o1, Node o2) {
                if (o1.key > o2.key){
                    return 1 ;
                }else {
                    return -1;
                }
            }
        });
    }

    public void print() {
        for (Node bucket : buckets) {
            if (bucket == null){
                continue;
            }
            System.out.println(bucket.toString());
        }
    }

    public int size() {
        return size;
    }

    /**
     * 数据节点
     */
    private class Node {
        public int key;
        public String value;

        public Node(int key, String value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public String toString() {
            return "Node{" +
                    "key=" + key +
                    ", value='" + value + '\'' +
                    '}';
        }

    }


    public static void main(String[] args) {
        TreeMap<Integer, String> addressRing = new TreeMap<Integer, String>();
        for (int i = 0; i < 10; i++) {
            addressRing.put(i, "192.168.1." + i);
        }
        Integer jobHash = 0;
        SortedMap<Integer, String> lastRing = addressRing.tailMap(jobHash);
        if (!lastRing.isEmpty()) {
            System.out.println("result= " + lastRing.get(lastRing.firstKey()));
        } else {
            System.out.println("result=" + addressRing.firstEntry().getValue());
        }


    }
}
