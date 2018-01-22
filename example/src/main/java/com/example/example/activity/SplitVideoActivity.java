package com.example.example.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.example.R;
import com.example.example.ffmpeg.FFmpegCmd;
import com.wuwang.aavt.utils.VideoClip;
import com.wuwang.aavt.view.VideoSeekBar;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class SplitVideoActivity extends AppCompatActivity {


    private VideoSeekBar videoSeekBar;

    private String videoPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cut.mp4";

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(SplitVideoActivity.this, "剪辑成功", Toast.LENGTH_SHORT).show();
        }
    };
    private ProgressDialog show;

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
    private SimpleDateFormat format=new SimpleDateFormat("HH:mm:ss");

    public void spilitFfmepg(View view) {
        final String path = Environment.getExternalStorageDirectory().getPath();
        format.setTimeZone(TimeZone.getTimeZone("GMT+0"));
        String startTime=format.format(new Date((long) videoSeekBar.getStartTime()));
        String duration=format.format(new Date((long)(videoSeekBar.getEndTime()-videoSeekBar.getStartTime())));
        //原命令 ffmpeg -i input.mp3 -ss hh:mm:ss -t hh:mm:ss -acodec copy output.mp3
        //flac有点问题,  支持mp3 wma m4a
        String cmd = "ffmpeg -i " + path + "/Cut.mp4" + " -ss "+startTime+" -t "+duration+" -c:v copy -c:a copy -y " + path +
                "/Cut_out.mp4";
        toExec(cmd);
    }

    private void toExec(String cmd) {
        show = ProgressDialog.show(this, null, "执行中...", true);
        //转换为数组
        String[] cmds = cmd.split(" ");
        FFmpegCmd.exec(cmds, new FFmpegCmd.OnExecListener() {
            @Override
            public void onExecuted(final int ret) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SplitVideoActivity.this, "执行完成=" + ret, Toast.LENGTH_SHORT).show();
                        show.dismiss();
                    }
                });
            }
        });
    }
}
