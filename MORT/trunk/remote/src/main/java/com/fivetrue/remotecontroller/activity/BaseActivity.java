package com.fivetrue.remotecontroller.activity;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;

import com.fivetrue.commonsdk.device.data.Camera;
import com.fivetrue.commonsdk.device.data.Sensor;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.fivetrue.commonsdk.service.network.client.IMORTClientNetworkCallback;
import com.fivetrue.commonsdk.service.network.client.IMORTClientNetworkService;
import com.fivetrue.remotecontroller.network.server.MORTDeviceInfoServer;
import com.fivetrue.remotecontroller.service.MORTClientNetworkService;

import ioio.lib.spi.Log;

import static com.fivetrue.commonsdk.service.network.client.IMORTClientNetworkCallback.*;


abstract public class BaseActivity extends FragmentActivity{

    private static final String TAG = "BaseActivity";

    private ProgressDialog mProgress = null;
    private IMORTClientNetworkService mClientService = null;
    static private MORTDeviceInfoServer sDeviceInfoServer = null;

    private  MORTDeviceInfoServer.OnReceivedMortDeivceInfoListener onReceivedMortDeivceInfoListener = new MORTDeviceInfoServer.OnReceivedMortDeivceInfoListener() {
        @Override
        public void onReceivcedCameraData(final MORTNetworkData data) {
            if(data != null){
                Camera camera = new Camera(data);
                onCameraData(camera);
            }
        }

        @Override
        public void onReceivcedSensorData(MORTNetworkData data) {
            if(data != null){
                Sensor sensor = new Sensor(data);
                onSensorData(sensor);
            }
        }
    };

    private IMORTClientNetworkCallback mClientCallback = new Stub(){

        @Override
        public void onReceivedCameraData(Camera data) throws RemoteException {
            onCameraData(data);
        }

        @Override
        public void onReceivedSensorData(Sensor data) throws RemoteException {
            onSensorData(data);
        }

        @Override
        public void onConnected(String ip, MORTNetworkData data) throws RemoteException {
            BaseActivity.this.onConnected(ip, data);
        }

        @Override
        public void onDisconnected() throws RemoteException {
            BaseActivity.this.onDisconnected();
        }

        @Override
        public void onFailConnect() throws RemoteException {
            BaseActivity.this.onFailConnect();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgress = new ProgressDialog(this);
        if(sDeviceInfoServer == null){
            sDeviceInfoServer = new MORTDeviceInfoServer(onReceivedMortDeivceInfoListener);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        MORTClientNetworkService.bind(getApplicationContext(), serviceConnection);
        sDeviceInfoServer.startCameraServer();
        sDeviceInfoServer.startSensorServer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(serviceConnection != null){
            MORTClientNetworkService.unbind(getApplicationContext(), serviceConnection);
        }
        sDeviceInfoServer.stopCameraServer();
        sDeviceInfoServer.stopSensorServer();
    }

    protected void showProgress(String title, String message){
        if(mProgress != null && !mProgress.isShowing()){
            mProgress.setTitle(title);
            mProgress.setMessage(message);
            mProgress.show();
        }
    }

    protected void dismissProgress(){
        if(mProgress != null && mProgress.isShowing()){
            mProgress.dismiss();
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mClientService = IMORTClientNetworkService.Stub.asInterface(service);
            try {
                mClientService.registerCallback(mClientCallback);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            onServiceBind(mClientService);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mClientService = null;
            onServiceUnbind();
        }
    };

    protected void onServiceBind(IMORTClientNetworkService service){
        Log.d(TAG, "call onServiceBind");
    }

    protected void onServiceUnbind(){
        Log.d(TAG, "call onServiceUnbind");
    }

    protected void onCameraData(Camera camera){

    }

    protected void onSensorData(Sensor sensor){

    }

    protected void onConnected(String ip, MORTNetworkData data){

    }

    protected void onDisconnected(){

    }

    protected void onFailConnect(){

    }

    protected IMORTClientNetworkService getClientService(){
        return mClientService;
    }
}
