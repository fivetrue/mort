package com.fivetrue.commonsdk.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.fivetrue.commonsdk.network.client.MORTDeviceInfoClient;
import com.fivetrue.commonsdk.network.constants.MORTNetworkConstants;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.fivetrue.commonsdk.network.server.MORTServer;
import com.fivetrue.commonsdk.network.server.MORTServerImpl;
import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Fivetrue on 2015-03-19.
 */
public class MORTServerNetworkService extends Service implements MORTServerImpl {
    public static final String TAG = "ServerNetworkService";
    public static final String ACTION_BIND = "com.fivetrue.commsdk.service.control.bind";
    private static final int INVALID_VALUE = -1;

    private ArrayList<String> mClientIp = new ArrayList<>();
    private MORTDeviceInfoClient mCameraClient = null;

    public static void bind(Context context, ServiceConnection conn){
        Intent intent = new Intent(context, MORTServerNetworkService.class);
        intent.setAction(ACTION_BIND);
        context.bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }
    public static void unbind(Context context, ServiceConnection conn){
        if(conn != null ){
            context.unbindService(conn);
        }
    }
    private MORTServer mMortServer = null;
    private MORTNetworkData mDraftData = null;
    private Gson mGson = new Gson();

//    private IMORTDeviceMonitorService mMonitorService = null;
//    private IMORTDeviceControlService mControlService = null;

    private RemoteCallbackList<IMORTServerNetworkCallback> mCallback = new RemoteCallbackList<>();

//    private ServiceConnection mConnMonitor = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            if(service != null){
//                mMonitorService = IMORTDeviceMonitorService.Stub.asInterface(service);
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            if(mMonitorService != null){
//                mMonitorService = null;
//            }
//        }
//    };
//
//    private ServiceConnection mConnControl = new ServiceConnection() {
//        @Override
//        public void onServiceConnected(ComponentName name, IBinder service) {
//            if(service != null){
//                mControlService = IMORTDeviceControlService.Stub.asInterface(service);
//            }
//        }
//
//        @Override
//        public void onServiceDisconnected(ComponentName name) {
//            if(mControlService != null){
//                mControlService = null;
//            }
//
//        }
//    };

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
        mMortServer = new MORTServer(this, MORTNetworkConstants.MORT_PORT);
        mMortServer.setNetworkMortImplement(this);
        mMortServer.bind();
//        MORTDeviceControlService.bind(this, mConnControl);
//        MORTDeviceMonitorService.bind(this, mConnMonitor);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMortServer != null){
            mMortServer.unbind();
        }
//        MORTDeviceControlService.unbind(this, mConnControl);
//        MORTDeviceMonitorService.unbind(this, mConnMonitor);
    }

    private IBinder mBinder = new IMORTServerNetworkService.Stub(){

        @Override
        public void registerCallback(IMORTServerNetworkCallback callback) throws RemoteException {
            if(callback != null && mCallback != null){
                Log.e(TAG, "registerCallback");
                mCallback.register(callback);
            }
        }

        @Override
        public void unregisterCallback(IMORTServerNetworkCallback callback) throws RemoteException {
            if(callback != null && mCallback != null){
                Log.e(TAG, "unregisterCallback");
                mCallback.unregister(callback);
            }
        }

        @Override
        public void responseOperationData(String response) throws RemoteException {

        }

        @Override
        public void responseControlViewData(String response) throws RemoteException {

        }

        @Override
        public void sendBroadcastToClient(final String data) throws RemoteException {
            if(mClientIp != null){
                if(mCameraClient == null){
                    mCameraClient = new MORTDeviceInfoClient();
                }
                for(int i = 0 ; i < mClientIp.size() ; i ++){
                    MORTNetworkData networkData = mGson.fromJson(data, MORTNetworkData.class);
                    if(networkData.getType().equals(MORTNetworkData.DEVICE_INFO_CAMERA)){
                        mCameraClient.sendCameraData(data, getClientAddress(i));
                        Log.e(TAG, "MORTNetworkData.DEVICE_INFO_CAMERA = " + networkData.toString());
                    }else if(networkData.getType().equals(MORTNetworkData.DEVICE_INFO_SENSOR)){
                        mCameraClient.sendSensorData(data, getClientAddress(i));
                        Log.e(TAG, "MORTNetworkData.DEVICE_INFO_SENSOR = " + networkData.toString());
                    }
                    networkData = null;
                }
            }
        }
    };

    @Override
    public MORTNetworkData getDraftData() {
        return mDraftData;
    }

    @Override
    public void onReceiveOperation(Socket socket, MORTNetworkData data) {
        Log.e(TAG, "onReceiveOperation = " + data.toString());
        onOperation(socket, data);
        if(mCallback != null){
            int count = mCallback.beginBroadcast();
            try {
                for(int i = 0 ; i < count ; i ++){
                    mCallback.getBroadcastItem(i).onRecevedOperation(data);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mCallback.finishBroadcast();
        }
    }

    @Override
    public void onReceiveControlView(Socket socket, MORTNetworkData data) {
        Log.e(TAG, "onReceiveControlView = " + data.toString());
        onControlView(socket, data);
        if(mCallback != null){
            int count = mCallback.beginBroadcast();
            try {
                for(int i = 0 ; i < count ; i ++){
                    mCallback.getBroadcastItem(i).onReceivedControlView(data);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mCallback.finishBroadcast();
        }
    }

    @Override
    public void onConnected(Socket socket, MORTNetworkData data) {
        //Conncected 되었다는 패킷 전달.
        if(socket != null && data != null){
            addClientAddress(socket.getInetAddress().getHostAddress());
            DataOutputStream out = null;
            try {
                out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(mGson.toJson(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisconnected(Socket socket, MORTNetworkData data) {
        if(socket != null){
            removeClientAddress(socket.getInetAddress().getHostAddress());
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    synchronized private void addClientAddress(String ip){
        mClientIp.add(ip);
    }

    synchronized  private void removeClientAddress(String ip){
        mClientIp.remove(ip);
    }

    synchronized  private String getClientAddress(int index){
        if(mClientIp.size() > index){
            return mClientIp.get(index);
        }
        return null;
    }


    private void onOperation(Socket socket, final MORTNetworkData data){
        if(socket != null && data != null){
//            Log.e(TAG, "onOperation = " + data.toString());
//            if(data.getAction().equals(MORTNetworkData.OPERATION_LIGHT_ON)){
//                try {
//                    mControlService.writeDigitalSensorValue(0, true);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }else if(data.getAction().equals(MORTNetworkData.OPERATION_LIGHT_OFF)){
//                try {
//                    mControlService.writeDigitalSensorValue(0, false);
//                } catch (RemoteException e) {
//                    e.printStackTrace();
//                }
//            }
            DataOutputStream out = null;
            try {
                out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(mGson.toJson(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onControlView(Socket socket, MORTNetworkData data){
        if(socket != null && data != null){
            DataOutputStream out = null;
            try {
                out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(mGson.toJson(data));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
