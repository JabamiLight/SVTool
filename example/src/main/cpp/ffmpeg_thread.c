//
// Created by TY on 2018/1/8.
//


#include "ffmpeg_thread.h"
pthread_t ntid;
char **argvs = NULL;
int num = 0;
void *thread(void *arg) {
    int result = ffmpeg_exec(num, argvs);
//    ffmpeg_thread_exit(0);
    return (void *) 0;
}

int ffmpeg_thread_run_cmd(int cmdnum, char **argv) {
    num = cmdnum;
    argvs = argv;
    int temp = pthread_create(&ntid, NULL, thread, NULL);
    if (temp != 0) {
        //LOGE("can't create thread: %s ",strerror(temp));
        return 1;
    }
    return 0;
}

static void (*ffmpeg_callback)(int ret);

/**
 * 注册线程回调
 */
void ffmpeg_thread_callback(void (*cb)(int ret)) {
    ffmpeg_callback = cb;
}

void ffmpeg_thread_exit(int ret) {
    if (ffmpeg_callback) {
        ffmpeg_callback(ret);
    }
    pthread_exit((void *) "ffmpeg_thread_exit");

}