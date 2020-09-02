package com.neusoft.qiangzi.locationrecorddemo.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.neusoft.qiangzi.locationrecorddemo.ILocationAidlInterface;
import com.neusoft.qiangzi.locationrecorddemo.R;
import com.neusoft.qiangzi.locationrecorddemo.service.LocationRecordService;
import com.neusoft.qiangzi.locationrecorddemo.utils.ServiceUtil;

public class MainActivity extends AppCompatActivity implements ServiceConnection, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "MainActivity";
    private ILocationAidlInterface binder;
    private TextView textView;
    Intent serviceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tvLoactionMsg);
        serviceIntent = new Intent(MainActivity.this, LocationRecordService.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},0);
                return;
            }
        }

        Switch sw = findViewById(R.id.switchRecordEnable);
        sw.setOnCheckedChangeListener(this);

        if (ServiceUtil.isServiceRunning(this, LocationRecordService.SERVICE_NAME)) {
            Log.d(TAG, "onCreate: isServiceRunning = true.");
            sw.setChecked(true);
            bindService(serviceIntent, MainActivity.this, BIND_AUTO_CREATE);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: is called");
        if(binder !=null) {
            unbindService(this);
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        Log.d(TAG, "onServiceConnected: is called");
        binder = (ILocationAidlInterface) iBinder;
        try {
            binder.setRecordLocationEnabled(true);
            Location location = binder.getLocation();
            textView.setText("经度："+location.getLongitude()+
                    "纬度："+location.getLatitude());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        Log.d(TAG, "onServiceDisconnected: "+componentName);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(b) {
            startForegroundService(serviceIntent);
            bindService(serviceIntent, MainActivity.this, BIND_AUTO_CREATE);
        } else {
            try {
                binder.setRecordLocationEnabled(false);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            unbindService(MainActivity.this);
            binder = null;
            stopService(serviceIntent);
        }
    }
}