package com.crossoverjie.cim.server.api;

import com.crossoverjie.cim.common.core.proxy.DynamicUrl;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.server.api.vo.req.SendMsgReqVO;
import com.crossoverjie.cim.server.api.vo.res.SendMsgResVO;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020-04-25 14:23
 * @since JDK 1.8
 */
public interface ServerApi {

    /**
     * Push msg to client
     * @param sendMsgReqVO
     * @return
     * @throws Exception
     */
    BaseResponse<SendMsgResVO> sendMsg(SendMsgReqVO sendMsgReqVO, @DynamicUrl String url);
}
