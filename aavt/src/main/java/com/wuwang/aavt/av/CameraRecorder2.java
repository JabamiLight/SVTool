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
package com.wuwang.aavt.av;

import com.wuwang.aavt.core.Renderer;
import com.wuwang.aavt.media.CameraProvider;
import com.wuwang.aavt.media.SoundRecorder;
import com.wuwang.aavt.media.SurfaceEncoder;
import com.wuwang.aavt.media.SurfaceShower;
import com.wuwang.aavt.media.VideoSurfaceProcessor;
import com.wuwang.aavt.media.av.AvException;
import com.wuwang.aavt.media.hard.HardMediaData;
import com.wuwang.aavt.media.hard.IHardStore;
import com.wuwang.aavt.media.hard.StrengthenMp4MuxStore;
import com.wuwang.aavt.view.BreakPointView;

import java.util.List;
import java.util.Queue;

/**
 * CameraRecorder2 相机预览及录制工具类
 *
 * @author wuwang
 * @version v1.0 2017:10:26 18:35
 */
public class CameraRecorder2 {

    private VideoSurfaceProcessor mTextureProcessor;
    private CameraProvider mCameraProvider;
    private SurfaceShower mShower;
    private SurfaceEncoder mSurfaceStore;
    private IHardStore mMuxer;

    private SoundRecorder mSoundRecord;

    public CameraRecorder2(){
        //用于视频混流和存储
        mMuxer=new StrengthenMp4MuxStore(true);

        //用于预览图像
        mShower=new SurfaceShower();
        mShower.setOutputSize(720,1280);

        //用于编码图像
        mSurfaceStore=new SurfaceEncoder();
        mSurfaceStore.setStore(mMuxer);

        //用于音频
        mSoundRecord=new SoundRecorder(mMuxer);

        //用于处理视频图像
        mTextureProcessor=new VideoSurfaceProcessor();
        mTextureProcessor.setTextureProvider(mCameraProvider=new CameraProvider());
        mTextureProcessor.addObserver(mShower);
        mTextureProcessor.addObserver(mSurfaceStore);
    }

    public void setRenderer(Renderer renderer){
        mTextureProcessor.setRenderer(renderer);
    }

    /**
     * 设置预览对象，必须是{@link android.view.Surface}、{@link android.graphics.SurfaceTexture}或者
     * {@link android.view.TextureView}
     * @param surface 预览对象
     */
    public void setSurface(Object surface){
        mShower.setSurface(surface);
    }

    /**
     * 设置录制的输出路径
     * @param path 输出路径
     */
    public void setOutputPath(String path){
        mMuxer.setOutputPath(path);
    }

    /**
     * 设置预览大小
     * @param width 预览区域宽度
     * @param height 预览区域高度
     */
    public void setPreviewSize(int width,int height){
        mShower.setOutputSize(width,height);
    }

    /**
     * 打开数据源
     */
    public void open(){
        mTextureProcessor.start();
    }

    /**
     * 关闭数据源
     */
    public void close(){
        mTextureProcessor.stop();
        stopRecord();
    }

    /**
     * 打开预览
     */
    public void startPreview(){
        mShower.open();
    }

    /**
     * 关闭预览
     */
    public void stopPreview(){
        mShower.close();
    }

    /**
     * 开始录制
     */
    public void startRecord(){
        mSurfaceStore.open();
        mSoundRecord.start();
    }

    /**
     * 关闭录制
     */
    public void stopRecord(){
        mSoundRecord.stop();
        mSurfaceStore.close();
        try {
            mMuxer.writeToMux();
            mMuxer.close();
        } catch (AvException e) {
            e.printStackTrace();
        }
    }

    public void pause(boolean isPause){
        mSurfaceStore.setPause(isPause);
        mSoundRecord.setPause(isPause);
    }

    public void addData(){
        mMuxer.addSection();
    }
    public Queue<List<HardMediaData>> getData(){
        return mMuxer.getDataQueue();
    }




    public void setSectionView(BreakPointView sectionView) {
        mMuxer.setSectionView(sectionView);
    }

    public void switchCarmer() {
        mTextureProcessor.stop();
        mCameraProvider.switchCamera();
        mShower.setCameraId(mCameraProvider.getCameraId());
        mSurfaceStore.setCameraId(mCameraProvider.getCameraId());
        mTextureProcessor.start();
    }


    public void setFlash(boolean flash) {
        mCameraProvider.setFlash(flash);
    }
}
