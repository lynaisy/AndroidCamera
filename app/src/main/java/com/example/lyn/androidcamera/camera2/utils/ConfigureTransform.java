package com.example.lyn.androidcamera.camera2.utils;

import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Size;
import android.view.Surface;
import android.view.TextureView;

import com.blankj.utilcode.util.ActivityUtils;

/**
 * Created by Administrator on 2018/5/19.
 */

public class ConfigureTransform {
    /**
     * Configures the necessary {@link android.graphics.Matrix} transformation to `textureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `textureView` is fixed.
     *
     * @param viewWidth  The width of `textureView`
     * @param viewHeight The height of `textureView`
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void configureTransform(TextureView textureView, Size mPreviewSize, int viewWidth, int viewHeight) {

        if (null == textureView || null == mPreviewSize) {
            return;
        }
        int rotation = ActivityUtils.getTopActivity().getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        //视频左右翻转
        //matrix.postScale(-1 ,1, centerX, centerY);
        textureView.setTransform(matrix);
    }
}
