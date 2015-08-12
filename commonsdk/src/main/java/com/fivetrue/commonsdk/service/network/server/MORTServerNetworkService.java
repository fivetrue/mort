package com.fivetrue.commonsdk.service.network.server;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.fivetrue.commonsdk.device.data.DeviceInfo;
import com.fivetrue.commonsdk.network.server.MORTDeviceInfoSender;
import com.fivetrue.commonsdk.network.constants.MORTNetworkConstants;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.fivetrue.commonsdk.network.server.MORTServer;
import com.fivetrue.commonsdk.network.server.MORTServerImpl;

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

    private ArrayList<String> mClientIps = new ArrayList<>();
    private MORTDeviceInfoSender mDeviceInfoSender = null;

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

    private RemoteCallbackList<IMORTServerNetworkCallback> mCallback = new RemoteCallbackList<>();

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mMortServer != null){
            mMortServer.unbind();
        }
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
        public void sendBroadcastToClient(final String data) throws RemoteException {
            if(mClientIps != null){
                if(mDeviceInfoSender == null){
                    mDeviceInfoSender = new MORTDeviceInfoSender();
                }
                for(int i = 0 ; i < mClientIps.size() ; i ++){
                    MORTNetworkData networkData = MORTNetworkData.fromJson(data);
                    if(networkData != null && networkData.getType() != null){
                        switch(networkData.getType()){
                        case CONNECTION:

                            break;

                        case DEVICE:
                            if(networkData.getDevice() != null){
                                switch (networkData.getDevice()){
                                    case CAMERA:
                                        mDeviceInfoSender.sendCameraData(data, getClientAddress(i));
                                        break;

                                    case SENSOR:
                                        mDeviceInfoSender.sendSensorData(data, getClientAddress(i));
                                        break;
                                }
                                Log.e(TAG, "MORTNetworkData.DEVICE = " + networkData.toString());
                            }
                            break;

                        case OPERATION:

                            break;
                        }
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
    public void onReceiveDeviceInfo(Socket socket, MORTNetworkData data) {
        if(data != null && data.getMessage() != null){
            Log.e(TAG, "onReceiveDeviceInfo = " + data.toString());
            onDeviceInfo(socket, data);
            DeviceInfo deviceInfo = new DeviceInfo(data);
            if(mCallback != null){
                int count = mCallback.beginBroadcast();
                try {
                    for(int i = 0 ; i < count ; i ++){
                        mCallback.getBroadcastItem(i).onRecevedDeviceInfo(deviceInfo);
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                mCallback.finishBroadcast();
            }
        }
    }

    @Override
    public void onReceiveConnection(Socket socket, MORTNetworkData data, String serverAddr) {
        if(socket != null && data != null && data.getConnection() != null){
            switch(data.getConnection()){
                case CONNECTED:
                    addClientAddress(socket.getInetAddress().getHostAddress());
                    DataOutputStream out = null;
                    try {
                        out = new DataOutputStream(socket.getOutputStream());
                        out.writeUTF(data.convertJson());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case DISCONNECTED:
                    if(socket != null){
                        removeClientAddress(socket.getInetAddress().getHostAddress());
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
            }

        }
    }
    synchronized private void addClientAddress(String ip){
        mClientIps.add(ip);
    }

    synchronized  private void removeClientAddress(String ip){
        mClientIps.remove(ip);
    }

    synchronized  private String getClientAddress(int index){
        if(mClientIps.size() > index){
            return mClientIps.get(index);
        }
        return null;
    }


    private void onOperation(Socket socket, final MORTNetworkData data){
        if(socket != null && data != null){
            DataOutputStream out = null;
            try {
                out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(data.convertJson());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void onDeviceInfo(Socket socket, MORTNetworkData data){
        if(socket != null && data != null){
            DataOutputStream out = null;
            try {
                out = new DataOutputStream(socket.getOutputStream());
                out.writeUTF(data.convertJson());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
