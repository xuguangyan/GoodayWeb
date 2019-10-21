package com.goodaybase.util;

import com.goodaybase.model.vo.LoginUserVo;
import rxframework.utility.cache.MCacheUtils;
import rxframework.utility.cache.MemcacheException;
import rxframework.utility.string.StringUtils;
import rxplatform.system.cache.WebCache;
import rxplatform.system.constant.SystemConstants;
import rxplatform.system.model.entity.CommonTypeEntity;
import rxplatform.system.service.impl.CommonTypeService;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.UUID;

public class AuthUtil {
    // 缓存名称
    public static String CacheName = "LoginUser";
    // 平台id
    public static String PlatformId = "b6213845-2c64-4a94-9cfc-cea832b0be70";

    // 社会人事企业ID
    public static String CommonEtpId = "0434ac46-fe09-4f5e-80b5-d5bd4c3e4b1c";

    /**
     * 创建缓存并创建token，并设置openId(登录和注册时使用)
     * @return
     */
    public static LoginUserVo createLoginUserVoAndToken(String openId) {
        LoginUserVo user = new LoginUserVo();
        // 生成token令牌
        String tokenId = UUID.randomUUID().toString();
        user.setToken(tokenId);
        user.setOpenId(openId);
        // 刷新缓存
        updateLoginTime(user);
        return user;
    }

    /**
     * 设置手机，(注册时使用)
     * @param phone
     * @return
     */
    public static LoginUserVo setLoginUserVoPhone(String token, String phone) {
        LoginUserVo user = getLoginUserVo(token);
        if(StringUtils.hasText(phone)){
            user.setPhone(phone);
        }
        // 刷新缓存
        updateLoginTime(user);
        return user;
    }

    /**
     * 更新用户缓存时间
     * @param user
     * @return
     */
    public static boolean updateLoginTime(LoginUserVo user){
        boolean isOk = false;
        if (user != null) {
            if(StringUtils.hasText(user.getToken())) {
                // 登录过期毫秒
                int unLoginTime = WebCache.getSystemParamAsInt("WX_UN_LOGIN_TIME");
                // 设置到mc
                isOk = MCacheUtils.put(
                        WebCache.getSystemParam(SystemConstants.PLATFORM_NAMESPACE) + "." + CacheName,
                        user.getToken(), user, new Date(unLoginTime));
            }
        }
        return isOk;
    }

    /**
     * 获取用户缓存
     * @param request
     * @return
     */
    public static LoginUserVo getLoginUserVo(HttpServletRequest request){
        String token = request.getHeader("x-access-token");
        return getLoginUserVo(token);
    }
    /**
     * 获取用户缓存
     * @param token
     * @return
     */
    public static LoginUserVo getLoginUserVo(String token){
        if(StringUtils.hasText(token)) {
            LoginUserVo user = (LoginUserVo) MCacheUtils.get(
                    WebCache.getSystemParam(SystemConstants.PLATFORM_NAMESPACE)
                            + "." + CacheName, token);
            return user;
        }
        return null;
    }

    /**
     * 删除用户缓存
     * @param token
     * @return
     */
    public static void removeLoginUserVo(String token){
        try {
            MCacheUtils.remove(WebCache.getSystemParam(
                    SystemConstants.PLATFORM_NAMESPACE)+"."+CacheName, token);
        } catch (MemcacheException e) {
            e.printStackTrace();
        }
    }

    /**
     * 直接获取登录用户的id'
     * @param request
     * @return
     */
    public static String getLogInUserId(HttpServletRequest request) {
        LoginUserVo loginUserVo = getLoginUserVo(request);
        String id = "";
        if (loginUserVo != null) {
            id = loginUserVo.getId();
        }
        return id;
    }

    // 获取主项或者副项文字描述
    private static String getItemsText(String mainItemsBig, String mainItemsSmall){
        String result = "";
        String smallStr = "";
        if(StringUtils.hasText(mainItemsBig)){
            CommonTypeEntity big = CommonTypeService.SELF.getByCode(mainItemsBig);
            if(big!=null){
                if(StringUtils.hasText(mainItemsSmall)){
                    String[] smallArr = mainItemsSmall.split(",");
                    for(int i=0; i<smallArr.length; i++){
                        CommonTypeEntity com = CommonTypeService.SELF.getByCode(smallArr[i]);
                        if(com != null){
                            if(StringUtils.hasText(smallStr)) {
                                smallStr += ",";
                            }
                            smallStr += big.getName()+"-"+com.getName();
                        }
                    }
                }
            }
        }
        if(StringUtils.hasText(smallStr)) {
            result = smallStr;
        }else{

        }
        return result;
    }
}
