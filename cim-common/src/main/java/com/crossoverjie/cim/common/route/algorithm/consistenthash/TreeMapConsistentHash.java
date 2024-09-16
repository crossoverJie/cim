package com.crossoverjie.cim.common.route.algorithm.consistenthash;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.google.common.annotations.VisibleForTesting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Function:TreeMap 实现
 *
 * @author crossoverJie
 * Date: 2019-02-27 01:16
 * @since JDK 1.8
 */
public class TreeMapConsistentHash extends AbstractConsistentHash {
    private final TreeMap<Long,String> treeMap = new TreeMap<Long, String>() ;

    /**
     * 虚拟节点数量
     */
    private static final int VIRTUAL_NODE_SIZE = 2 ;

    @Override
    public void add(long key, String value) {
        for (int i = 0; i < VIRTUAL_NODE_SIZE; i++) {
            Long hash = super.hash("vir" + key + i);
            treeMap.put(hash,value);
        }
        treeMap.put(key, value);
    }

    @Override
    protected Map<String,String> remove(String value) {
        treeMap.entrySet().removeIf(next -> next.getValue().equals(value));
        Map<String, String> result = new HashMap<>(treeMap.entrySet().size());
        for (Map.Entry<Long, String> longStringEntry : treeMap.entrySet()) {
            result.put(longStringEntry.getValue(),"");
        }
        return result;
    }

    @Override
    protected void clear() {
        treeMap.clear();
    }

    /**
     * Used only in test.
     * @return Return the data structure of the current algorithm.
     */
    @VisibleForTesting
    public TreeMap getTreeMap() {
        return treeMap;
    }

    @Override
    public String getFirstNodeValue(String value) {
        long hash = super.hash(value);
        SortedMap<Long, String> last = treeMap.tailMap(hash);
        if (!last.isEmpty()) {
            return last.get(last.firstKey());
        }
        if (treeMap.size() == 0){
            throw new CIMException(StatusEnum.SERVER_NOT_AVAILABLE) ;
        }
        return treeMap.firstEntry().getValue();
    }

}
