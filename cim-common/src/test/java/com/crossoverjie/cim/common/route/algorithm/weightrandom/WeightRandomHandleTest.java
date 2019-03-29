package com.crossoverjie.cim.common.route.algorithm.weightrandom;

import com.crossoverjie.cim.common.route.algorithm.RouteHandle;
import com.crossoverjie.cim.common.route.algorithm.weightrandom.WeightRandomHandle;
import org.junit.Test;

import java.util.*;

/**
 * @author luozhou
 * @Description:
 * @date 2019/3/29 18:42
 */
public class WeightRandomHandleTest {

    @Test
    public void testWeightRandom(){
        Map<String, Integer> countMap = new HashMap<>();
        List<String> testList = new ArrayList<>();
        testList.add("/route/ip-192.168.14.178:11211:8081:1");
        testList.add("/route/ip-192.168.14.178:11211:8082:1");
        testList.add("/route/ip-192.168.14.178:11211:8083:5");
        testList.add("/route/ip-192.168.14.178:11211:8084:7");
        testList.add("/route/ip-192.168.14.178:11211:8085:9");
        testList.add("/route/ip-192.168.14.178:11211:8086:11");
        RouteHandle handle = new WeightRandomHandle();
        for (int i = 0; i <1000 ; i++) {
            String routeStr = handle.routeServer(testList, "123");
            if (!countMap.containsKey(routeStr)){
                countMap.put(routeStr,1);
            }else {
                Integer intval=  countMap.get(routeStr);
                countMap.put(routeStr,++intval);
            }

        }
       Iterator<Map.Entry<String, Integer>> iterable= countMap.entrySet().iterator();
        while (iterable.hasNext()){
            Map.Entry entry= iterable.next();
            System.out.println( "节点："+entry.getKey()+"     命中："+entry.getValue() +"次");
        }
    }
}
