package com.fivetrue.commonsdk.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.View;

import com.fivetrue.commonsdk.MortApplication;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Fivetrue on 2015-05-26.
 */
public class ScreenTaker {

    public static final String TAG = "ScreenTaker";

    /**
     * Version code JB
     */
    private static final int SEPERATE_VERSION = 16;

    public interface OnScreenTakeListener{
        void onTakeScreen(Bitmap screen);
    }
    public static final int INVALID_VALUE = -1;

    private Activity mActivity = null;
    private View mRootView = null;

    private Camera mCamera = null;

    private long mMiliSec = INVALID_VALUE;
    private boolean isRun = false;
    private boolean isCamera = false;

    private OnScreenTakeListener mOnScreenTakeListener = null;

    public ScreenTaker(Activity activity){
        this.mActivity = activity;
        mRootView = mActivity.findViewById(android.R.id.content).getRootView();
        mRootView.setDrawingCacheEnabled(true);
    }

    public void onStartTaker(long millisec, OnScreenTakeListener l){
        this.mMiliSec = millisec;
        this.mOnScreenTakeListener = l;
        this.isRun = true;
        this.isCamera = false;
        new ScreenTakerThread().start();
    }

    public void onStartTakerFromView(View view, long millisec, OnScreenTakeListener l){
        mRootView = view;
        this.mMiliSec = millisec;
        this.mOnScreenTakeListener = l;
        this.isRun = true;
        this.isCamera = false;
        new ScreenTakerThread().start();
    }

    public void onStartTakerFromCamera(Camera camera, long millisec, OnScreenTakeListener l){
        mCamera = camera;
        this.mMiliSec = millisec;
        this.mOnScreenTakeListener = l;
        this.isRun = true;
        this.isCamera = true;
        new ScreenTakerThread().start();
    }

    public void setCamera(boolean setCamera){
        this.isCamera = setCamera;
    }

    public void onStopTaker(){
        this.isRun = false;
    }

    private class ScreenTakerThread extends Thread{

        private boolean isLowPerfomance = false;
        private Rect mRect = null;
        private int mQuallity = 20;

        public ScreenTakerThread(){
            isLowPerfomance = Build.VERSION.SDK_INT < SEPERATE_VERSION;
//            mQuallity = isLowPerfomance ? 10 : 20;
        }
        @Override
        public void run() {

            if(mMiliSec == INVALID_VALUE){
                new IllegalArgumentException("milli sec is invalid");
            }

            if(mRootView == null){
                new IllegalArgumentException("rootView is null");
            }
            if(mOnScreenTakeListener == null){
                new IllegalArgumentException("screen taker listener is null");
            }
            while(isRun){
                if(isCamera){
                    if(mCamera != null && mOnScreenTakeListener != null){
                        mCamera.setOneShotPreviewCallback(new Camera.PreviewCallback() {
                            @Override
                            public void onPreviewFrame(byte[] data, Camera camera) {
                                Camera.Parameters parameters = camera.getParameters();
                                int imageFormat = parameters.getPreviewFormat();
                                if (imageFormat == ImageFormat.NV21)
                                {
                                    YuvImage img = new YuvImage(data, ImageFormat.NV21, parameters.getPreviewSize().width, parameters.getPreviewSize().height, null);
                                    ByteArrayOutputStream os = new ByteArrayOutputStream();

                                    if(mRect == null){
                                        if(isLowPerfomance){
                                            mRect = new Rect((int)(img.getWidth() * 0.3), (int)(img.getHeight() * 0.3),
                                                    (int)(img.getWidth() * 0.7) , (int)(img.getHeight() * 0.7));
                                        }else{
                                            mRect = new Rect(0, 0, img.getWidth(), img.getHeight());
                                        }
                                    }
                                    img.compressToJpeg(mRect, mQuallity, os);
                                    Bitmap temp = BitmapFactory.decodeByteArray(os.toByteArray(), 0, os.toByteArray().length);
                                    Matrix matrix = new Matrix();
                                    matrix.postRotate(90);
                                    Bitmap preview = Bitmap.createBitmap(temp, 0, 0, temp.getWidth() , temp.getHeight(), matrix, true);
                                    mOnScreenTakeListener.onTakeScreen(preview);
                                    temp.recycle();
                                    temp = null;
                                    img = null;
                                    os.reset();
                                    try {
                                        os.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                    } else{
                        isRun = false;
                        Log.e(TAG, "Camera or Listener is Null");
                    }
                }else{
                    if(mRootView != null && mOnScreenTakeListener != null){
                        mRootView.setDrawingCacheEnabled(true);
                        mRootView.buildDrawingCache(true);
                        if(mRootView.getDrawingCache() != null && !mRootView.getDrawingCache().isRecycled()){
                            Bitmap bmp = Bitmap.createBitmap(mRootView.getDrawingCache());
                            ByteArrayOutputStream bos = new ByteArrayOutputStream();
                            bmp.compress(Bitmap.CompressFormat.JPEG, 50, bos);
                            byte[] imageData = bos.toByteArray();
                            Bitmap bm = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
                            mOnScreenTakeListener.onTakeScreen(bm);
                            bmp.recycle();
                        }
                        mRootView.setDrawingCacheEnabled(false); // clear drawing cache
                    } else{
                        isRun = false;
                        Log.e(TAG, "View or Listener is Null");
                    }
                }
                try {
                    Thread.sleep(mMiliSec);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
