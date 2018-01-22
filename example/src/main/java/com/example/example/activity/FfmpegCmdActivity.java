package com.example.example.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.example.R;
import com.example.example.ffmpeg.FFmpegCmd;

public class FfmpegCmdActivity extends AppCompatActivity {

    private ProgressDialog show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_split_videoffmpeg);
    }

    public void combineVideo(View view) {
        final String path = Environment.getExternalStorageDirectory().getPath();
        String path1 = path + "/Cut.mp4";
        String path2 = path + "/Cut_out.mp4";
//        String cmd = "ffmpeg -i "+path1+" -i "+path2+" -map 0 -c copy -y tt.mp4";
        String cmd = "ffmpeg -f concat -safe 0 -i " + path + "/filelist.txt -c copy -y " + path + "/output" +
                ".mp4";
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
                        Toast.makeText(FfmpegCmdActivity.this, "执行完成=" + ret, Toast.LENGTH_SHORT).show();
                        show.dismiss();
                    }
                });
            }
        });
    }

    /**
     * 转码命令
     *
     * @param view
     */
    public void transCodeVideo(View view) {
        final String path = Environment.getExternalStorageDirectory().getPath();
        String cmd = "ffmpeg -i " + path + "/output.mp4 -acodec copy -vcodec copy -f flv -y " + path +
                "/transcode.flv";
        toExec(cmd);
    }


    public void getPic(View view) {
        final String path = Environment.getExternalStorageDirectory().getPath();
        String cmd = "ffmpeg -i " + path + "/output.mp4 -y -f image2 -ss 8 -t 0.01 -s 100x100 " + path +
                "/test.jpg";
        toExec(cmd);
    }


    /**
     * 添加水印
     *
     * @param view
     */
    public void addWaterMark(View view) {
        final String path = Environment.getExternalStorageDirectory().getPath();
        String cmd = "ffmpeg -i " + path + "/diaosi.mov -i " + path + "/test.jpg -filter_complex " +
                "overlay=W/2:H/2 -y " + path + "/diaosifilter.mov";
        toExec(cmd);
    }


    /**
     * 获取视频gif
     *
     * @param view
     */
    public void getGif(View view) {
        final String path = Environment.getExternalStorageDirectory().getPath();
        String cmd = "ffmpeg -i " + path + "/output.mp4 -y -ss 10 -t 2 -f gif -r 1 -s 320*240 " + path +
                "/test.gif";
        toExec(cmd);
    }

    public void addGifFilter(View view) {
        final String path = Environment.getExternalStorageDirectory().getPath();
        String cmd = "ffmpeg -threads 2 -i " + path + "/diaosi.mov -ignore_loop 0 -i " + path + "/test.gif " +
                "-filter_complex overlay=shortest=1 -y " + path + "/diaosi_filter.mov";
        toExec(cmd);
    }

    public void compressVideo(View view) {
        final String path = Environment.getExternalStorageDirectory().getPath();
        String cmd = "ffmpeg -i "+path+"/diaosi.mov " +
                "-y " +
                "-c:v libx264 " +
                "-c:a aac " +
                "-vf scale=480:-2 " +
                "-preset ultrafast " +
                "-crf 28 " +
                "-b:a 128k " +
                path+"/diaoCompress.mov";
        toExec(cmd);
    }
}
