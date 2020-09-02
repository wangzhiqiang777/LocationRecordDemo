package com.neusoft.qiangzi.bootapp;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        //启动service
//        Intent i = new Intent();
//        i.setComponent(new ComponentName("com.neusoft.qiangzi.locationrecorddemo",
//                "com.neusoft.qiangzi.locationrecorddemo.service.LocationRecordService"));
//        startForegroundService(i);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentResolver contentResolver = getContentResolver();
                Uri uri = Uri.parse("content://com.neusoft.qiangzi.locationrecorddemo.locationrecordprovider");
                contentResolver.query(uri, null, null, null, null);
            }
        });

        if (ServiceUtil.isServiceRunning(this, "com.neusoft.qiangzi.locationrecorddemo.service.LocationRecordService")) {
            Toast.makeText(this,"服务正在运行。。。",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"服务已关闭！",Toast.LENGTH_SHORT).show();
        }
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
