package com.example.example.ffmpeg;

public class FFmpegCmd {
    /**
     * 加载所有相关链接库
     */
    static {
        System.loadLibrary("native-lib");
        System.loadLibrary("avcodec");
        System.loadLibrary("avfilter");
        System.loadLibrary("avformat");
        System.loadLibrary("avutil");
        System.loadLibrary("swscale");
        System.loadLibrary("fdk-aac");
        System.loadLibrary("swresample");
    }

    private static OnExecListener listener;

    /**
     * 调用底层执行
     *
     * @param argc
     * @param argv
     * @return
     */
    public static native int exec(int argc, String[] argv);

    public static void onExecuted(int ret) {
        if (listener != null) {
            listener.onExecuted(ret);
        }
    }

    /**
     * 执行ffmoeg命令
     *
     * @param cmds
     * @param listener
     */
    public static void exec(String[] cmds, OnExecListener listener) {
        FFmpegCmd.listener = listener;
        exec(cmds.length, cmds);
    }

    /**
     * 执行完成/错误 时的回调接口
     */
    public interface OnExecListener {
        void onExecuted(int ret);
    }
}