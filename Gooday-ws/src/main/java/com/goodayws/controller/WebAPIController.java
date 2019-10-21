package com.goodayws.controller;

import com.goodaybase.model.vo.LoginUserVo;
import com.goodaybase.util.AuthUtil;
import com.goodayws.websocket.MyHandler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.socket.TextMessage;
import rxframework.base.controller.BaseController;
import rxframework.base.model.ServiceResult;
import rxframework.utility.cache.MemcachedUtil;
import rxframework.utility.string.StringUtils;
import rxui.manager.SessionContext;

import javax.inject.Inject;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

@Controller
@RequestMapping("/webapi")
public class WebAPIController extends BaseController {

    @Inject
    private MyHandler handler;

    @RequestMapping(value = "login")
    @ResponseBody
    public ServiceResult login(HttpServletRequest request) {
        ServiceResult sr = new ServiceResult();

        // 先从请求参数取
        String username = request.getParameter("username");
        // 再从请求主体取
        if(StringUtils.isNullOrEmpty(username) && request.getContentLength()>0) {
            try {
                ServletInputStream is = request.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer sb = new StringBuffer();
                String line;
                while((line=br.readLine())!=null){
                    sb.append(line);
                }
                br.close();
                JSONObject jsonObj = JSONObject.fromObject(sb.toString());
                if(jsonObj != null){
                    username = StringUtils.safeString(jsonObj.get("username"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (StringUtils.hasText(username)) {
            // 创建缓存对象
            LoginUserVo user = AuthUtil.createLoginUserVoAndToken(username);
            HttpSession session = SessionContext.getSession();
            session.setAttribute("username", username);
            sr.setErrorMsg("登录成功");
            System.out.println("HttpSessionId:" + session.getId());
        }else{
            sr.setErrorCode(-1);
            sr.setErrorMsg("登录失败，用户名为空");
        }

        System.out.println("登录接口,username=" + username);
        return sr;
    }

    @RequestMapping(value = "logout")
    @ResponseBody
    public ServiceResult logout(HttpServletRequest request) {
        ServiceResult sr = new ServiceResult();
        sr.setErrorMsg("退出成功");
        return sr;
    }

    @RequestMapping(value = "message")
    @ResponseBody
    public ServiceResult sendMessage(HttpServletRequest request) {
        ServiceResult sr = new ServiceResult();
        boolean flag = handler.sendMessageToAllUsers(new TextMessage("你好"));
        System.out.println(flag);
        sr.setErrorMsg("发送成功");
        return sr;
    }
}
