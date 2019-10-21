package com.goodayws.interceptor;

import com.goodaybase.model.vo.LoginUserVo;
import com.goodaybase.util.AuthUtil;
import com.goodayws.service.IAuthService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import rxframework.base.service.IBaseService;
import rxframework.interceptor.AbstractHandlerInterceptor;
import rxframework.utility.string.StringUtils;
import rxplatform.system.service.IResourceService;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 权限拦截器
 *
 * @author
 */
public class AuthInterceptor extends AbstractHandlerInterceptor {

    @Inject
    protected IBaseService baseService;
    @Inject
    protected IResourceService resourceService;
    @Inject
    protected IAuthService authService;

    protected List<String> excludeUrls; // 白名单

    protected Boolean isCheckAuth; // 是否检查权限

    public Boolean getIsCheckAuth() {
        return isCheckAuth;
    }

    public void setIsCheckAuth(Boolean isCheckAuth) {
        this.isCheckAuth = isCheckAuth;
    }

    public IBaseService getBaseService() {
        return baseService;
    }

    public void setBaseService(IBaseService baseService) {
        this.baseService = baseService;
    }

    public List<String> getExcludeUrls() {
        return excludeUrls;
    }

    public void setExcludeUrls(List<String> excludeUrls) {
        this.excludeUrls = excludeUrls;
    }

    /**
     * 在controller前拦截
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
        String requestPath = getRequestPath(request);// 用户访问的资源地址
        if (excludeUrls.contains(requestPath)) {
            return true;
        }
        // 从 http 请求头中取出 token
        LoginUserVo user = AuthUtil.getLoginUserVo(request);
        if (user != null) {// 同时必然有openId
            // 更新过期时间
            AuthUtil.updateLoginTime(user);
            if (StringUtils.hasText(user.getPhone())) {
                if (StringUtils.hasText(user.getRoles())) {
                    // 此处要判断角色
                    return true;
                } else {
                    // 没有注册角色，去角色页面
                    response.setStatus(401);
//                        response.sendRedirect("/auth/noAuthRole");
                    return false;
                }
            } else {
                // 没有手机，去手机注册页面
                response.setStatus(401);
//                    response.sendRedirect("/auth/noAuthPhone");
                return false;
            }
        } else {
            response.setStatus(401);
//                response.sendRedirect("/auth/noAuth");
            return false;
        }
    }

    /**
     * 转发
     *
     * @param request
     * @return
     */
    @RequestMapping(params = "forword")
    public ModelAndView forword(HttpServletRequest request) {

        return new ModelAndView(new RedirectView("login.do?login"));
    }

    /**
     * 获取请求路径
     *
     * @param request
     * @return
     */
    public static String getRequestPath(HttpServletRequest request) {
        String requestPath = request.getRequestURI();
        requestPath = requestPath.replace("//", "/");
        requestPath = requestPath.substring(request.getContextPath().length() + 1);// 去掉项目路径
        return requestPath;
    }
}