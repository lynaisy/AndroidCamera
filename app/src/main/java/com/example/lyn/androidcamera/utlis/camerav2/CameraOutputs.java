package com.example.lyn.androidcamera.utlis.camerav2;

import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.ImageReader;
import android.media.MediaRecorder;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Size;

import com.blankj.utilcode.util.ActivityUtils;
import com.example.lyn.androidcamera.Constants;
import com.example.lyn.androidcamera.view.customviews.AutoFitTextureView;

import java.util.Arrays;
import java.util.Collections;

/**
 * Created by Administrator on 2018/5/19.
 */

public class CameraOutputs {
    private static ImageReader imageReader;
    private static int sensorOrientation;
    private static Size previewSize;
    private static Size videoSize;
    private static String cameraId;
    private static MediaRecorder mediaRecorder;
    private static boolean bFlashSupported;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setUpCameraOutputs(int viewWidth, int viewHeight, CameraManager manager, AutoFitTextureView textureView) {
        String mCameraId = null;
        String[] supportCamerIDs = null;
        try {
            supportCamerIDs = manager.getCameraIdList();
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        if (supportCamerIDs == null || supportCamerIDs.length == 0) {
            return;
        }
        for (String cameraId : supportCamerIDs) {
            if (Constants.SELECT_CAMERA_ID.equals(cameraId)) {
                mCameraId = cameraId;
            }
        }
        if (mCameraId == null) {
            mCameraId = supportCamerIDs[0];
        }
        try {
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(mCameraId);
            StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
            if (map == null) {
                return;
            }
            // For still image captures, we use the largest available size.
            Size largest = Collections.max(Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)), new CompareSizesByArea());
            imageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(), ImageFormat.JPEG, /*maxImages*/2);//初始化ImageReader
            //处理图片方向相关
            sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
            //注：使用过程中发现显示不佳，这里为了最好的预览效果，直接用可预览的最大尺寸！
            previewSize = CameraSizeOption.chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class), viewWidth, viewHeight);
            videoSize = CameraSizeOption.chooseVideoSize(map.getOutputSizes(MediaRecorder.class));
            mediaRecorder = new MediaRecorder();
            // We fit the aspect ratio of TextureView to the size of preview we picked.
            int orientation = ActivityUtils.getTopActivity().getResources().getConfiguration().orientation;
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                textureView.setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
            } else {
                textureView.setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
            }
            // 设置是否支持闪光灯
            Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            bFlashSupported = available == null ? false : available;
            CameraOutputs.cameraId = mCameraId;
        } catch (CameraAccessException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public static ImageReader getImageReader() {
        return imageReader;
    }


    public static int getSensorOrientation() {
        return sensorOrientation;
    }


    public static Size getPreviewSize() {
        return previewSize;
    }


    public static Size getVideoSize() {
        return videoSize;
    }


    public static String getCameraId() {
        return cameraId;
    }


    public static MediaRecorder getMediaRecorder() {
        return mediaRecorder;
    }


    public static boolean isbFlashSupported() {
        return bFlashSupported;
    }


}
