package com.crossoverjie.cim.common.route.algorithm.random;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;

import com.crossoverjie.cim.common.util.RouteInfoParseUtil;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Function: 路由策略， 随机
 *
 * @Auther: jiangyunxiong
 * @Date: 2019/3/7 11:56
 * @since JDK 1.8
 */
public class RandomHandle implements RouteHandle {

    private List<String> values;
    @Override
    public String routeServer(List<String> values, String key) {
        int size = values.size();
        if (size == 0) {
            throw new CIMException(StatusEnum.SERVER_NOT_AVAILABLE) ;
        }
        this.values = values;
        int offset = ThreadLocalRandom.current().nextInt(size);

        return values.get(offset);
    }

    @Override
    public List<String> removeExpireServer(RouteInfo routeInfo) {
        String parse = RouteInfoParseUtil.parse(routeInfo);
        values.removeIf(next -> next.equals(parse));
        return values;
    }
}
