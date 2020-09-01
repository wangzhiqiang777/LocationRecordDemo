package com.neusoft.qiangzi.bootapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setComponent(new ComponentName("com.neusoft.qiangzi.locationrecorddemo",
                        "com.neusoft.qiangzi.locationrecorddemo.MainActivity"));
                startActivity(i);
            }
        });

        //启动service
        Intent i = new Intent();
        i.setComponent(new ComponentName("com.neusoft.qiangzi.locationrecorddemo",
                "com.neusoft.qiangzi.locationrecorddemo.LocationRecordService"));
        startForegroundService(i);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
