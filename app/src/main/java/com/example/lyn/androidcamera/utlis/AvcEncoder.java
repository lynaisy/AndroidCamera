package com.example.lyn.androidcamera.utlis;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.os.Environment;

import com.blankj.utilcode.util.Utils;

public class AvcEncoder {
    // 保存yuv数据的BlockingQueue
    private ArrayBlockingQueue<byte[]> yuvBlockingQueue = new ArrayBlockingQueue<byte[]>(yuvQueueSize);
    // 队列的长度
    private static int yuvQueueSize = 10;

    private MediaCodec mediaCodec;
    private final static String TAG = "MeidaCodec";
    private static final String MIME = "video/avc";

    private int TIMEOUT_USEC = 12000;
    private int mWidth;
    private int mHeight;
    private static final int FRAME_RATE = 30;
    private static final int BITERATE = 8500 * 1000;

    private byte[] m_info = null;
    private byte[] configbyte;
    private static String path = Utils.getApp().getCacheDir() + "/test";
    private BufferedOutputStream outputStream;
    FileOutputStream outStream;

    ByteBuffer[] inputBuffers;
    ByteBuffer[] outputBuffers;

    private boolean isRuning = false;

    /**
     * 构造器  根据官方代码实例  一共有4个步骤
     * 1.MediaCodec codec = MediaCodec.createByCodecName(name);
     * 2.codec.configure(format, …);
     * 3.MediaFormat outputFormat = codec.getOutputFormat(); // option B
     * 4.codec.start();
     *
     * @param width
     * @param height
     */
    public AvcEncoder(int width, int height) {
        mWidth = width;
        mHeight = height;
        try {
            // 步骤1
            mediaCodec = MediaCodec.createEncoderByType("video/avc");
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 步骤2
        MediaFormat mediaFormat = MediaFormat.createVideoFormat(MIME, width, height);
        mediaFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420SemiPlanar);
        mediaFormat.setInteger(MediaFormat.KEY_BIT_RATE, width * height * 5);
        mediaFormat.setInteger(MediaFormat.KEY_FRAME_RATE, 30);
        mediaFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        // 步骤3
        mediaCodec.configure(mediaFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
        // 步骤4
        mediaCodec.start();
        createfile();
    }

    public void putYuvData(byte[] buffer, int length) {
        if (yuvBlockingQueue.size() >= 10) {
            yuvBlockingQueue.poll();
        }
        yuvBlockingQueue.add(buffer);
    }


    /**
     * 停止转码线程
     */
    public void stopThread() {
        isRuning = false;
        try {
            stopEncoder();
            outputStream.flush();
            outputStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * 创建转码线程 开始转码
     */
    public void startEncoderThread() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                isRuning = true;
                byte[] input = null;
                long pts = 0;
                long generateIndex = 0;

                while (isRuning) {
                    if (yuvBlockingQueue.size() > 0) {
                        input = yuvBlockingQueue.poll();
                        byte[] yuv420sp = new byte[mWidth * mHeight * 3 / 2];
                        NV21ToNV12(input, yuv420sp, mWidth, mHeight);
                        input = yuv420sp;
                    }
                    if (input != null) {
                        try {
                            long startMs = System.currentTimeMillis();
                            ByteBuffer[] inputBuffers = mediaCodec.getInputBuffers();
                            ByteBuffer[] outputBuffers = mediaCodec.getOutputBuffers();
                            int inputBufferIndex = mediaCodec.dequeueInputBuffer(-1);
                            if (inputBufferIndex >= 0) {
                                pts = computePresentationTime(generateIndex);
                                ByteBuffer inputBuffer = inputBuffers[inputBufferIndex];
                                inputBuffer.clear();
                                inputBuffer.put(input);
                                mediaCodec.queueInputBuffer(inputBufferIndex, 0, input.length, pts, 0);
                                generateIndex += 1;
                            }

                            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();
                            int outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                            while (outputBufferIndex >= 0) {
                                //Log.i("AvcEncoder", "Get H264 Buffer Success! flag = "+bufferInfo.flags+",pts = "+bufferInfo.presentationTimeUs+"");
                                ByteBuffer outputBuffer = outputBuffers[outputBufferIndex];
                                byte[] outData = new byte[bufferInfo.size];
                                outputBuffer.get(outData);
                                if (bufferInfo.flags == 2) {
                                    configbyte = new byte[bufferInfo.size];
                                    configbyte = outData;
                                } else if (bufferInfo.flags == 1) {
                                    byte[] keyframe = new byte[bufferInfo.size + configbyte.length];
                                    System.arraycopy(configbyte, 0, keyframe, 0, configbyte.length);
                                    System.arraycopy(outData, 0, keyframe, configbyte.length, outData.length);
                                    outputStream.write(keyframe, 0, keyframe.length);
                                } else {
                                    outputStream.write(outData, 0, outData.length);
                                }

                                mediaCodec.releaseOutputBuffer(outputBufferIndex, false);
                                outputBufferIndex = mediaCodec.dequeueOutputBuffer(bufferInfo, TIMEOUT_USEC);
                            }

                        } catch (Throwable t) {
                            t.printStackTrace();
                        }
                    } else {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.execute(runnable);
    }

    /**
     * 停止转码
     */
    private void stopEncoder() {
        try {
            mediaCodec.stop();
            mediaCodec.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 创建文件
     */
    private void createfile() {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        file = new File(path + "h264.txt");
        boolean b = file.exists();
        try {
            outputStream = new BufferedOutputStream(new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 给谁转换
     *
     * @param nv21
     * @param nv12
     * @param width
     * @param height
     */
    private void NV21ToNV12(byte[] nv21, byte[] nv12, int width, int height) {
        if (nv21 == null || nv12 == null) return;
        int framesize = width * height;
        int i = 0, j = 0;
        System.arraycopy(nv21, 0, nv12, 0, framesize);
        for (i = 0; i < framesize; i++) {
            nv12[i] = nv21[i];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j - 1] = nv21[j + framesize];
        }
        for (j = 0; j < framesize / 2; j += 2) {
            nv12[framesize + j] = nv21[j + framesize - 1];
        }
    }

    /**
     * Generates the presentation time for frame N, in microseconds.
     */
    private long computePresentationTime(long frameIndex) {
        return 132 + frameIndex * 1000000 / FRAME_RATE;
    }

}
