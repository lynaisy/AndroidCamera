package com.example.lyn.androidcamera;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.example.lyn.androidcamera.camera1.CameraV1Fragment;
import com.example.lyn.androidcamera.camera1.CameraV1TextureFragment;
import com.example.lyn.androidcamera.camera2.CameraV2Fragment;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //方式一：camera1 + TextureView
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.container, CameraV1TextureFragment.newInstance())
//                .commit();

//        //方式二：camera1 + surfaceView
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.container, CameraV1Fragment.newInstance())
//                .commit();

        //方式三：camera2 + textureView
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, CameraV2Fragment.newInstance())
                .commit();

    }
}
