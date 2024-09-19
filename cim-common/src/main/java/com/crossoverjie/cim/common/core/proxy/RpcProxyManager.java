package com.crossoverjie.cim.common.core.proxy;

import static com.crossoverjie.cim.common.enums.StatusEnum.VALIDATION_FAIL;
import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.common.exception.CIMException;
import com.crossoverjie.cim.common.util.HttpClient;
import com.crossoverjie.cim.common.util.StringUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * RpcProxyManager is a proxy manager for creating dynamic proxy instances of interfaces.
 * It handles HTTP requests and responses using OkHttpClient.
 *
 * @param <T> the type of the proxied interface
 */
@Slf4j
public final class RpcProxyManager<T> {

    private Class<T> clazz;
    private String url;
    private OkHttpClient okHttpClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Private constructor to initialize RpcProxyManager.
     *
     * @param clazz        Proxied interface
     * @param url          Server provider URL
     * @param okHttpClient HTTP client
     */
    private RpcProxyManager(Class<T> clazz, String url, OkHttpClient okHttpClient) {
        this.clazz = clazz;
        this.url = url;
        this.okHttpClient = okHttpClient;
    }

    private RpcProxyManager(Class<T> clazz, OkHttpClient okHttpClient) {
        this(clazz, "", okHttpClient);
    }

    /**
     * Default private constructor.
     */
    private RpcProxyManager() {
    }

    /**
     * Creates a proxy instance of the specified interface.
     *
     * @param clazz        Proxied interface
     * @param url          Server provider URL
     * @param okHttpClient HTTP client
     * @param <T>          Type of the proxied interface
     * @return Proxy instance of the specified interface
     */
    public static <T> T create(Class<T> clazz, String url, OkHttpClient okHttpClient) {
        return new RpcProxyManager<>(clazz, url, okHttpClient).getInstance();
    }

    public static <T> T create(Class<T> clazz, OkHttpClient okHttpClient) {
        return new RpcProxyManager<>(clazz, okHttpClient).getInstance();
    }

    /**
     * Gets the proxy instance of the API.
     *
     * @return Proxy instance of the API
     */
    @SuppressWarnings("unchecked")
    public T getInstance() {
        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{clazz},
                new ProxyInvocation());
    }

    /**
     * ProxyInvocation is an invocation handler for handling method calls on proxy instances.
     */
    private class ProxyInvocation implements InvocationHandler {

        /**
         * Handles method calls on proxy instances.
         *
         * @param proxy  The proxy instance
         * @param method The method being called
         * @param args   The arguments of the method call
         * @return The result of the method call
         * @throws Throwable if an error occurs during method invocation
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

            Response result = null;
            String serverUrl = url + "/" + method.getName();
            Request annotation = method.getAnnotation(Request.class);
            if (annotation != null && StringUtil.isNotEmpty(annotation.url())) {
                serverUrl = url + "/" + annotation.url();
            }
            URI serverUri = new URI(serverUrl);
            serverUrl = serverUri.normalize().toString();

            Object para = null;
            Class<?> parameterType = null;
            for (int i = 0; i < method.getParameterAnnotations().length; i++) {
                Annotation[] annotations = method.getParameterAnnotations()[i];
                if (annotations.length == 0) {
                    para = args[i];
                    parameterType = method.getParameterTypes()[i];
                }

                for (Annotation ann : annotations) {
                    if (ann instanceof DynamicUrl) {
                        if (args[i] instanceof String) {
                            serverUrl = (String) args[i];
                            if (((DynamicUrl) ann).useMethodEndpoint()) {
                                serverUrl = serverUrl + "/" + method.getName();
                            }
                            break;
                        } else {
                            throw new CIMException("DynamicUrl must be String type");
                        }
                    }
                }
            }

            try {
                if (annotation != null && annotation.method().equals(Request.GET)) {
                    result = HttpClient.get(okHttpClient, serverUrl);
                } else {

                    if (args == null || args.length > 2 || para == null || parameterType == null) {
                        throw new IllegalArgumentException(VALIDATION_FAIL.message());
                    }
                    JSONObject jsonObject = new JSONObject();
                    for (Field field : parameterType.getDeclaredFields()) {
                        field.setAccessible(true);
                        jsonObject.put(field.getName(), field.get(para));
                    }

                    result = HttpClient.post(okHttpClient, jsonObject.toString(), serverUrl);
                }
                if (method.getReturnType() == void.class) {
                    return null;
                }

                String json = result.body().string();
                Type genericTypeOfBaseResponse = getGenericTypeOfBaseResponse(method);
                if (genericTypeOfBaseResponse == null) {
                    return objectMapper.readValue(json, method.getReturnType());
                } else {
                    return objectMapper.readValue(json, objectMapper.getTypeFactory()
                            .constructParametricType(method.getReturnType(),
                                    objectMapper.getTypeFactory().constructType(genericTypeOfBaseResponse)));
                }
            } finally {
                if (result != null) {
                    result.body().close();
                }
            }
        }
    }

    private Type getGenericTypeOfBaseResponse(Method declaredMethod) {
        Type returnType = declaredMethod.getGenericReturnType();

        // check if the return type is a parameterized type
        if (returnType instanceof ParameterizedType parameterizedType) {

            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

            for (Type typeArgument : actualTypeArguments) {
                return typeArgument;
            }
        }

        return null;

    }

    /**
     * Gets the generic type of the BaseResponse.
     *
     * @param declaredMethod The method whose return type is being checked
     * @return The generic type of the BaseResponse, or null if not found
     * @throws ClassNotFoundException if the class of the generic type is not found
    private Class<?> getBaseResponseGeneric(Method declaredMethod) throws ClassNotFoundException {

    Type returnType = declaredMethod.getGenericReturnType();

    // check if the return type is a parameterized type
    if (returnType instanceof ParameterizedType parameterizedType) {

    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

    for (Type typeArgument : actualTypeArguments) {
    // BaseResponse only has one generic type
    return getClass(typeArgument);
    }
    }

    return null;
    }

    public static Class<?> getClass(Type type) throws ClassNotFoundException {
    if (type instanceof Class<?>) {
    // 普通类型，直接返回
    return (Class<?>) type;
    } else if (type instanceof ParameterizedType) {
    // 参数化类型，返回原始类型
    return getClass(((ParameterizedType) type).getRawType());
    } else if (type instanceof TypeVariable<?>) {
    // 类型变量，无法在运行时获取具体类型
    return Object.class;
    } else {
    throw new ClassNotFoundException("无法处理的类型: " + type.toString());
    }
    }*/

}