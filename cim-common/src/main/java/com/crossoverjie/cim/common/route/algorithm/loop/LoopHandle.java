package com.crossoverjie.cim.common.route.algorithm.loop;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.route.algorithm.RouteHandle;

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
    private AtomicLong index = new AtomicLong();

    @Override
    public String routeServer(List<String> values,String key) {
        if (values.size() == 0) {
            throw new CIMException(StatusEnum.SERVER_NOT_AVAILABLE) ;
        }
        Long position = index.incrementAndGet() % values.size();
        if (position < 0) {
            position = 0L;
        }

        return values.get(position.intValue());
    }
}
