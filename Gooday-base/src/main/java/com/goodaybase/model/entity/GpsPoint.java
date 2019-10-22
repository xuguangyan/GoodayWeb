package com.goodaybase.model.entity;

import java.io.Serializable;

/**
 * 经纬度点 对象
 */
public class GpsPoint implements Serializable {

    private double lng;
    private double lat;

    public GpsPoint() {
    }

    public GpsPoint(double lng, double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public GpsPoint(GpsPoint pt) {
        this.lng = pt.getLng();
        this.lat = pt.getLat();
    }

    public void setPoint(GpsPoint pt) {
        this.lng = pt.getLng();
        this.lat = pt.getLat();
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }
}
