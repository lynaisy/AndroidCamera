package com.example.lyn.ffmpegandroid;

public class MainTest {
    static {
        System.loadLibrary("native-lib");
    }
    public String stringFromJNICall() {
        return stringFromJNI();
    }
    public String urlprotocolinfoCall() {
        return urlprotocolinfo();
    }
    public String avformatinfoCall() {
        return avformatinfo();
    }
    public String avcodecinfoCall() {
        return avcodecinfo();
    }
    public String avfilterinfoCall() {
        return avfilterinfo();
    }
    public native String stringFromJNI();

    public native String urlprotocolinfo();

    public native String avformatinfo();

    public native String avcodecinfo();

    public native String avfilterinfo();

}
