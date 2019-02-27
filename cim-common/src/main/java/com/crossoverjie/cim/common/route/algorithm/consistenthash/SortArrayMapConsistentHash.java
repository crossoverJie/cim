package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import com.crossoverjie.cim.common.data.construct.SortArrayMap;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-02-27 00:38
 * @since JDK 1.8
 */
public class SortArrayMapConsistentHash extends AbstractConsistentHash {

    private SortArrayMap sortArrayMap = new SortArrayMap();

    @Override
    public void add(long key, String value) {
        sortArrayMap.add(key, value);
    }

    @Override
    public void sort() {
        sortArrayMap.sort();
        sortArrayMap.print();
    }

    @Override
    public String getFirstNodeValue(String value) {
        long hash = super.hash(value);
        System.out.println("value=" + value + " hash = " + hash);
        return sortArrayMap.firstNodeValue(hash);
    }

}
