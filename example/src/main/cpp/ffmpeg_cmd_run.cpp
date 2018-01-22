//
// Created by TY on 2018/1/5.
//

#include "logutils.h"

char **argv;
extern "C" {
#include "com_example_example_ffmpeg_FFmpegCmd.h"
#include "jni.h"
#include <stdlib.h>

#include "ffmpeg_thread.h"


static JavaVM *gVm = NULL;
static jobject m_clazz = NULL;//当前类(面向java)


void callJavaMethod(JNIEnv *pEnv, int ret);

void ffmpeg_callback(int ret) {
    JNIEnv * env;
    bool attached = false;
    switch (gVm->GetEnv((void**)&env, JNI_VERSION_1_6))
    {
        case JNI_OK:
            LOGD("已经获取成功");
            break;
        case JNI_EDETACHED:
            if (gVm->AttachCurrentThread(&env, NULL)!=0)
            {
                LOGD("附加线程失败");
            }
            attached = true;
            callJavaMethod(env, ret);
            break;
        case JNI_EVERSION:
            LOGD("无效版本");
            break;

        case JNI_ERR:
            LOGD("获取失败");
            break;
    }
    if (attached)
    {
        gVm->DetachCurrentThread();
    }
    free(argv);
}

void callJavaMethod(JNIEnv *env, int ret) {
    if (m_clazz) {
        jmethodID methodID = env->GetStaticMethodID((jclass) m_clazz, "onExecuted", "(I)V");
        if (!methodID) {
            LOGE("---------------methodID isNULL---------------");
            return;
        }
        //调用该java方法
        env->CallStaticVoidMethod((jclass)m_clazz, methodID, ret);
    }

}
JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved){
    gVm=vm;
    return JNI_VERSION_1_6;
}



JNIEXPORT jint JNICALL Java_com_example_example_ffmpeg_FFmpegCmd_exec
        (JNIEnv *env, jclass jclass, jint cmdnum, jobjectArray cmdline) {

    m_clazz =env->NewGlobalRef(jclass);
    if (cmdline) {
        argv = (char **) malloc(sizeof(char *) * cmdnum);
        int length = env->GetArrayLength(cmdline);
        for (int i = 0; i < length; ++i) {
            jstring str = (jstring) env->GetObjectArrayElement(cmdline, i);
            argv[i] = const_cast<char *>(env->GetStringUTFChars(str, JNI_FALSE));
        }
    }
    //注册ffmpeg命令执行完毕时的回调
    ffmpeg_thread_callback(ffmpeg_callback);
    ffmpeg_thread_run_cmd(cmdnum, argv);


    return 0;

}

}
