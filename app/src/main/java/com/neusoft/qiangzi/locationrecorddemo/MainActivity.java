package com.neusoft.qiangzi.locationrecorddemo;

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
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.neusoft.qiangzi.locationrecorddemo.service.LocationRecordService;

public class MainActivity extends AppCompatActivity implements ServiceConnection {

    private static final String TAG = "MainActivity";
    private ILocationAidlInterface aidlInterface;
    private TextView textView;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.tvLoactionMsg);

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
        findViewById(R.id.buttonGetLocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Location location = aidlInterface.getLocation();
                    textView.setText("经度："+location.getLongitude()+
                            "纬度："+location.getLatitude());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        Switch sw = findViewById(R.id.switchRecordEnable);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                try {
                    aidlInterface.setRecordLocationEnabled(b);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: is called");
        Intent serviceIntern = new Intent(this, LocationRecordService.class);
//        startService(serviceIntern);
        bindService(serviceIntern, this, BIND_AUTO_CREATE);
    }


    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: is called");
        unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        aidlInterface = (ILocationAidlInterface) iBinder;
        Log.d(TAG, "onServiceConnected: "+componentName);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

        Log.d(TAG, "onServiceDisconnected: "+componentName);
    }
}