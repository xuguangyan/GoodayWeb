package com.goodayws.service.impl;

import com.goodaybase.util.AuthUtil;
import com.goodayws.service.IAuthService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rxframework.base.model.OutPut;
import rxframework.base.model.ServiceResult;
import rxframework.base.service.impl.RootService;
import rxplatform.system.service.IUserService;
import rxui.manager.model.LoginUser;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

@Service
@Transactional
public class AuthService extends RootService implements IAuthService {

    @Inject
    private IUserService userService;

    @Override
    public ServiceResult loginByOpenId(HttpServletRequest request, String openId) {
        // 通过openId 登录
        OutPut<LoginUser> loginUser = new OutPut<LoginUser>();
        ServiceResult sr = userService.loginByOpenId(request, AuthUtil.PlatformId, openId, loginUser);
        if (sr.getErrorCode() != 0) {
            // 登录失败
            //AuthUtil.getLoginUserVo(token);
//            LoginUser lgUser = new LoginUser();
//            lgUser.seto
//            AuthUtil.setLoginUserVo(lgUser, "");
        }else{

        }

        return sr;
    }
}
