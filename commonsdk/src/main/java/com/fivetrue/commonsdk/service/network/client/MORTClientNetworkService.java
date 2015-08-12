package com.fivetrue.commonsdk.service.network.client;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.hardware.SensorEvent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;

import com.fivetrue.commonsdk.device.data.Camera;
import com.fivetrue.commonsdk.device.data.Sensor;
import com.fivetrue.commonsdk.network.client.MORTClient;
import com.fivetrue.commonsdk.network.client.MORTDeviceInfoServer;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.fivetrue.commonsdk.utils.BitmapConverter;

import java.net.Socket;

/**
 * Created by Fivetrue on 2015-03-19.
 */
public class MORTClientNetworkService extends Service{
    public static final String TAG = "MORTClientNetworkService";
    public static final String ACTION_BIND = "com.fivetrue.commonsdk.service.client.bind";
    private static final int INVALID_VALUE = -1;


    public static void bind(Context context, ServiceConnection conn){
        Intent intent = new Intent(context, MORTClientNetworkService.class);
        intent.setAction(ACTION_BIND);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }
    public static void unbind(Context context, ServiceConnection conn){
        if(conn != null ){
            context.unbindService(conn);
        }
    }

    private RemoteCallbackList<IMORTClientNetworkCallback> mCallback = new RemoteCallbackList<>();
    private MORTClient mMortClient = null;
    private MORTDeviceInfoServer mDeviceInfoServer = null;

    @Override
    public IBinder onBind(Intent intent) {
        if(intent != null && intent.getAction() != null && intent.getAction().equals(ACTION_BIND)){
            return mBinder;
        }
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if(mMortClient == null){
            mMortClient = new MORTClient(this);
            mMortClient.setMORTClientNetworkListener(mortClientNetworkListener);
        }

        mDeviceInfoServer = new MORTDeviceInfoServer(onReceivedMortDeivceInfoListener);
        mDeviceInfoServer.startCameraServer();
        mDeviceInfoServer.startSensorServer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mDeviceInfoServer != null){
            mDeviceInfoServer.stopCameraServer();
            mDeviceInfoServer.stopSensorServer();
        }
    }

    private IBinder mBinder = new IMORTClientNetworkService.Stub(){

        @Override
        public void registerCallback(IMORTClientNetworkCallback callback) throws RemoteException {
            if(callback != null){
                mCallback.register(callback);
            }
        }

        @Override
        public void unregisterCallback(IMORTClientNetworkCallback callback) throws RemoteException {
            if(callback != null){
                mCallback.unregister(callback);
            }
        }
    };


    private MORTClient.MORTClientNetworkListener mortClientNetworkListener = new MORTClient.MORTClientNetworkListener() {
        @Override
        public void onConnected(Socket socket, String ipAddress, MORTNetworkData data) {

        }

        @Override
        public void onDisconnectd() {

        }

        @Override
        public void onFailConnect() {

        }
    };

    private MORTDeviceInfoServer.OnReceivedMortDeivceInfoListener onReceivedMortDeivceInfoListener = new MORTDeviceInfoServer.OnReceivedMortDeivceInfoListener() {
        @Override
        public void onReceivcedCameraData(final MORTNetworkData data) {

            if(mCallback != null && data != null){
                Camera camera = new Camera(data);
                int n = mCallback.beginBroadcast();
                for(int i = 0 ; i < n ; i++){
                    try {
                        mCallback.getBroadcastItem(i).onReceivedCameraData(camera);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }

        }

        @Override
        public void onReceivcedSensorData(MORTNetworkData data) {
            if(mCallback != null && data != null){
                Sensor sensor = new Sensor(data);
                int n = mCallback.beginBroadcast();
                for(int i = 0 ; i < n ; i++){
                    try {
                        mCallback.getBroadcastItem(i).onReceivedSensorData(sensor);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }

                }
            }
        }
    };
}
