package com.example.example.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.example.PermissionAsker;
import com.example.example.R;

public class MainActivity extends AppCompatActivity {
    private PermissionAsker mAsker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAsker = new PermissionAsker(10, new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_main);
            }
        }, new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, "必要权限被拒绝，应用退出",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }).askPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO

        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[]
            grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        mAsker.onRequestPermissionsResult(grantResults);
    }

    public void breakPoint(View view) {
        startActivity(new Intent(this, CameraRecordActivity.class));
    }


    public void splitVideo(View view) {
        startActivity(new Intent(this, SplitVideoActivity.class));
    }

    public void combineVideo(View view) {
        startActivity(new Intent(this, FfmpegCmdActivity.class));

    }
}
