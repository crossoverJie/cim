package com.crossoverjie.cim.route.exception;

import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.res.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020-04-12 22:13
 * @since JDK 1.8
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandlingController {
    @ExceptionHandler(CIMException.class)
    @ResponseBody()
    public BaseResponse handleAllExceptions(CIMException ex) {
        log.error("exception", ex);
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(ex.getErrorCode());
        baseResponse.setMessage(ex.getMessage());
        return baseResponse;
    }

}
