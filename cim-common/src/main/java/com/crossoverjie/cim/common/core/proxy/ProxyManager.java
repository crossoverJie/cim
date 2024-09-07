package com.crossoverjie.cim.common.core.proxy;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.util.HttpClient;
import okhttp3.OkHttpClient;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URI;
import okhttp3.Response;

import static com.crossoverjie.cim.common.enums.StatusEnum.VALIDATION_FAIL;

/**
 * Function:
 *
 * @author crossoverJie
 * Date: 2020-04-25 00:18
 * @since JDK 1.8
 */
public final class ProxyManager<T> {


    private final Class<T> clazz;

    private final String url;

    private final OkHttpClient okHttpClient;

    /**
     * @param clazz        Proxied interface
     * @param url          server provider url
     * @param okHttpClient http client
     */
    public ProxyManager(Class<T> clazz, String url, OkHttpClient okHttpClient) {
        this.clazz = clazz;
        this.url = url;
        this.okHttpClient = okHttpClient;
    }


    /**
     * Get proxy instance of api.
     *
     * @return
     */
    public T getInstance() {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz},
                new ProxyInvocation());
    }


    private class ProxyInvocation implements InvocationHandler {

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Response result = null;
            try {
                Request annotation = method.getAnnotation(Request.class);
                if (annotation != null && annotation.method().equals(Request.GET)) {
                    result = HttpClient.get(okHttpClient, url);
                } else {
                    JSONObject jsonObject = new JSONObject();
                    String serverUrl = url + "/" + method.getName();
                    URI serverUri = new URI(serverUrl);
                    serverUrl = serverUri.normalize().toString();

                    if (args != null && args.length > 1) {
                        throw new CIMException(VALIDATION_FAIL);
                    }

                    if (method.getParameterTypes().length > 0) {
                        Object para = args[0];
                        Class<?> parameterType = method.getParameterTypes()[0];
                        for (Field field : parameterType.getDeclaredFields()) {
                            field.setAccessible(true);
                            jsonObject.put(field.getName(), field.get(para));
                        }
                    }
                    result = HttpClient.post(okHttpClient, jsonObject.toString(), serverUrl);
                }
                if (method.getReturnType() == void.class){
                    return null;
                }
                String json = result.body().string();
                return JSON.parseObject(json, method.getReturnType());
            } finally {
                if (result != null) {
                    result.body().close();
                }
            }
        }
    }
}
