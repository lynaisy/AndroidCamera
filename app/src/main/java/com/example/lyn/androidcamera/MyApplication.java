package com.example.lyn.androidcamera;

import android.app.Application;

import com.blankj.utilcode.util.Utils;

/**
 * Created by Administrator on 2018/6/5.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(this);
    }
}
