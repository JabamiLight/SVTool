package com.example.example;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.wuwang.aavt.view.BreakPointView;
import com.wuwang.aavt.av.CameraRecorder2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraRecordActivity extends AppCompatActivity {

    private Button btnRecord;
    private BreakPointView breakPointView;
    private ExecutorService mExecutor;
    private boolean isRecording;
    private boolean isPreviewOpen=false;
    private CameraRecorder2 mCamera;


    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private Runnable captureRunable = new Runnable() {
        @Override
        public void run() {
            mCamera.addData();
            mCamera.startRecord();
            mCamera.pause(false);
        }
    };
    private Runnable taskRunnable=new Runnable() {
        @Override
        public void run() {
            isRecording = true;
            breakPointView.addBean();
            mExecutor.execute(captureRunable);
        }
    };


    private SurfaceView mSurfaceView;
    private String tempPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_record);
        btnRecord = findViewById(R.id.btn_record);
        breakPointView = findViewById(R.id.break_point_view);
        mSurfaceView= (SurfaceView) findViewById(R.id.surface);
        mExecutor = Executors.newCachedThreadPool();
        mCamera =new CameraRecorder2();
        mCamera.setOutputPath(tempPath);
        mCamera.setSectionView(breakPointView);
        btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        handler.postDelayed(taskRunnable,500);
                        break;
                    case MotionEvent.ACTION_UP:
                        isRecording = false;
                        handler.removeCallbacks(taskRunnable);
                        mCamera.pause(true);
                        break;
                }
                return false;
            }
        });


        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                mCamera.open();
                mCamera.setSurface(holder.getSurface());
                mCamera.setPreviewSize(width, height);
                mCamera.startPreview();
                isPreviewOpen=true;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                mCamera.stopPreview();
                mCamera.close();
            }
        });

        mCamera.startPreview();

    }


    public void close(View view) {
        finish();
    }

    public void flash(View view) {
        view.setSelected(!view.isSelected());
    }

    public void switchCamera(View view) {

    }


    public void back(View view) {

    }

    public void done(View view) {
        mCamera.stopRecord();
        Toast.makeText(this, "停止录制", Toast.LENGTH_SHORT).show();
    }
}
