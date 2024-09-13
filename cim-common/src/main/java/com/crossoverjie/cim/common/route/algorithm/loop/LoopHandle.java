package com.crossoverjie.cim.common.route.algorithm.loop;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.pojo.RouteInfo;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;

import com.crossoverjie.cim.common.util.RouteInfoParseUtil;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2019-02-27 15:13
 * @since JDK 1.8
 */
public class LoopHandle implements RouteHandle {
    private final AtomicLong index = new AtomicLong();

    private List<String> values;

    @Override
    public String routeServer(List<String> values,String key) {
        if (values.size() == 0) {
            throw new CIMException(StatusEnum.SERVER_NOT_AVAILABLE) ;
        }
        this.values = values;
        Long position = index.incrementAndGet() % values.size();
        if (position < 0) {
            position = 0L;
        }

        return values.get(position.intValue());
    }

    @Override
    public List<String> removeExpireServer(RouteInfo routeInfo) {
        String parse = RouteInfoParseUtil.parse(routeInfo);
        values.removeIf(next -> next.equals(parse));
        return values;
    }
}
