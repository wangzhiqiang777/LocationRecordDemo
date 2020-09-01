package com.neusoft.qiangzi.locationrecorddemo.service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.neusoft.qiangzi.locationrecorddemo.ILocationAidlInterface;
import com.neusoft.qiangzi.locationrecorddemo.LocationModel;
import com.neusoft.qiangzi.locationrecorddemo.MainActivity;
import com.neusoft.qiangzi.locationrecorddemo.R;

import androidx.core.app.NotificationCompat;

public class LocationRecordService extends Service {
    private static final String TAG = "LocationRecordService";
    Thread locationThread = null;
    LocationManager locaManager = null;
    private boolean locationThreadisRun;
    private LocationModel locationModel;

    public LocationRecordService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "onStartCommand: is called");
        NotificationChannel notificationChannel = null;
        String CHANNEL_ID = "com.neusoft.qiangzi.locationrecorddemo";
        String CHANNEL_NAME = "TEST";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
        Notification notification = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("位置记录")
                .setContentText("正在记录位置信息。。。")
                .setWhen(System.currentTimeMillis())
                .build();
        startForeground(1,notification);

        return START_STICKY;
    }

    private Location currentLocation;
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: is called");
        if (locaManager==null) return null;

        return new ILocationAidlInterface.Stub() {

            @Override
            public Location getLocation() throws RemoteException {
                return currentLocation;
            }

            @Override
            public void setRecordLocationEnabled(boolean enabled) throws RemoteException {
                if(isRecordLocationEnabled == enabled)return;
                isRecordLocationEnabled = enabled;
                if(enabled){
                    locationModel.startRecord(currentLocation);
                }else {
                    locationModel.stopRecord(currentLocation);
                }
            }
        };
    }

    private boolean isRecordLocationEnabled;
    private void setLocationManager() {
        if (locaManager == null) {
            Log.d(TAG, "setLocationManager: is called.");
            locaManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                                != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "setLocationManager: no permisson!");
                    return;
                }
            }

            locaManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 500, 0.1f,
                    new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            Log.d(TAG, "onLocationChanged: Longtitude="
                                    + location.getLongitude() + ",Latitude=" + location.getLatitude());
                            currentLocation = location;
                            if(isRecordLocationEnabled){
                                if (locationModel.distanceTolastLocation(location) > 1) {
                                    locationModel.insertLocation(location);
                                }
                            }
                        }

                        @Override
                        public void onStatusChanged(String s, int i, Bundle bundle) {

                        }

                        @Override
                        public void onProviderEnabled(String s) {
                            Log.d(TAG, "onProviderEnabled: provider=" + s);
                        }

                        @Override
                        public void onProviderDisabled(String s) {
                            Log.d(TAG, "onProviderDisabled: provider=" + s);
                        }
                    });
            currentLocation = locaManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(currentLocation!=null)
                Log.d(TAG, "setLocationManager: l="+currentLocation.toString());
            else Log.e(TAG, "setLocationManager: can not get location!!!");
        }
    }

    private void startTimingThread() {
        if (locationThread == null) {
            locationThread = new Thread() {

                @Override
                public void run() {
                    super.run();
                    locationThreadisRun = true;
                    while (locationThreadisRun) {
                        if (locaManager != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                    Log.e(TAG, "getLocation: have no permission!");
                                }
                            }
                            Location location = locaManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if(location!=null){
                                Log.d(TAG, "run: Longitude="+location.getLongitude()+
                                        ", Latitude="+location.getLatitude());
                            }else {
                                Log.e(TAG, "run: location is null");
                            }

                        }

                        try {
                            sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            locationThread.start();
        }
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        super.unbindService(conn);
        Log.d(TAG, "unbindService: is called");

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: is called");
//        startForeground(1,new Notification());
        setLocationManager();
//        startTimingThread();
        locationModel = new LocationModel(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: is called");
        locationThreadisRun = false;
        if(locationThread!=null){
            try {
                locationThread.join(5000);
                Log.d(TAG, "onDestroy: thread is stopped!");
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "onDestroy: thread can not stop! something is wrong.");
            }
        }

        stopForeground(true);
    }
}
