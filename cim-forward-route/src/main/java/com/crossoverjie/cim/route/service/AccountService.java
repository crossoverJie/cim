package com.crossoverjie.cim.route.service;

import com.crossoverjie.cim.route.vo.req.LoginReqVO;
import com.crossoverjie.cim.route.vo.res.RegisterInfoResVO;

/**
 * Function: 账户服务
 *
 * @author crossoverJie
 *         Date: 2018/12/23 21:57
 * @since JDK 1.8
 */
public interface AccountService {

    /**
     * 注册用户
     * @param info 用户信息
     * @return
     * @throws Exception
     */
    RegisterInfoResVO register(RegisterInfoResVO info) throws Exception;

    /**
     * 登录服务
     * @param loginReqVO 登录信息
     * @return true 成功 false 失败
     * @throws Exception
     */
    boolean login(LoginReqVO loginReqVO) throws Exception ;

    /**
     * 保存路由信息
     * @param msg 服务器信息
     * @param loginReqVO 用户信息
     * @throws Exception
     */
    void saveRouteInfo(LoginReqVO loginReqVO ,String msg) throws Exception ;
}
