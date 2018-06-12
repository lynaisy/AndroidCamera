package com.example.lyn.androidcamera.camera1;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileUtils;
import com.example.lyn.androidcamera.Constants;
import com.example.lyn.androidcamera.camera1.utils.ParameterUtil;
import com.example.lyn.androidcamera.commonutlis.SavePictureUtil;
import com.example.lyn.androidcamera.view.AutoFitSurfaceView;
import com.example.lyn.androidcamera.view.AutoFitTextureView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/6/6.
 */

public class Camera1Helper {
    private volatile boolean isTakePicture;
    private Camera camera;


    int viewWidth, viewHeight;

    private Camera1Helper() {
        isTakePicture = false;

    }

    public static Camera1Helper getInstance() {
        return InstanceHolder.INSTANCE;
    }


    private static class InstanceHolder {
        private static Camera1Helper INSTANCE = new Camera1Helper();
    }


    /**
     * 打开相机
     *
     * @param surfaceView
     * @return
     */
    public Camera openCamera(AutoFitSurfaceView surfaceView) {
        viewWidth = surfaceView.getWidth();
        viewHeight = surfaceView.getHeight();
        camera = Camera.open(Integer.parseInt("1"));
        try {
            camera.setPreviewDisplay(surfaceView.getHolder());
            setParameters(surfaceView);
            setDisplayOrientation();
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return camera;
    }

    /**
     * 打开相机
     *
     * @param textureView
     * @return
     */
    public Camera openCamera(AutoFitTextureView textureView) {
        viewWidth = textureView.getWidth();
        viewHeight = textureView.getHeight();
        camera = Camera.open(Integer.parseInt(Constants.SELECT_CAMERA_ID));
        try {
            camera.setPreviewTexture(textureView.getSurfaceTexture());
            setParameters(textureView);
            setDisplayOrientation();
            camera.startPreview();
            camera.cancelAutoFocus();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return camera;
    }

    /**
     * 释放相机
     */
    public void releaseCamera() {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void takePicture() {
        //快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。
        if (isTakePicture) {
            return;
        }
        isTakePicture = true;
        camera.takePicture(new Camera.ShutterCallback() {
            @Override
            public void onShutter() {
            }
        }, new Camera.PictureCallback() {
            @Override
            // 拍摄的未压缩原数据的回调,可以为null
            public void onPictureTaken(byte[] data, Camera camera) {

            }
            //对jpeg图像数据的回调,最重要的一个回调
        }, new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                isTakePicture = false;
                camera.startPreview();
                //此回调在主线程，保存图片要应该用子线程
                SavePictureUtil.getInstance().execute(data);
            }
        });
    }

    /**
     * 设置预览参数
     */
    private void setParameters(View view) {
        Camera.Parameters parameters = camera.getParameters();
        ParameterUtil.setPreviewSize(parameters, viewWidth, viewHeight, view);
        ParameterUtil.setPictureSize(parameters, viewWidth, viewHeight);
//        ParameterUtil.setOthers(parameters);
        camera.setParameters(parameters);
    }

    /**
     * 设置预览方向  为了解决，当手机竖屏时，SurfaceView预览图像会颠倒90度
     */
    private void setDisplayOrientation() {
        int orientation = ActivityUtils.getTopActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            camera.setDisplayOrientation(90);
        }
    }


}
