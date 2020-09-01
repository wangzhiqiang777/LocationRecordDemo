package com.neusoft.qiangzi.locationrecorddemo.database;

import android.location.Location;

import java.sql.Time;
import java.util.Date;

public class LocationBean extends Location {
    int id;
    int recordId;
    Time recordTime;
    public LocationBean(int recordId, Location l) {
        super(l);
        this.recordId = recordId;
        recordTime = new Time(System.currentTimeMillis());
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public Time getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(Time recordTime) {
        this.recordTime = recordTime;
    }
}
