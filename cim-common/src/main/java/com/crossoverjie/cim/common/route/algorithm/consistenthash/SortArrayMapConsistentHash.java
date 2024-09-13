package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import com.crossoverjie.cim.common.data.construct.SortArrayMap;
import com.google.common.annotations.VisibleForTesting;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Function:自定义排序 Map 实现
 *
 * @author crossoverJie
 * Date: 2019-02-27 00:38
 * @since JDK 1.8
 */
public class SortArrayMapConsistentHash extends AbstractConsistentHash {

    private SortArrayMap sortArrayMap = new SortArrayMap();

    /**
     * 虚拟节点数量
     */
    private static final int VIRTUAL_NODE_SIZE = 2 ;

    @Override
    public void add(long key, String value) {
        for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
            Long hash = super.hash("vir" + key + i);
            sortArrayMap.add(hash, value);
        }
        sortArrayMap.add(key, value);
    }

    @Override
    protected Map<String,String> remove(String value) {
        sortArrayMap = sortArrayMap.remove(value);
        return sortArrayMap;
    }

    @Override
    public void sort() {
        sortArrayMap.sort();
    }

    /**
     * Used only in test.
     * @return Return the data structure of the current algorithm.
     */
    @VisibleForTesting
    public SortArrayMap getSortArrayMap() {
        return sortArrayMap;
    }

    @Override
    protected void clear() {
        sortArrayMap.clear();
    }

    @Override
    public String getFirstNodeValue(String value) {
        long hash = super.hash(value);
        return sortArrayMap.firstNodeValue(hash);
    }

}
