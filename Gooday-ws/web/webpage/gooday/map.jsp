<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
    <meta name="viewport" content="initial-scale=1.0, user-scalable=no"/>
    <style type="text/css">
        body, html, #allmap {
            width: 100%;
            height: 100%;
            overflow: hidden;
            margin: 0;
            font-family: "微软雅黑";
        }
    </style>
    <script type="text/javascript" src="//api.map.baidu.com/api?v=2.0&ak=hz3QAaDPaqU3VuO5zZ6TbbCt"></script>
    <title>Map</title>
</head>
<body>
<div id="allmap"></div>
</body>
</html>
<script type="text/javascript">
    // 百度地图API功能
    var map = new BMap.Map("allmap");    // 创建Map实例
    map.centerAndZoom(new BMap.Point(116.404, 39.915), 14);
    var sy = new BMap.Symbol(BMap_Symbol_SHAPE_BACKWARD_OPEN_ARROW, {
        scale: 0.6,//图标缩放大小
        strokeColor: '#fff',//设置矢量图标的线填充颜色
        strokeWeight: '1',//设置线宽
    });
    var icons = new BMap.IconSequence(sy, '10', '30');
    var myIcon1 = new BMap.Icon("${pageContext.request.contextPath}/images/poi_start.png", new BMap.Size(35, 35));
    var myIcon2 = new BMap.Icon("${pageContext.request.contextPath}/images/poi_end.png", new BMap.Size(35, 35));
    var marker1 = new BMap.Marker(new BMap.Point(116.404, 39.915), {icon: myIcon1});
    var marker2 = new BMap.Marker(new BMap.Point(116.404, 39.915), {icon: myIcon2});

    function drawMap(data) {
        var pointStart = data.pointStart;
        var pointCenter = data.pointCenter;
        var pointEnd = data.pointEnd;
        var queue = data.queue;
        console.log(pointCenter);
        console.log(queue);

        map.setCenter(pointCenter);  // 初始化地图,设置中心点坐标和地图级别
        map.enableScrollWheelZoom(true);     //开启鼠标滚轮缩放

        map.clearOverlays();

        // 创建polyline对象
        var pois = queue;
        if (pois.length > 0) {
            var polyline = new BMap.Polyline(pois, {
                enableEditing: false,//是否启用线编辑，默认为false
                enableClicking: true,//是否响应点击事件，默认为true
                icons: [icons],
                strokeWeight: '4',//折线的宽度，以像素为单位
                strokeOpacity: 0.8,//折线的透明度，取值范围0 - 1
                strokeColor: "#ff0000" //折线颜色
            });
            map.addOverlay(polyline);  //增加折线
        }

        marker1.setPosition(pointStart);
        map.addOverlay(marker1);
        marker2.setPosition(pointEnd);
        map.addOverlay(marker2);
    }
</script>

<script src="${pageContext.request.contextPath}/scripts/common.js"></script>
<script src="http://libs.baidu.com/jquery/2.1.4/jquery.min.js"></script>
<script>
    var interval = null;
    $(function () {
        getData();

        interval = setInterval(function () {
            getData();
        }, 30000);
    })

    function getData() {
        var url = "${pageContext.request.contextPath}/webapi/getMapData";
        $.post(url, null, function (result) {
            if (result.errorCode == 0) {
                //坐标转换
                translate(result.data);
            } else {
                alert("请求失败：" + result.errorMsg);
            }
        });
    }

    //坐标转换完之后的回调函数
    translateCallback = function (data) {
        var newData = {};
        if (data.status == 0) {
            newData.pointStart = data.points[0];
            newData.pointCenter = data.points[1];
            newData.pointEnd = data.points[2];
            if (data.points.length > 3) {
                newData.queue = data.points.slice(3);
            } else {
                newData.queue = [];
            }
        } else { //转换不成功时，用回转换前坐标绘制
            newData.pointStart = pointArr[0];
            newData.pointCenter = pointArr[1];
            newData.pointEnd = pointArr[2];
            if (pointArr.length > 3) {
                newData.queue = pointArr.slice(3);
            } else {
                newData.queue = [];
            }
        }

        // 绘制地图
        drawMap(newData);
    }

    var pointArr = [];

    function translate(data) {
        pointArr = [];
        pointArr.push(new BMap.Point(data.pointStart.lng, data.pointStart.lat));
        pointArr.push(new BMap.Point(data.pointCenter.lng, data.pointCenter.lat));
        pointArr.push(new BMap.Point(data.pointEnd.lng, data.pointEnd.lat));
        for (var i = 0; i < data.queue.length; i++) {
            pointArr.push(new BMap.Point(data.queue[i].lng, data.queue[i].lat));
        }
        var convertor = new BMap.Convertor();
        // pointArr不能超过10个点，否则返回status=25
        // form = 1：GPS设备获取的角度坐标，WGS84坐标;
        // to = 5：bd09ll(百度经纬度坐标);
        // API文档地址：http://lbsyun.baidu.com/index.php?title=webapi/guide/changeposition
        convertor.translate(pointArr, 1, 5, translateCallback);
    }
</script>