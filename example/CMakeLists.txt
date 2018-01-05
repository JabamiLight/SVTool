
cmake_minimum_required(VERSION 3.4.1)


add_library(
             native-lib

             SHARED

              src/main/cpp/cmdutils.c
              src/main/cpp/ffmpeg.c
              src/main/cpp/ffmpeg_filter.c
              src/main/cpp/ffmpeg_opt.c
              src/main/cpp/ffmpeg_cmd_run.cpp

             )
find_library(
              log-lib

              log )

include_directories(libs/include)
set(DIR ../../../../libs)

add_library(avcodec
            SHARED
            IMPORTED)
set_target_properties(avcodec
                      PROPERTIES IMPORTED_LOCATION
                      ${DIR}/armeabi/libavcodec.so)
add_library(avfilter
            SHARED
            IMPORTED)
set_target_properties(avfilter
                        PROPERTIES IMPORTED_LOCATION
                        ${DIR}/armeabi/libavfilter.so)
add_library(avformat
            SHARED
            IMPORTED)
set_target_properties(avformat
                      PROPERTIES IMPORTED_LOCATION
                      ${DIR}/armeabi/libavformat.so)

add_library(avutil
            SHARED
            IMPORTED)
set_target_properties(avutil
                      PROPERTIES IMPORTED_LOCATION
                      ${DIR}/armeabi/libavutil.so)


add_library(libfdk-aac
            SHARED
            IMPORTED)
set_target_properties(libfdk-aac
                      PROPERTIES IMPORTED_LOCATION
                      ${DIR}/armeabi/libfdk-aac.so)

add_library(swresample
            SHARED
            IMPORTED)
set_target_properties(swresample
                      PROPERTIES IMPORTED_LOCATION
                      ${DIR}/armeabi/libswresample.so)
add_library(swscale
            SHARED
            IMPORTED)
set_target_properties(swscale
                      PROPERTIES IMPORTED_LOCATION
                       ${DIR}/armeabi/libswscale.so)


target_link_libraries( native-lib
                       avcodec
                       avfilter
                       avformat
                       avutil
                       libfdk-aac
                       swresample
                       swscale
                       ${log-lib} )