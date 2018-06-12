package com.example.lyn.androidcamera.camera1.utils;

import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.util.Size;
import android.view.View;

import com.blankj.utilcode.util.ActivityUtils;
import com.example.lyn.androidcamera.camera2.utils.CameraSizeOption;
import com.example.lyn.androidcamera.view.AutoFitSurfaceView;
import com.example.lyn.androidcamera.view.AutoFitTextureView;

import java.util.List;

/**
 * Created by Administrator on 2018/6/6.
 */

public class ParameterUtil {

    /**
     * 设置预览大小
     *
     * @param parameters
     * @param viewWidth
     * @param viewHeight
     */
    public static void setPreviewSize(Camera.Parameters parameters, int viewWidth, int viewHeight, View view) {
        Size previewSize = CameraSizeOption.chooseOptimalSize(getSupportPreviewSizes(parameters), viewWidth, viewHeight);
        parameters.setPreviewSize(previewSize.getWidth(), previewSize.getHeight());
        int orientation = ActivityUtils.getTopActivity().getResources().getConfiguration().orientation;
        if (view instanceof AutoFitSurfaceView) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ((AutoFitSurfaceView) view).setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
            } else {
                ((AutoFitSurfaceView) view).setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
            }
        } else if (view instanceof AutoFitTextureView) {
            if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                ((AutoFitTextureView) view).setAspectRatio(previewSize.getWidth(), previewSize.getHeight());
            } else {
                ((AutoFitTextureView) view).setAspectRatio(previewSize.getHeight(), previewSize.getWidth());
            }
        }
    }

    /**
     * 设置图片大小
     *
     * @param parameters
     * @param viewWidth
     * @param viewHeight
     */
    public static void setPictureSize(Camera.Parameters parameters, int viewWidth, int viewHeight) {
        Size pictureSize = CameraSizeOption.chooseMaxlSize(getSupportPictureSizes(parameters), viewWidth, viewHeight);
        parameters.setPictureSize(pictureSize.getWidth(), pictureSize.getHeight());
    }

    public static void setOthers(Camera.Parameters parameters) {
        //设置自动对焦
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }

    /**
     * 系统支持的预览大小
     *
     * @param parameters
     * @return
     */
    private static Size[] getSupportPreviewSizes(Camera.Parameters parameters) {
        List<Camera.Size> supportedPreviewSizeList = parameters.getSupportedPreviewSizes();
        Size[] sizes = new Size[supportedPreviewSizeList.size()];
        for (int i = 0; i < supportedPreviewSizeList.size(); i++) {
            sizes[i] = new Size(supportedPreviewSizeList.get(i).width, supportedPreviewSizeList.get(i).height);
        }
        return sizes;
    }

    /**
     * 系统支持的图片大小
     *
     * @param parameters
     * @return
     */
    private static Size[] getSupportPictureSizes(Camera.Parameters parameters) {
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        Size[] sizes = new Size[supportedPictureSizes.size()];
        for (int i = 0; i < supportedPictureSizes.size(); i++) {
            sizes[i] = new Size(supportedPictureSizes.get(i).width, supportedPictureSizes.get(i).height);
        }
        return sizes;
    }
}
