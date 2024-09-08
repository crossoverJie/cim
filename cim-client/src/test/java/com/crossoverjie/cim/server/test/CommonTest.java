package com.crossoverjie.cim.server.test;


import com.crossoverjie.cim.common.core.proxy.RpcProxyManager;
import com.crossoverjie.cim.common.pojo.CIMUserInfo;
import com.crossoverjie.cim.common.res.BaseResponse;
import com.crossoverjie.cim.route.api.RouteApi;
import com.crossoverjie.cim.route.api.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.api.vo.req.P2PReqVO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vdurmont.emoji.EmojiParser;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import org.junit.Test;

/**
 * Function:
 *
 * @author crossoverJie
 *         Date: 22/05/2018 18:44
 * @since JDK 1.8
 */
@Slf4j
public class CommonTest {




    @Test
    public void searchMsg2(){
        StringBuilder sb = new StringBuilder() ;
        String allMsg = "于是在之前的基础上我完善了一些内容，先来看看这个项目的介绍吧：\n" +
                "\n" +
                "CIM(CROSS-IM) 一款面向开发者的 IM(即时通讯)系统；同时提供了一些组件帮助开发者构建一款属于自己可水平扩展的 IM 。\n" +
                "\n" +
                "借助 CIM 你可以实现以下需求：" ;

        String key = "CIM" ;

        String[] split = allMsg.split("\n");
        for (String msg : split) {
            if (msg.trim().contains(key)){
                sb.append(msg).append("\n") ;
            }
        }
        int pos = 0;

        String result = sb.toString();

        int count = 1 ;
        int multiple = 2 ;
        while((pos = result.indexOf(key, pos)) >= 0) {

            log.info("{},{}",pos, pos + key.length());

            pos += key.length();


            count ++ ;
        }

        System.out.println(sb.toString());
        System.out.println(sb.toString().replace(key,"\033[31;4m" + key+"\033[0m"));
    }

    @Test
    public void log(){
        String msg = "hahahdsadsd" ;
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        int month = today.getMonthValue();
        int day = today.getDayOfMonth();

        String dir = "/opt/logs/cim/zhangsan" + "/";
        String fileName = dir + year + month + day + ".log";
        log.info("fileName={}", fileName);

        Path file = Paths.get(fileName);
        boolean exists = Files.exists(Paths.get(dir), LinkOption.NOFOLLOW_LINKS);
        try {
            if (!exists) {
                Files.createDirectories(Paths.get(dir));
            }

            List<String> lines = Arrays.asList(msg);

            Files.write(file, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.info("IOException", e);
        }

    }

    @Test
    public void emoji() throws Exception{
        String str = "An :grinning:awesome :smiley:string &#128516;with a few :wink:emojis!";
        String result = EmojiParser.parseToUnicode(str);
        System.out.println(result);


        result = EmojiParser.parseToAliases(str);
        System.out.println(result);
//
//        Collection<Emoji> all = EmojiManager.getAll();
//        for (Emoji emoji : all) {
//            System.out.println(EmojiParser.parseToAliases(emoji.getUnicode())  + "--->" + emoji.getUnicode() );
//        }

    }

    @Test
    public void emoji2(){
        String emostring ="😂";

        String face_with_tears_of_joy = emostring.replaceAll("\uD83D\uDE02", "face with tears of joy");
        System.out.println(face_with_tears_of_joy);

        System.out.println("======" + face_with_tears_of_joy.replaceAll("face with tears of joy","\uD83D\uDE02"));
    }

//    @Test
    public void deSerialize() throws Exception {
        RouteApi routeApi = RpcProxyManager.create(RouteApi.class, "http://localhost:8083", new OkHttpClient());

        BaseResponse<com.crossoverjie.cim.route.api.vo.res.CIMServerResVO> login =
                routeApi.login(new LoginReqVO(1725722966520L, "cj"));
        System.out.println(login.getDataBody());

        BaseResponse<Set<CIMUserInfo>> setBaseResponse = routeApi.onlineUser();
        log.info("setBaseResponse={}",setBaseResponse.getDataBody());
    }

    @Test
    public void json() throws JsonProcessingException, ClassNotFoundException {
        String json = "{\"code\":\"9000\",\"message\":\"成功\",\"reqNo\":null,\"dataBody\":{\"ip\":\"127.0.0.1\",\"cimServerPort\":11211,\"httpPort\":8081}}";

        ObjectMapper objectMapper = new ObjectMapper();
        Class<?> generic = null;
        for (Method declaredMethod : RouteApi.class.getDeclaredMethods()) {
            if (declaredMethod.getName().equals("login")){
                Type returnType = declaredMethod.getGenericReturnType();

                // check if the return type is a parameterized type
                if (returnType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) returnType;

                    Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();

                    for (Type typeArgument : actualTypeArguments) {
                        System.out.println("generic: " + typeArgument.getTypeName());
                        generic = Class.forName(typeArgument.getTypeName());
                        break;
                    }
                } else {
                    System.out.println("not a generic type");
                }
            }
        }
        BaseResponse<com.crossoverjie.cim.route.api.vo.res.CIMServerResVO> response = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructParametricType(BaseResponse.class, generic));
        System.out.println(response.getDataBody().getIp());
    }


    private static class Gen<T,R>{
        private T t;
        private R r;
    }

    interface TestInterface{
        Gen<String, P2PReqVO> login();
    }


    @Test
    public void test1() throws JsonProcessingException {
        String json = "{\"code\":\"200\",\"message\":\"Success\",\"reqNo\":null,\"dataBody\":[{\"userId\":\"123\",\"userName\":\"Alice\"}, {\"userId\":\"456\",\"userName\":\"Bob\"}]}";

        ObjectMapper objectMapper = new ObjectMapper();

        // 获取 BaseResponse<Set<CIMUserInfo>> 的泛型参数
        Type setType = getGenericTypeOfBaseResponse();

        // 将泛型类型传递给 ObjectMapper 进行反序列化
        BaseResponse<Set<CIMUserInfo>> response = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructParametricType(BaseResponse.class, objectMapper.getTypeFactory().constructType(setType)));

        System.out.println("Response Code: " + response.getCode());
        System.out.println("Online Users: ");
        for (CIMUserInfo user : response.getDataBody()) {
            System.out.println("User ID: " + user.getUserId() + ", User Name: " + user.getUserName());
        }
    }

    // 通过反射获取 BaseResponse<Set<CIMUserInfo>> 中的泛型类型
    public static Type getGenericTypeOfBaseResponse() {
        // 这里模拟你需要处理的 BaseResponse<Set<CIMUserInfo>>
        ParameterizedType baseResponseType = (ParameterizedType) new TypeReference<BaseResponse<Set<CIMUserInfo>>>() {}.getType();

        // 获取 BaseResponse 的泛型参数，即 Set<CIMUserInfo>
        Type[] actualTypeArguments = baseResponseType.getActualTypeArguments();

        // 返回第一个泛型参数 (Set<CIMUserInfo>)
        return actualTypeArguments[0];
    }
}
