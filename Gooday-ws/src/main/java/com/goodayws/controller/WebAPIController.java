package com.goodayws.controller;

import com.goodaybase.constant.CommParams;
import com.goodaybase.model.entity.GpsPoint;
import com.goodaybase.model.vo.LoginUserVo;
import com.goodaybase.util.AuthUtil;
import com.goodayws.websocket.MyHandler;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.socket.TextMessage;
import rxframework.base.controller.BaseController;
import rxframework.base.model.ServiceResult;
import rxframework.utility.string.StringUtils;
import rxui.manager.SessionContext;

import javax.inject.Inject;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

@Controller
@RequestMapping("/webapi")
public class WebAPIController extends BaseController {

    //存储指定用户定位
    private static final String CLIENT_NAME = "dasheng";

    @Inject
    private MyHandler handler;

    @RequestMapping(value = "login")
    @ResponseBody
    public ServiceResult login(HttpServletRequest request) {
        ServiceResult sr = new ServiceResult();

        // 先从请求参数取
        String username = request.getParameter("username");
        // 再从请求主体取
        if (StringUtils.isNullOrEmpty(username) && request.getContentLength() > 0) {
            try {
                ServletInputStream is = request.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                StringBuffer sb = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                JSONObject jsonObj = JSONObject.fromObject(sb.toString());
                if (jsonObj != null) {
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
        } else {
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

    @RequestMapping(value = "map")
    public ModelAndView map(HttpServletRequest request) {
        ServiceResult sr = getMapData(request);
        HashMap<String, Object> hashMap = (HashMap<String, Object>) (sr.getData());
        request.setAttribute("pointStart", hashMap.get("pointStart"));
        request.setAttribute("pointCenter", hashMap.get("pointCenter"));
        request.setAttribute("pointEnd", hashMap.get("pointEnd"));

        request.setAttribute("queue", hashMap.get("queue"));
        return new ModelAndView("/gooday/map");
    }

    @RequestMapping(value = "getMapData")
    @ResponseBody
    public ServiceResult getMapData(HttpServletRequest request) {
        ServiceResult sr = new ServiceResult();

        Queue<GpsPoint> queue = new LinkedList<GpsPoint>();
        GpsPoint pointCenter = new GpsPoint(116.404, 39.915);
        GpsPoint pointStart = new GpsPoint(pointCenter);
        GpsPoint pointEnd = new GpsPoint(pointCenter);
        // Object objLocationPoints = MCacheUtils.get(WebCache.getSystemParam("PLATFORM_NAMESPACE") + "_LOCATION_POINTS", CLIENT_NAME);
        Object objLocationPoints = CommParams.queues.get(CommParams.CLIENT_NAME);
        // Object objLocationPoints = MyHandler.queues.get(CLIENT_NAME);
        if (objLocationPoints != null) {
            queue = (Queue<GpsPoint>) objLocationPoints;
        } else {
            GpsPoint point1 = new GpsPoint(116.350658, 39.938285);
            queue.offer(point1);

            GpsPoint point2 = new GpsPoint(116.386446, 39.939281);
            queue.offer(point2);

            GpsPoint point3 = new GpsPoint(116.389034, 39.913828);
            queue.offer(point3);

            GpsPoint point4 = new GpsPoint(116.442501, 39.914603);
            queue.offer(point4);
        }
        Queue<GpsPoint> queueCopy = new LinkedList<GpsPoint>();
        int size = queue.size();
        for (int i = 0; i < size; i++) {
            GpsPoint point = queue.poll();
            queueCopy.offer(point);
            if (i == 0) {
                pointStart.setPoint(point);
            } else if (i == size / 2) {
                pointCenter.setPoint(point);
            } else if (i == size - 1) {
                pointEnd.setPoint(point);
            }
        }
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("pointStart", pointStart);
        hashMap.put("pointCenter", pointCenter);
        hashMap.put("pointEnd", pointEnd);
        hashMap.put("queue", queueCopy);

        sr.setData(hashMap);

        return sr;
    }
}
