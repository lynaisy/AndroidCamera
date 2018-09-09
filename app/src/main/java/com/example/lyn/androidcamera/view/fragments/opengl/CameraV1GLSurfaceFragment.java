package com.example.lyn.androidcamera.view.fragments.opengl;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lyn.androidcamera.R;
import com.example.lyn.androidcamera.utlis.Constants;
import com.example.lyn.androidcamera.view.customviews.AutoFitSurfaceView;
import com.example.lyn.androidcamera.view.customviews.CameraGLSurfaceView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class CameraV1GLSurfaceFragment extends Fragment {
    @BindView(R.id.gl_surface_view)
    CameraGLSurfaceView glSurfaceView;
    Unbinder unbinder;

    public static CameraV1GLSurfaceFragment newInstance() {
        return new CameraV1GLSurfaceFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_camerav2_glsurface, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.PermissionsResCode.CAMERA_RES_CODE) {
            glSurfaceView.openCamera();
        }
    }
}
