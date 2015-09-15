package com.fivetrue.childrencar.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.ImageView;
import android.widget.Toast;

import com.fivetrue.childrencar.R;
import com.fivetrue.childrencar.network.server.MORTDeviceInfoSender;
import com.fivetrue.commonsdk.device.data.DeviceInfo;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.fivetrue.commonsdk.service.network.server.IMORTServerNetworkCallback;
import com.fivetrue.commonsdk.service.network.server.IMORTServerNetworkService;
import com.fivetrue.childrencar.service.MORTServerNetworkService;
import com.fivetrue.commonsdk.utils.BitmapConverter;
import com.fivetrue.commonsdk.utils.ScreenTaker;
import com.google.gson.Gson;

import java.io.IOException;

import ioio.lib.api.IOIO;
import ioio.lib.spi.Log;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOActivity;

/**
 * Created by Fivetrue on 2015-03-19.
 */

public class BaseActivity extends IOIOActivity implements MORTDeviceControlLooper.DeviceControlImpl, SensorEventListener{

    public static final String TAG = "BaseActivity";
    private IMORTServerNetworkService mNetworkService = null;
    private MORTDeviceControlLooper mDeviceControlLooper = null;
    private ScreenTaker mScreenTaker = null;
    private Camera mCamera = null;
    private ImageView mImageView = null;
    private Gson mGson = null;
    private SensorManager mSensorManager = null;

    private Sensor mGravitySensor = null;

    private int HRZ = 100;

    @Override
    protected IOIOLooper createIOIOLooper() {
        return mDeviceControlLooper;
    }

    private void toast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(BaseActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private IMORTServerNetworkCallback mCallback = new IMORTServerNetworkCallback.Stub()
    {

        @Override
        public void onRecevedOperation(final MORTNetworkData data) throws RemoteException {
            Log.e(TAG, "onRecevedOperation");
            if(data != null && data.getOperation() != null){
                switch(data.getOperation()){
                    case CENTER_LED_ON:
                        mDeviceControlLooper.setOnCenterLed(true);
                        break;

                    case CENTER_LED_OFF:
                        mDeviceControlLooper.setOnCenterLed(false);
                        break;

                    case BUZZER_ON:
                        mDeviceControlLooper.setOnBuzzer(HRZ+=10, 300);
                        break;

                    case SERVO_MORTOR:
                        if(data.getExtra() != null){
                            mDeviceControlLooper.setServoMotorValue(Float.parseFloat(data.getExtra()));
                        }
                        break;

                    case LEFT_MORTOR_ON:

                        break;

                    case LEFT_MORTOR_OFF:

                        break;

                    case RIGHT_MORTOR_ON:

                        break;

                    case RIGHT_MORTOR_OFF:

                        break;
                }
            }
        }

        @Override
        public void onRecevedDeviceInfo(DeviceInfo data) throws RemoteException {

        }
    };

    private ServiceConnection mConnNetwork = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            if(service != null){
                mNetworkService = IMORTServerNetworkService.Stub.asInterface(service);
                try {
                    mNetworkService.registerCallback(mCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            if(mNetworkService != null){
                try {
                    mNetworkService.unregisterCallback(mCallback);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mNetworkService = null;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
        initDatas();
        initViews();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MORTServerNetworkService.bind(this, mConnNetwork);
        mCamera = Camera.open(findRearCamera());
        registerSensor();
    }

    @Override
    protected void onStop() {
        super.onStop();
        MORTServerNetworkService.unbind(this, mConnNetwork);
        mScreenTaker.onStopTaker();
        mCamera.release();
        unregisterSensor();
    }

    @Override
    public void showVersionInfo(IOIO ioio, String msg) {
        toast(String.format("%s\n" +
                        "IOIOLib: %s\n" +
                        "Application firmware: %s\n" +
                        "Bootloader firmware: %s\n" +
                        "Hardware: %s",
                msg,
                ioio.getImplVersion(IOIO.VersionType.IOIOLIB_VER),
                ioio.getImplVersion(IOIO.VersionType.APP_FIRMWARE_VER),
                ioio.getImplVersion(IOIO.VersionType.BOOTLOADER_VER),
                ioio.getImplVersion(IOIO.VersionType.HARDWARE_VER)));
    }

    @Override
    public void disconnected() {
        toast("IOIO disconnected");
    }

    private void initDatas(){
        mDeviceControlLooper = new MORTDeviceControlLooper(this);
        mScreenTaker = new ScreenTaker(this);
        mGson = new Gson();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGravitySensor =  mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
    }

    private void initViews(){
        final SurfaceView surface = (SurfaceView) findViewById(R.id.surface_camera);
        mImageView = (ImageView) findViewById(R.id.iv_captrue);
        surface.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    mCamera.setDisplayOrientation(90);
                    mCamera.setPreviewDisplay(holder);
                    mCamera.getParameters().setFlashMode(Camera.Parameters.FLASH_MODE_AUTO);
                    mCamera.getParameters().setAntibanding(Camera.Parameters.ANTIBANDING_OFF);
                    mCamera.startPreview();
                    mScreenTaker.onStartTakerFromCamera(mCamera, 200L, onScreenTakeListener);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                surface.requestLayout();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        surface.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void registerSensor(){
        if(mSensorManager != null){
            if(mGravitySensor != null){
                mSensorManager.registerListener(this, mGravitySensor, SensorManager.SENSOR_DELAY_GAME);
            }
        }
    }

    private void unregisterSensor(){
        if(mSensorManager != null){
            mSensorManager.unregisterListener(this);
        }
    }

    private int findFrontCamera(){
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }
    private int findRearCamera(){
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d(TAG, "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private ScreenTaker.OnScreenTakeListener onScreenTakeListener = new ScreenTaker.OnScreenTakeListener() {
        @Override
        public void onTakeScreen(Bitmap screen) {
            if(screen != null && !screen.isRecycled()){
                MORTNetworkData network = new MORTNetworkData();
                network.setType(MORTNetworkData.Type.DEVICE);
                network.setDevice(MORTNetworkData.Device.CAMERA);
                network.setExtra(BitmapConverter.BitmapToBase64String(screen, 50));
                String json = mGson.toJson(network);
                MORTDeviceInfoSender.get().sendDataToClient(json);
                screen.recycle();
                network = null;
            }
        }
    };

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event != null && mNetworkService != null){
            if(event.sensor.getType() == Sensor.TYPE_GRAVITY){
                MORTNetworkData network = new MORTNetworkData();
                network.setType(MORTNetworkData.Type.DEVICE);
                network.setDevice(MORTNetworkData.Device.SENSOR);
                network.setExtra(mGson.toJson(event));
                String json = mGson.toJson(network);
                MORTDeviceInfoSender.get().sendDataToClient(json);
                network = null;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}