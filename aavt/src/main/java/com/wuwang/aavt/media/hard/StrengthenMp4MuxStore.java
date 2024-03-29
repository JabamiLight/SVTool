/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.wuwang.aavt.media.hard;

import android.annotation.TargetApi;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.util.Log;

import com.wuwang.aavt.log.AvLog;
import com.wuwang.aavt.media.av.AvException;
import com.wuwang.aavt.view.BreakPointView;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * StrengthenMp4MuxStore
 *
 * @author wuwang
 * @version v1.0 2017:11:08 17:15
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class StrengthenMp4MuxStore implements IHardStore {

    private final String tag = getClass().getSimpleName();
    private MediaMuxer mMuxer;
    private final boolean av;
    private String path;
    private int audioTrack = -1;
    private int videoTrack = -1;
    private final Object Lock = new Object();
    private boolean muxStarted = false;
    private ArrayDeque<LinkedList<HardMediaData>> videoCache, audioCache;
    private Recycler<HardMediaData> recycler;
    private ExecutorService exec;
    private BreakPointView sectionView;

    public StrengthenMp4MuxStore(boolean av) {
        this.av = av;
        videoCache = new ArrayDeque<>(30);
        audioCache = new ArrayDeque<>(30);
        recycler = new Recycler<>();
        exec = new ThreadPoolExecutor(1, 1, 1, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(16), Executors.defaultThreadFactory());
    }

    @Override
    public void close() throws AvException {
        synchronized (Lock) {
            if (muxStarted) {
                audioTrack = -1;
                videoTrack = -1;
                muxStarted = false;
                mMuxer.stop();
                AvLog.d(tag, "muxer stoped success");
                mMuxer.release();
            }
        }
    }

//    private void muxRun() {
//        AvLog.d(tag, "enter mux loop");
//        while (muxStarted) {
//            try {
//                HardMediaData data = cache.poll(1, TimeUnit.SECONDS);
//                synchronized (Lock) {
//                    AvLog.d(tag, "data is null?" + (data == null));
//                    if (muxStarted && data != null) {
//                        mMuxer.writeSampleData(data.index, data.data, data.info);
//                        recycler.put(data.index, data);
//                    }
//                }
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            mMuxer.stop();
//            AvLog.d(tag, "muxer stoped success");
//            mMuxer.release();
//        } catch (IllegalStateException e) {
//            e.printStackTrace();
//            AvLog.e("stop muxer failed!!!");
//        }
//        mMuxer = null;
//        cache.clear();
//        recycler.clear();
//    }

    @Override
    public int addTrack(MediaFormat mediaFormat) {
        int ret = -1;
        synchronized (Lock) {
            if (!muxStarted) {
                if (audioTrack == -1 && videoTrack == -1) {
                    try {
                        mMuxer = new MediaMuxer(path, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                    } catch (IOException e) {
                        e.printStackTrace();
                        AvLog.e("create MediaMuxer failed:" + e.getMessage());
                    }
                }
                String mime = mediaFormat.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio")) {
                    audioTrack = mMuxer.addTrack(mediaFormat);
                    ret = audioTrack;
                } else if (mime.startsWith("video")) {
                    videoTrack = mMuxer.addTrack(mediaFormat);
                    ret = videoTrack;
                }
                startMux();
            } else {
                if (audioTrack != -1 && videoTrack != -1) {
                    return videoTrack;
                }
            }
        }
        return ret;
    }

    private void startMux() {
        boolean canMux = !av || (audioTrack != -1 && videoTrack != -1);
        if (canMux) {
            mMuxer.start();
            muxStarted = true;
//            exec.execute(new Runnable() {
//                @Override
//                public void run() {
//                    muxRun();
//                }
//            });
        }
    }

    @Override
    public int addData(int track, HardMediaData hardMediaData) {
        if (track >= 0) {
//            AvLog.d(tag, "addData->" + track + "/" + audioTrack + "/" + videoTrack);
            Log.e("tedu", "addData: track" + track + "   videoTrack" + videoTrack);
            hardMediaData.index = track;
            if (track == audioTrack || track == videoTrack) {
//                HardMediaData d = recycler.poll(track);
//                if (d == null) {
//                    d = hardMediaData.copy();
//                } else {
//                    hardMediaData.copyTo(d);
//                }
                HardMediaData d = hardMediaData.copy();
                synchronized (Lock) {
                    if (track == audioTrack) {
                        try {
                        List<HardMediaData> dataList = audioCache.getLast();
                        if (d.info.presentationTimeUs >= currentAudioTime())
                            dataList.add(d);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {

                        try {
                            List<HardMediaData> dataList = videoCache.getLast();
                            if (d.info.presentationTimeUs >= currentVideoTime())
                                dataList.add(d);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    if (sectionView != null) {
                        sectionView.setRenderBeans(audioCache);
                    }

                }
//                while (!cache.offer(d)) {
//                    AvLog.d(tag, "put data to the cache : poll");
//                    HardMediaData c = cache.poll();
//                    recycler.put(c.index, c);
//                }

            }
        }
        return 0;
    }

    @Override
    public void setOutputPath(String path) {
        this.path = path;
    }

    @Override
    public void writeToMux() {
        while (!audioCache.isEmpty()) {
            List<HardMediaData> dataList = audioCache.poll();
            for (HardMediaData data : dataList) {
                mMuxer.writeSampleData(data.index, data.data, data.info);
            }
        }
        while (!videoCache.isEmpty()) {
            List<HardMediaData> dataList = videoCache.poll();
            for (HardMediaData data : dataList) {
                mMuxer.writeSampleData(data.index, data.data, data.info);
            }
        }
    }

    @Override
    public void addSection() {
        synchronized (Lock) {
            videoCache.add(new LinkedList<HardMediaData>());
            audioCache.add(new LinkedList<HardMediaData>());
        }
    }

    @Override
    public Queue<List<HardMediaData>> getDataQueue() {
        return null;
    }

    @Override
    public void setSectionView(BreakPointView sectionView) {
        this.sectionView = sectionView;
    }

    @Override
    public long currentVideoTime() {
        try {
            if (videoCache.getLast().getLast() == null) return 0;

        } catch (Exception e) {
            return 0;
        }
        return videoCache.getLast().getLast().info.presentationTimeUs;
    }

    @Override
    public long currentAudioTime() {
        try {
            if (audioCache.getLast().getLast() == null) return 0;
        } catch (Exception e) {
            return 0;
        }
        return audioCache.getLast().getLast().info.presentationTimeUs;
    }
}
