package com.crossoverjie.cim.route.controller;

import com.crossoverjie.cim.common.enums.StatusEnum;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.res.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalDefaultExceptionHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(GlobalDefaultExceptionHandler.class);
    /**
     * 捕抓异常
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public BaseResponse handleException(Exception e) {
        LOGGER.error("handleException",e);

        if (e instanceof CIMException) {
            CIMException cimException = (CIMException) e;
            return new BaseResponse(cimException.getErrorCode(),cimException.getErrorMessage());
        }
        return BaseResponse.create(e.getMessage(),StatusEnum.FAIL);
    }


}
