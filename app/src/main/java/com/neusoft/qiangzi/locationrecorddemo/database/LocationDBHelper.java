package com.neusoft.qiangzi.locationrecorddemo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class LocationDBHelper extends SQLiteOpenHelper {

    public LocationDBHelper(@Nullable Context context) {
        super(context, "location_record.db", null, 1);
    }

    public static final String RECORDS_TABLE = "records_table";
    public static final String LOCATIONS_TABLE = "locations_table";
    public static final String COLUMN_START_TIME = "start_time";
    public static final String COLUMN_STOP_TIME = "stop_time";
    public static final String COLUMN_DISTANCE = "distance";
    public static final String COLUMN_RECORD_ID = "record_id";
    public static final String COLUMN_LATITUDE = "latitude";
    public static final String COLUMN_LONGITUDE = "longitude";
    public static final String COLUMN_RECORD_TIME = "record_time";


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        sqLiteDatabase.execSQL("create table if not exists "+RECORDS_TABLE+"(" +
                "id integer primary key autoincrement," +
                COLUMN_START_TIME+" time," +
                COLUMN_STOP_TIME+" time," +
                COLUMN_DISTANCE+" double)");
        sqLiteDatabase.execSQL("create table if not exists "+LOCATIONS_TABLE+"(" +
                "id integer primary key autoincrement," +
                COLUMN_RECORD_ID+" integer,"+
                COLUMN_LATITUDE +" double," +
                COLUMN_LONGITUDE+" double," +
                COLUMN_RECORD_TIME+" time," +
                "FOREIGN KEY ("+COLUMN_RECORD_ID+") REFERENCES "+RECORDS_TABLE+"(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
