package com.crossoverjie.cim.route.kit;

import com.alibaba.fastjson.JSONObject;
import com.crossoverjie.cim.route.util.SpringBeanFactory;
import com.crossoverjie.cim.route.vo.req.ChatReqVO;
import com.crossoverjie.cim.route.vo.req.MsgCallBackReqVo;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.Callable;

public class MsgCallBacker implements Callable<Boolean> {
    private static MediaType mediaType = MediaType.parse("application/json");

    private String callBackUrl;
    private long sendUserId;
    private ChatReqVO chatReqVO;

    public MsgCallBacker(String callBackUrl, long sendUserId, ChatReqVO groupReqVo) {
        this.callBackUrl = callBackUrl;
        this.sendUserId = sendUserId;
        this.chatReqVO = groupReqVo;
    }

    @Override
    public Boolean call() throws Exception {
        MsgCallBackReqVo msgCallBackReqVo = new MsgCallBackReqVo();
        msgCallBackReqVo.setSendUserId(sendUserId);
        msgCallBackReqVo.setReceiveUserId(chatReqVO.getUserId());
        msgCallBackReqVo.setMsg(chatReqVO.getMsg());
        String jsonBoby = JSONObject.toJSONString(msgCallBackReqVo);
        RequestBody requestBody = RequestBody.create(mediaType, jsonBoby);

        Request request = new Request.Builder()
                .url(callBackUrl)
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = SpringBeanFactory.getBean(OkHttpClient.class);
        Response response = okHttpClient.newCall(request).execute();
        try {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }
            return true;
        } catch (Exception e) {
            e.getStackTrace();
        } finally {
            response.body().close();
        }
        return false;
    }
}
