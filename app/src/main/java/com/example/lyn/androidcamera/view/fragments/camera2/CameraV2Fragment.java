package com.example.lyn.androidcamera.view.fragments.camera2;

import android.Manifest;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.PermissionUtils;
import com.example.lyn.androidcamera.R;
import com.example.lyn.androidcamera.utlis.camerav2.Camera2Helper;
import com.example.lyn.androidcamera.view.customviews.AutoFitTextureView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class CameraV2Fragment extends Fragment {
    @BindView(R.id.textureView)
    AutoFitTextureView textureView;
    Unbinder unbinder;
    @BindView(R.id.btn_record)
    Button btnRecord;

    private boolean isRecordVideo;

    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO};

    public static CameraV2Fragment newInstance() {
        return new CameraV2Fragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camerav2, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        textureView.setAspectRatio(1, 1);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                openCamera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                releaseCamera();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });
    }

    @OnClick({R.id.btn_shut, R.id.btn_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_shut:
                Camera2Helper.getInstance().takePicture();
                break;
            case R.id.btn_record:
                if (isRecordVideo) {
                    Camera2Helper.getInstance().stopRecordingVideo();
                    btnRecord.setText("开始录制");
                    isRecordVideo = false;
                } else {
                    Camera2Helper.getInstance().startRecordingVideo();
                    btnRecord.setText("停止录制");
                    isRecordVideo = true;
                }
                break;
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        Camera2Helper.getInstance().releaseCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 0) {
            openCamera();
        }
    }

    private void openCamera() {
        if (!PermissionUtils.isGranted(permissions)) {
            ActivityCompat.requestPermissions(ActivityUtils.getTopActivity(), permissions, 0);
        } else {
            Camera2Helper.getInstance().startCamera(textureView);
        }
    }

    private void releaseCamera() {
        Camera2Helper.getInstance().releaseCamera();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}