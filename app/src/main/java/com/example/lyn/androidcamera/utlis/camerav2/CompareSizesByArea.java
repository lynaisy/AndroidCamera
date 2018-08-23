package com.example.lyn.androidcamera.utlis.camerav2;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Size;

import java.util.Comparator;

/**
 * Created by Administrator on 2018/5/19.
 */

public class CompareSizesByArea  implements Comparator<Size> {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int compare(Size lhs, Size rhs) {
        // We cast here to ensure the multiplications won't overflow
        return Long.signum((long) lhs.getWidth() * lhs.getHeight() - (long) rhs.getWidth() * rhs.getHeight());
    }
}
