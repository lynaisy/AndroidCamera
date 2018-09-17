package com.example.lyn.androidcamera.view.fragments.camera1;


import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.ActivityUtils;
import com.example.lyn.androidcamera.R;
import com.example.lyn.androidcamera.utlis.AvcEncoder;
import com.example.lyn.androidcamera.utlis.camerav1.Camera1Helper;
import com.example.lyn.androidcamera.view.customviews.AutoFitSurfaceView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/5/18.
 * 使用surfaceView来预览视频流
 */

public class CameraV1Fragment extends Fragment implements SurfaceHolder.Callback, Camera.PreviewCallback {
    @BindView(R.id.surfaceView)
    AutoFitSurfaceView autoFitSurfaceView;
    Unbinder unbinder;
    private SurfaceHolder holder;
    private AvcEncoder avcCodec;

    public static CameraV1Fragment newInstance() {
        return new CameraV1Fragment();
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camerav1, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        holder = autoFitSurfaceView.getHolder();
        holder.addCallback(this);
        autoFitSurfaceView.setAspectRatio(1, 1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        openCamera();
        avcCodec = new AvcEncoder(Camera1Helper.getInstance().getViewWidth(), Camera1Helper.getInstance().getViewHeight());
        avcCodec.startEncoderThread();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        avcCodec.stopThread();
        releaseCamera();
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //预览的数据回调
        avcCodec.putYuvData(data, data.length);
    }

    /**
     * 点击事件
     *
     * @param view
     */
    @OnClick({R.id.btn_shut})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_shut:
                Camera1Helper.getInstance().takePicture();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            openCamera();
        }
    }

    private void openCamera() {
        if (ActivityCompat.checkSelfPermission(ActivityUtils.getTopActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(ActivityUtils.getTopActivity(), new String[]{Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            Camera1Helper.getInstance().openCamera(autoFitSurfaceView).setPreviewCallback(this);
            autoFitSurfaceView.setAspectRatio(Camera1Helper.getInstance().getViewWidth(), Camera1Helper.getInstance().getViewHeight());
        }
    }

    private void releaseCamera() {
        Camera1Helper.getInstance().releaseCamera();
    }

}
