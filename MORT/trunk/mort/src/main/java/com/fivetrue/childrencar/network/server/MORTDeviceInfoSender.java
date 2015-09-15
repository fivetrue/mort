package com.fivetrue.childrencar.network.server;

import android.text.TextUtils;
import android.util.Log;

import com.fivetrue.commonsdk.network.constants.MORTNetworkConstants;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Created by kwonojin on 15. 5. 28..
 */
public class MORTDeviceInfoSender {

    public static final String TAG = "MORTDeviceInfoSender";
    private static MORTDeviceInfoSender sInstance = null;
    private ArrayList<String> mClientIps = new ArrayList<>();

    public static MORTDeviceInfoSender get(){
        if(sInstance == null){
            sInstance = new MORTDeviceInfoSender();
        }
        return sInstance;
    }

    private MORTDeviceInfoSender(){
    }

    public void registerClientIp(String ip){
        if(!mClientIps.contains(ip)){
            mClientIps.add(ip);
        }
    }

    public void unregisterClientIp(String ip){
        if(mClientIps.contains(ip)){
            mClientIps.remove(ip);
        }
    }

    public void sendDataToClient(final String data){
        if(mClientIps != null){
            for(String ip : mClientIps){
                MORTNetworkData networkData = MORTNetworkData.fromJson(data);
                if(networkData != null && networkData.getType() != null){
                    switch(networkData.getType()){
                        case CONNECTION:

                            break;

                        case DEVICE:
                            if(networkData.getDevice() != null){
                                switch (networkData.getDevice()){
                                    case CAMERA:
                                        sendCameraData(data, ip);
                                        break;

                                    case SENSOR:
                                        sendSensorData(data, ip);
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

    public void sendCameraData(String data, String ip){
        if(!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(data)){
            new UDPSender().sendData(data, ip, MORTNetworkConstants.MORT_CAMERA_PORT);
        }
    }

    public void sendSensorData(String data, String ip){
            new UDPSender().sendData(data, ip, MORTNetworkConstants.MORT_DEVICE_SENSOR_PORT);
    }

    private class UDPSender extends Thread{


        private String mData = null;
        private String mIp = null;
        private int mPort = -1;

        public UDPSender(){

        }

        public void sendData(String data, String ip, int port){
            mIp = ip;
            mData = data;
            mPort = port;
            start();
        }

        @Override
        public void run() {
            if(mIp != null && mData != null && mPort > 0){
                try {
                    DatagramSocket s = new DatagramSocket();
                    InetAddress hostAddress = InetAddress.getByName(mIp);
                    DatagramPacket out = new DatagramPacket(mData.getBytes(), mData.getBytes().length, hostAddress, mPort);
                    s.send(out);
                } catch (SocketException e) {
                    e.printStackTrace();
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
