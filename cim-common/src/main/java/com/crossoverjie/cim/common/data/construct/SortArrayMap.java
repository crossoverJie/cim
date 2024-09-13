package com.crossoverjie.cim.common.data.construct;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.curator.shaded.com.google.common.collect.Sets;

/**
 * Function:根据 key 排序的 Map
 *
 * @author crossoverJie
 * Date: 2019-02-25 18:17
 * @since JDK 1.8
 */
public class SortArrayMap extends AbstractMap<String, String> {

    /**
     * 核心数组
     */
    private Node[] buckets;

    private static final int DEFAULT_SIZE = 10;

    /**
     * 数组大小
     */
    private int size = 0;

    public SortArrayMap() {
        buckets = new Node[DEFAULT_SIZE];
    }

    /**
     * 写入数据
     * @param key
     * @param value
     */
    public void add(Long key, String value) {
        checkSize(size + 1);
        Node node = new Node(key, value);
        buckets[size++] = node;
    }

    public SortArrayMap remove(String value){
        List<Node> list = new ArrayList<>(Arrays.asList(buckets));
        list.removeIf(next -> next != null && next.value.equals(value));
        buckets = list.toArray(new Node[0]);
        return this;
    }

    /**
     * 校验是否需要扩容
     * @param size
     */
    private void checkSize(int size) {
        if (size >= buckets.length) {
            //扩容自身的 3/2
            int oldLen = buckets.length;
            int newLen = oldLen + (oldLen >> 1);
            buckets = Arrays.copyOf(buckets, newLen);
        }
    }

    /**
     * 顺时针取出数据
     * @param key
     * @return
     */
    public String firstNodeValue(long key) {
        if (size == 0){
            return null ;
        }
        for (Node bucket : buckets) {
            if (bucket == null){
                break;
            }
            if (bucket.key >= key) {
                return bucket.value;
            }
        }

        return buckets[0].value;

    }

    /**
     * 排序
     */
    public void sort() {
        Arrays.sort(buckets, 0, size, (o1, o2) -> {
            if (o1.key > o2.key) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    public void print() {
        for (Node bucket : buckets) {
            if (bucket == null) {
                continue;
            }
            System.out.println(bucket.toString());
        }
    }

    public int size() {
        return size;
    }

    public void clear(){
        buckets = new Node[DEFAULT_SIZE];
        size = 0 ;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        Set<Entry<String, String>> set = Sets.newHashSet();
        for (Node bucket : buckets) {
            set.add(new SimpleEntry<>(String.valueOf(bucket.key), bucket.value));
        }
        return set;
    }

    @Override
    public Set<String> keySet() {
        Set<String> set = Sets.newHashSet();
        for (Node bucket : buckets) {
            if (bucket == null){
                continue;
            }
            set.add(bucket.value);
        }
        return set;
    }

    /**
     * 数据节点
     */
    private class Node {
        public Long key;
        public String value;

        public Node(Long key, String value) {
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

}
