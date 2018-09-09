package com.example.lyn.androidcamera.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lyn.androidcamera.R;
import com.example.lyn.ffmpegandroid.MainTest;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Used to load the 'native-lib' library on application startup.

    private Button protocol,format,codec,filter;
    private TextView tv_info;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_1);

        init();
    }

    private void init() {
        protocol = (Button) findViewById(R.id.btn_protocol);
        format = (Button) findViewById(R.id.btn_format);
        codec = (Button) findViewById(R.id.btn_codec);
        filter = (Button) findViewById(R.id.btn_filter);
        tv_info = (TextView) findViewById(R.id.tv_info);

        protocol.setOnClickListener(this);
        format.setOnClickListener(this);
        codec.setOnClickListener(this);
        filter.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_protocol:
                tv_info.setText(new MainTest().urlprotocolinfo());
                break;
            case R.id.btn_format:
                tv_info.setText(new MainTest().avformatinfo());
                break;
            case R.id.btn_codec:
                tv_info.setText(new MainTest().avcodecinfo());
                break;
            case R.id.btn_filter:
                tv_info.setText(new MainTest().avfilterinfo());
                break;
            default:
                break;
        }
    }



}
