package com.example.example;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.wuwang.aavt.utils.VideoClip;
import com.wuwang.aavt.view.VideoSeekBar;

public class SplitVideoActivity extends AppCompatActivity {


    private VideoSeekBar videoSeekBar;

    private String videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cut.mp4";

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(SplitVideoActivity.this, "剪辑成功", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_video);
        videoSeekBar = findViewById(R.id.video_seekbar);
        videoSeekBar.setVideoUri(true, videoPath);
        videoSeekBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
    }

    public void spilit(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                new VideoClip().clipVideo(videoPath, (long) videoSeekBar.getStartTime() * 1000, (long)
                        (videoSeekBar.getEndTime() * 1000 - videoSeekBar.getStartTime() * 1000));
                handler.sendEmptyMessage(0);
            }
        }).start();
    }
}
