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
public final class ProxyManager<T,R> {


    private final Class<T> clazz;
    private Class<R> responseClazz;

    private final String url;

    private final OkHttpClient okHttpClient;



    /**
     *
     * @param clazz Proxied interface
     * @param url server provider url
     * @param okHttpClient http client
     */
    public ProxyManager(Class<T> clazz, String url, OkHttpClient okHttpClient) {
        this.clazz = clazz;
        this.url = url;
        this.okHttpClient = okHttpClient;
    }
    public ProxyManager(Class<T> clazz, Class<R> responseClazz, String url, OkHttpClient okHttpClient) {
        this(clazz, url, okHttpClient);
        this.responseClazz = responseClazz;
    }

    /**
     * Get proxy instance of api.
     * @return
     */
    public T getInstance() {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz}, new ProxyInvocation());
    }


    private class ProxyInvocation implements InvocationHandler {

        @Override
        public R invoke(Object proxy, Method method, Object[] args) throws Throwable {
            JSONObject jsonObject = new JSONObject();
            String serverUrl = url + "/" + method.getName() ;

            URI serverUri = new URI(serverUrl);
            serverUrl = serverUri.normalize().toString();

            if (args != null && args.length > 1) {
                throw new CIMException(VALIDATION_FAIL);
            }

            if (method.getParameterTypes().length > 0){
                Object para = args[0];
                Class<?> parameterType = method.getParameterTypes()[0];
                for (Field field : parameterType.getDeclaredFields()) {
                    field.setAccessible(true);
                    jsonObject.put(field.getName(), field.get(para));
                }
            }
            Response result = null;
            try {
                result = HttpClient.post(okHttpClient, jsonObject.toString(), serverUrl);
                String json = result.body().string() ;
                return JSON.parseObject(json, responseClazz);
            }finally {
                if (result != null) {
                    result.body().close();
                }
            }
        }
    }
}
