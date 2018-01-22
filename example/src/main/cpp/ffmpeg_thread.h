//
// Created by TY on 2018/1/8.
//

#ifndef SVTOOL_FFMPEG_THREAD_H
#define SVTOOL_FFMPEG_THREAD_H

#include "libavcodec/avcodec.h"
#include "libavformat/avformat.h"
#include "libswscale/swscale.h"
#include "ffmpeg.h"
#include <pthread.h>
#include <string.h>

int ffmpeg_thread_run_cmd(int cmdnum,char **argv);

void ffmpeg_thread_exit(int ret);

void ffmpeg_thread_callback(void (*cb)(int ret));



#endif //SVTOOL_FFMPEG_THREAD_H
