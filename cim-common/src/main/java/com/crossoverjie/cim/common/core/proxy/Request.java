package com.crossoverjie.cim.common.core.proxy;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author crossoverJie
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Request {
    String method() default POST;
    String url() default "";

    String GET = "GET";
    String POST = "POST";
}
