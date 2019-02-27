package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-02-27 01:16
 * @since JDK 1.8
 */
public class TreeMapConsistentHash extends AbstractConsistentHash {
    private TreeMap<Long,String> treeMap = new TreeMap<Long, String>() ;

    @Override
    public void add(long key, String value) {
        treeMap.put(key, value);
    }

    @Override
    public String getFirstNodeValue(String value) {
        long hash = super.hash(value);
        System.out.println("value=" + value + " hash = " + hash);
        SortedMap<Long, String> last = treeMap.tailMap(hash);
        if (!last.isEmpty()) {
            return last.get(last.firstKey());
        }
        return treeMap.firstEntry().getValue();
    }
}
