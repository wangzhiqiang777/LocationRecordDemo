package com.neusoft.qiangzi.locationrecorddemo.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.neusoft.qiangzi.locationrecorddemo.database.LocationDBHelper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LocationRecordProvider extends ContentProvider {
    private static final String TAG = "LocationRecordProvider";
    private static final int CORD_RECORD_ALL = 1;
    private static final int CORD_RECORD_ONE = 2;
    private static final int CORD_LOCATION_ALL = 3;
    private static final int CORD_LOCATION_ONE = 4;
    private static UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        uriMatcher.addURI("com.neusoft.qiangzi.locationrecorddemo.locationrecordprovider", "/record", CORD_RECORD_ALL);
        uriMatcher.addURI("com.neusoft.qiangzi.locationrecorddemo.locationrecordprovider", "/record/#", CORD_RECORD_ONE);
        uriMatcher.addURI("com.neusoft.qiangzi.locationrecorddemo.locationrecordprovider", "/location", CORD_LOCATION_ALL);
        uriMatcher.addURI("com.neusoft.qiangzi.locationrecorddemo.locationrecordprovider", "/location/#", CORD_LOCATION_ONE);
    }
    @Override
    public boolean onCreate() {
        Log.d(TAG, "onCreate: is called.");
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        Log.d(TAG, "query: is called");
        int code = uriMatcher.match(uri);
        LocationDBHelper locationDBHelper = new LocationDBHelper(getContext());
        SQLiteDatabase db = locationDBHelper.getReadableDatabase();
        Cursor c = null;
        switch (code) {
            case CORD_RECORD_ALL:
                Log.d(TAG, "query: CORD_RECORD_ALL");
                c = db.query(LocationDBHelper.RECORDS_TABLE, strings, s, strings1, s1, null, null);
                break;
            case CORD_RECORD_ONE:
                Log.d(TAG, "query: CORD_RECORD_ONE");
                break;
            case CORD_LOCATION_ALL:
                Log.d(TAG, "query: CORD_LOCATION_ALL");
                break;
            case CORD_LOCATION_ONE:
                Log.d(TAG, "query: CORD_LOCATION_ONE");
                break;
        }
        db.close();
        return c;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        Log.d(TAG, "insert: is called");
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        Log.d(TAG, "delete: is called.");
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        Log.d(TAG, "update: is called.");
        return 0;
    }
}
