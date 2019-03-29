package com.crossoverjie.cim.common.route.algorithm.weightrandom;

import com.crossoverjie.cim.common.route.algorithm.RouteHandle;
import com.crossoverjie.cim.common.route.algorithm.random.RandomHandle;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Function: 路由策略， 权重随机
 *
 * @author luozhou
 * @Description:
 * @date 2019/3/29 14:15
 * @since JDK 1.8
 */
public class WeightRandomHandle implements RouteHandle {

    private TreeMap<String, String> weigthTreeMap = new TreeMap<String, String>(new WeightComparator());
    /**
     * 存放相同权重节点，如果选中相同权重节点会再进入随机路由进行处理
     */
    private List<String> sameWeightNodes = new ArrayList<>();


    @Override
    public String routeServer(List<String> values, String key) {
        //初始化权重数据
        // TODO: 2019-03-29 后期进行缓存，不用每次都需要处理权重
        initWeightData(values);

        int index = ThreadLocalRandom.current().nextInt(Integer.parseInt(weigthTreeMap.lastKey()));
        Map.Entry entry = weigthTreeMap.tailMap(String.valueOf(index), false).firstEntry();
        String serverNode = String.valueOf(entry.getValue());
        if (sameWeightNodes.contains(serverNode))
            serverNode = new RandomHandle().routeServer(sameWeightNodes, key);
        return serverNode;
    }

    private void initWeightData(List<String> values) {
        // TODO: 2019-03-29 后续优化此处代码，目前不够优雅
        int total = 0;
        int tmp = 0;
        //处理总数
        for (int i = 0; i < values.size(); i++) {
            String[] serverNodes = values.get(i).split(":");
            total += Integer.parseInt(serverNodes[3]);
        }
        //计算权重区间
        for (int i = 0; i < values.size(); i++) {
            String[] serverNodes = values.get(i).split(":");
            tmp += Integer.parseInt(serverNodes[3]) * 100 / total;
            weigthTreeMap.put(String.valueOf(tmp), values.get(i));
        }
    }

    class WeightComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            if (Integer.parseInt((String) o1) > Integer.parseInt((String) o2))
                return 1;
            else return -1;
        }
    }
}
