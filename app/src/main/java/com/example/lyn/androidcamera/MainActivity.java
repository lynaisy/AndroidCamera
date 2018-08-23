package com.example.lyn.androidcamera;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.lyn.androidcamera.view.fragments.camera1.CameraV1Fragment;
import com.example.lyn.androidcamera.view.fragments.camera1.CameraV1TextureFragment;
import com.example.lyn.androidcamera.view.fragments.camera2.CameraV2Fragment;

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

        //方式二：camera1 + surfaceView
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, CameraV1Fragment.newInstance())
                .commit();

//        //方式三：camera2 + textureView
//        getSupportFragmentManager().beginTransaction()
//                .add(R.id.container, CameraV2Fragment.newInstance())
//                .commit();

    }
}
