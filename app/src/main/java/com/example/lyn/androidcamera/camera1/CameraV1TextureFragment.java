package com.example.lyn.androidcamera.camera1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
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
import com.example.lyn.androidcamera.R;
import com.example.lyn.androidcamera.view.AutoFitTextureView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2018/6/7.
 * 使用textureView来预览视频流
 */

public class CameraV1TextureFragment extends Fragment {
    @BindView(R.id.textureView)
    AutoFitTextureView textureView;
    Unbinder unbinder;

    public static CameraV1TextureFragment newInstance() {
        return new CameraV1TextureFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camerav1_texture, container, false);
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
                //预览的数据回调
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    /**
     * 点击事件
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
            ActivityCompat.requestPermissions(ActivityUtils.getTopActivity(), new String[]{Manifest.permission.CAMERA}, 0);
        } else {
            Camera1Helper.getInstance().openCamera(textureView);
        }
    }

    private void releaseCamera() {
        Camera1Helper.getInstance().releaseCamera();
    }
}
