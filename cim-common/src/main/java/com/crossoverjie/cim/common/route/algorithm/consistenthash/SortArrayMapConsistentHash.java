package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import com.crossoverjie.cim.common.data.construct.SortArrayMap;

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
        // fix https://github.com/crossoverJie/cim/issues/79
        sortArrayMap.clear();
        for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
            Long hash = super.hash("vir" + key + i);
            sortArrayMap.add(hash,value);
        }
        sortArrayMap.add(key, value);
    }

    @Override
    public void sort() {
        sortArrayMap.sort();
    }

    @Override
    public String getFirstNodeValue(String value) {
        long hash = super.hash(value);
        System.out.println("value=" + value + " hash = " + hash);
        return sortArrayMap.firstNodeValue(hash);
    }

}
