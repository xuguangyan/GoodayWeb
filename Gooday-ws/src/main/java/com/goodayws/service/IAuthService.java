package com.goodayws.service;

import rxframework.base.model.ServiceResult;
import rxui.manager.model.LoginUser;

import javax.servlet.http.HttpServletRequest;

public interface IAuthService {


    /**
     * 登录
     * @param request
     * @param openId
     * @return
     */
    ServiceResult loginByOpenId(HttpServletRequest request, String openId);
//    /**
//     * 登录
//     *
//     * @param userName
//     * @param password
//     * @return
//     */
//    ServiceResult login(HttpServletRequest request, String userName, String password);

//    /**
//     * 退出
//     *
//     * @param token
//     * @return
//     */
//    ServiceResult logout(HttpServletRequest request, String token);
//
//    /**
//     * 获取用户
//     * @param token
//     * @return
//     */
//    LoginUser getLoginUser(String token);
}
