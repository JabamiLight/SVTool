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
package com.wuwang.aavt.media;

import android.graphics.Point;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;

import com.wuwang.aavt.log.AvLog;

import java.io.IOException;
import java.util.concurrent.Semaphore;

/**
 * CameraProvider 相机数据
 *
 * @author wuwang
 * @version v1.0 2017:10:26 18:09
 */
public class CameraProvider implements ITextureProvider {

    private Camera mCamera;
    private int cameraId = 0;
    private Semaphore mFrameSem;
    private String tag = getClass().getSimpleName();

    @Override
    public Point open(final SurfaceTexture surface) {
        final Point size = new Point();
        try {
            mFrameSem = new Semaphore(0);
            mCamera = Camera.open(cameraId);
            mCamera.setPreviewTexture(surface);
            //设置帧数可获取的接口
            surface.setOnFrameAvailableListener(frameListener);
            Camera.Size s = mCamera.getParameters().getPreviewSize();
            Camera.Parameters para = mCamera.getParameters();
            para.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            para.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            mCamera.setParameters(para);
            mCamera.startPreview();
            size.x = s.height;
            size.y = s.width;
            AvLog.i(tag, "Camera Opened");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return size;
    }

    @Override
    public void switchCamera() {
        cameraId = cameraId == 1 ? 0 : 1;
    }

    @Override
    public void close() {
        mFrameSem.drainPermits();
        mFrameSem.release();

        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    @Override
    public boolean frame() {
        try {
            mFrameSem.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public long getTimeStamp() {
        return -1;
    }

    @Override
    public boolean isLandscape() {
        return true;
    }

    private SurfaceTexture.OnFrameAvailableListener frameListener = new SurfaceTexture
            .OnFrameAvailableListener() {

        @Override
        public void onFrameAvailable(SurfaceTexture surfaceTexture) {
            mFrameSem.drainPermits();
            mFrameSem.release();
        }

    };


    public int getCameraId() {
        return cameraId;
    }

    public void setFlash(boolean flash) {
        Camera.Parameters para = mCamera.getParameters();
        if (flash)
            para.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        else
            para.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCamera.setParameters(para);
    }
}
