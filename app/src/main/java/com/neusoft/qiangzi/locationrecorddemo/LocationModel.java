package com.neusoft.qiangzi.locationrecorddemo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;

import com.neusoft.qiangzi.locationrecorddemo.database.LocationBean;
import com.neusoft.qiangzi.locationrecorddemo.database.LocationDBHelper;
import com.neusoft.qiangzi.locationrecorddemo.database.LocationRecordBean;

public class LocationModel {

    private Context context;
    private LocationRecordBean currentRecord;
    private LocationBean lastLocationBean;

    public LocationModel(Context context) {
        this.context = context;
    }

    private int currentRecordId = 0;
    private static final String TAG = "LocationModel";
    public void startRecord(Location l){
        LocationDBHelper locationDBHelper = new LocationDBHelper(context);
        SQLiteDatabase db = locationDBHelper.getWritableDatabase();
        currentRecord = new LocationRecordBean();
        ContentValues recordValues = new ContentValues();
        recordValues.put(LocationDBHelper.COLUMN_START_TIME, currentRecord.getStartTime().getTime());
        recordValues.put(LocationDBHelper.COLUMN_STOP_TIME, currentRecord.getStopTime().getTime());
        recordValues.put(LocationDBHelper.COLUMN_DISTANCE, currentRecord.getDistance());

        long ret = db.insert(LocationDBHelper.RECORDS_TABLE, null, recordValues);
        if(ret < 0){
            Log.e(TAG, "insertRecord: insert record failed!");
            return;
        }
        Cursor c = db.rawQuery("select last_insert_rowid() from " + LocationDBHelper.RECORDS_TABLE, null);
        if(c.moveToFirst()){
            currentRecordId = c.getInt(0);
        }else {
            Log.e(TAG, "insertRecord: query record id failed!");
            return;
        }
        db.close();

        LocationBean bean = new LocationBean(currentRecordId, l);
        bean.setRecordTime(currentRecord.getStartTime());
        lastLocationBean = bean;
        insertLocation(bean);

        Log.d(TAG, "insertRecord: success!");
    }

    public void stopRecord(Location l){
        LocationBean bean = new LocationBean(currentRecordId,l);
        insertLocation(bean);

        LocationDBHelper locationDBHelper = new LocationDBHelper(context);
        SQLiteDatabase db = locationDBHelper.getWritableDatabase();
        ContentValues recordValues = new ContentValues();
        recordValues.put(LocationDBHelper.COLUMN_START_TIME, currentRecord.getStartTime().getTime());
        recordValues.put(LocationDBHelper.COLUMN_STOP_TIME, currentRecord.getStopTime().getTime());
        recordValues.put(LocationDBHelper.COLUMN_DISTANCE, currentRecord.getDistance());

        int ret = db.update(LocationDBHelper.RECORDS_TABLE, recordValues,
                "id=?",new String[]{currentRecordId+""});
        if(ret < 0){
            Log.e(TAG, "stopRecord: update record failed!");
            return;
        }
        db.close();

        Log.d(TAG, "stopRecord: success!");
    }

    public void insertLocation(LocationBean bean) {
        LocationDBHelper locationDBHelper = new LocationDBHelper(context);
        SQLiteDatabase db = locationDBHelper.getWritableDatabase();
        currentRecord.setStopTime(bean.getRecordTime());
        double d = calculateDistance(bean, lastLocationBean);
        currentRecord.appendDistance(d);//TODO
        ContentValues values = new ContentValues();
        values.put(LocationDBHelper.COLUMN_RECORD_ID,currentRecordId);
        values.put(LocationDBHelper.COLUMN_LATITUDE, bean.getLatitude());
        values.put(LocationDBHelper.COLUMN_LONGITUDE, bean.getLongitude());
        values.put(LocationDBHelper.COLUMN_RECORD_TIME, bean.getRecordTime().getTime());
        long ret = db.insert(LocationDBHelper.LOCATIONS_TABLE, null, values);
        if(ret < 0){
            Log.e(TAG, "insertRecord: insert location failed!");
            return;
        }
        db.close();
        lastLocationBean = bean;
        Log.d(TAG, "insertLocation: success!");
    }
    public void insertLocation(Location l){
        LocationBean bean = new LocationBean(currentRecordId, l);
        insertLocation(bean);
    }

    private final double EARTH_RADIUS = 6378137.0;

    public double distanceTolastLocation(Location l){
        return calculateDistance(l, lastLocationBean);
    }
    private double calculateDistance(Location l1, Location l2) {
        double lat_a = l1.getLatitude();
        double lng_a = l1.getLongitude();
        double lat_b = l2.getLatitude();
        double lng_b = l2.getLongitude();
        double radLat1 = (lat_a * Math.PI / 180.0);
        double radLat2 = (lat_b * Math.PI / 180.0);
        double a = radLat1 - radLat2;
        double b = (lng_a - lng_b) * Math.PI / 180.0;
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;

        Log.d(TAG, "calculateDistance: s="+s);
        return s;
    }
}
