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

import android.media.MediaFormat;

import com.wuwang.aavt.media.av.IStore;
import com.wuwang.aavt.view.BreakPointView;

import java.util.List;
import java.util.Queue;

/**
 * IHardStore 硬编码的媒体文件存储器
 *
 * @author wuwang
 * @version v1.0 2017:10:28 16:52
 */
public interface IHardStore extends IStore<MediaFormat,HardMediaData> {

    /**
     * 设置存储路径
     * @param path 路径
     */
    void setOutputPath(String path);


    void writeToMux();
    void addSection();

    Queue<List<HardMediaData>> getDataQueue();

    void setSectionView(BreakPointView sectionView);

    long currentVideoTime();
    long currentAudioTime();



}
