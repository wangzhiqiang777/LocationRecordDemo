package com.neusoft.qiangzi.locationrecorddemo.database;

import java.sql.Time;
import java.util.LinkedList;
import java.util.List;

public class LocationRecordBean {
    int id;
    Time startTime;
    Time stopTime;
    double distance;
    List<LocationBean> locationBeans = new LinkedList<>();

    public LocationRecordBean() {
        startTime = new Time(System.currentTimeMillis());
        stopTime = new Time(System.currentTimeMillis());
    }
    void appendLocation(LocationBean l){
        locationBeans.add(l);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getStopTime() {
        return stopTime;
    }

    public void setStopTime(Time stopTime) {
        this.stopTime = stopTime;
    }

    public double getDistance() {
        return distance;
    }

    public void appendDistance(double distance) {
        this.distance += distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public List<LocationBean> getLocationBeans() {
        return locationBeans;
    }

    public void setLocationBeans(List<LocationBean> locationBeans) {
        this.locationBeans = locationBeans;
    }
}
