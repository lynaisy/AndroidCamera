package com.example.lyn.androidcamera.camera2.utils;

import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Size;
import android.view.Surface;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.ScreenUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2018/5/19.
 */

public class CameraSizeOption {
    /**
     * 视频录制的VideoSize
     *
     * @param choices
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Size chooseVideoSize(Size[] choices) {
        for (Size size : choices) {
            if (size.getWidth() == size.getHeight() * 4 / 3 && size.getWidth() <= 1080) {
                return size;
            }
        }
        return choices[choices.length - 1];
    }


    /**
     * 为了避免太大的预览大小会超过相机总线的带宽限
     *
     * @param choices
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Size chooseMaxlSize(Size[] choices, int viewWidth, int viewHeight) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> sizeList = new ArrayList<>();        //合适的
        List<Size> sizeList1 = new ArrayList<>();       //不合适的
        for (Size option : choices) {
            if (viewWidth / (float) viewHeight == (float) option.getWidth() / (float) option.getHeight()) {
                sizeList.add(option);
            }else {
                sizeList1.add(option);
            }
        }
        if (sizeList.size() > 0) {
            return Collections.max(sizeList, new CompareSizesByArea());
        } else {
            return Collections.max(sizeList1, new CompareSizesByArea());
        }
    }

    /**
     * 为了避免太大的预览大小会超过相机总线的带宽限
     *
     * @param choices
     * @param viewWidth
     * @param viewHeight
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Size chooseOptimalSize(Size[] choices, int viewWidth, int viewHeight) {
        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();

        int maxWidth = ScreenUtils.getScreenWidth();
        int maxHeight = ScreenUtils.getScreenHeight();

        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight && (float) viewWidth / (float) viewHeight == (float) option.getWidth() / (float) option.getHeight()) {
                if (option.getWidth() >= viewWidth && option.getHeight() >= viewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            return choices[0];
        }
    }
}
