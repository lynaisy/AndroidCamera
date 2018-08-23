package com.example.lyn.androidcamera.utlis;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.Image;
import android.media.ImageReader;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileUtils;
import com.example.lyn.androidcamera.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2018/6/12.
 */

public class SavePictureUtil {
    private String fileDir;
    private String pictureDir;

    public static SavePictureUtil getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private static class InstanceHolder {
        private static SavePictureUtil INSTANCE = new SavePictureUtil();
    }

    private Executor executor;

    private SavePictureUtil() {
        fileDir = ActivityUtils.getTopActivity().getExternalFilesDir(null).getAbsolutePath();
        pictureDir = fileDir + File.separator + Constants.SAVE_PICTURE_FILE_NAME;
        FileUtils.createOrExistsDir(pictureDir);
        executor = Executors.newCachedThreadPool();
    }

    /**
     * 用线程池保存图片
     *
     * @param data
     */
    public void execute(byte[] data) {
        executor.execute(getSavePictureRunnable(data));
    }

    public void execute(ImageReader imageReader) {
        Image image = imageReader.acquireLatestImage();
        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);
        executor.execute(getSavePictureRunnable(bytes));
    }

    /**
     * 创建保存图片
     *
     * @param data
     * @return
     */
    private Runnable getSavePictureRunnable(final byte[] data) {
        return new Runnable() {
            @Override
            public void run() {
                saveBytes(data);
            }
        };
    }

    /**
     * 创建保存图片
     *
     * @param bitmap
     * @return
     */
    private Runnable getSavePictureRunnable(final Bitmap bitmap) {
        return new Runnable() {
            @Override
            public void run() {
                saveBitmap(bitmap);
            }
        };
    }


    private void saveBytes(byte[] data) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        Matrix matrix = new Matrix();
        int orientation = ActivityUtils.getTopActivity().getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT && Constants.SELECT_CAMERA_ID.equals(Constants.FRONT_FACING_CAMERA_ID)) {
            matrix.preRotate(270);
        } else if (orientation == Configuration.ORIENTATION_PORTRAIT && Constants.SELECT_CAMERA_ID.equals(Constants.BACK_CAMERA_ID)) {
            matrix.preRotate(90);
        }
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap
                .getHeight(), matrix, true);
        saveBitmap(bitmap);
    }


    private void saveBitmap(Bitmap bitmap) {
        File file = new File(pictureDir + File.separator + System.currentTimeMillis() + ".jpg");
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
