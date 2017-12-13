package com.example.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import com.example.example.view.BreakPointView;
import com.example.ty.svtoolib.data.RenderBean;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CameraRecordActivity extends AppCompatActivity {

    private Button btnRecord;
    private BreakPointView breakPointView;
    private List<RenderBean> renderBeanList;
    private ExecutorService mExecutor;
    private boolean isRecording;
    private Runnable captureRunable = new Runnable() {
        @Override
        public void run() {
            while (isRecording) {
                breakPointView.addTime();
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_record);
        btnRecord = findViewById(R.id.btn_record);
        breakPointView = findViewById(R.id.break_point_view);
        mExecutor = Executors.newCachedThreadPool();
        btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isRecording = true;

                        breakPointView.addBean();
                        mExecutor.execute(captureRunable);
                        break;
                    case MotionEvent.ACTION_UP:
                        isRecording = false;
                        break;

                }
                return false;

            }
        });
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

    }
}
