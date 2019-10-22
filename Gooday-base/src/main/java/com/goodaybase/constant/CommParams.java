package com.goodaybase.constant;

import com.goodaybase.model.entity.GpsPoint;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * 全局参数
 */
public class CommParams {

    //存储指定用户定位
    public static final String CLIENT_NAME = "dasheng";
    public static final Map<String, Queue<GpsPoint>> queues = new HashMap<String, Queue<GpsPoint>>();
}
