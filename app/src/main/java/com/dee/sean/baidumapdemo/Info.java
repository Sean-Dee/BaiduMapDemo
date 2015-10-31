package com.dee.sean.baidumapdemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dixin on 15/10/31.
 */
public class Info implements Serializable {

    private double latitude;
    private double longtitude;
    private int imgId;
    private String name;
    private String distance;
    private int zan;

    public static List<Info> infos = new ArrayList<Info>();

    static {
        infos.add(new Info(31.2816150000, 120.7332930000, 999, "20米", "中国科学技术大学(苏州研究院)", R.drawable.a01));
        infos.add(new Info(31.2758440000, 120.7568820000, 239, "1000米", "文汇人才公寓", R.drawable.a02));
    }

    public Info(double latitude, double longtitude, int zan, String distance, String name, int imgId) {
        this.latitude = latitude;
        this.zan = zan;
        this.distance = distance;
        this.name = name;
        this.imgId = imgId;
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public int getZan() {
        return zan;
    }

    public void setZan(int zan) {
        this.zan = zan;
    }
}
