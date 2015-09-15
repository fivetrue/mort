package com.fivetrue.remotecontroller.network.server;

import android.util.Log;

import com.fivetrue.commonsdk.network.constants.MORTNetworkConstants;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by kwonojin on 15. 8. 12..
 */
public class MORTDeviceInfoServer {
    public interface OnReceivedMortDeivceInfoListener {
        void onReceivcedCameraData(MORTNetworkData data);
        void onReceivcedSensorData(MORTNetworkData data);
    }

    private UDPServer mCameraServer = null;
    private UDPServer mSensorServer = null;

    private OnReceivedMortDeivceInfoListener mOnReceivedListener = null;

    public MORTDeviceInfoServer(OnReceivedMortDeivceInfoListener l){
        mOnReceivedListener = l;
    }

    public void startCameraServer(){
        if(mCameraServer == null){
            mCameraServer = new UDPServer(mOnReceivedListener, MORTNetworkConstants.MORT_CAMERA_PORT);
        }
        mCameraServer.onStartServer();
    }

    public void startSensorServer(){
        if(mSensorServer == null){
            mSensorServer = new UDPServer(mOnReceivedListener, MORTNetworkConstants.MORT_DEVICE_SENSOR_PORT);
        }
        mSensorServer.onStartServer();
    }

    public void stopCameraServer(){
        if(mCameraServer != null){
            mCameraServer.onStopServer();
            mCameraServer = null;
        }
    }

    public void stopSensorServer(){
        if(mSensorServer != null){
            mSensorServer.onStopServer();
            mSensorServer = null;
        }
    }

    private static class UDPServer extends Thread{
        private boolean isRun = false;
        private DatagramSocket mSocket = null;
        private OnReceivedMortDeivceInfoListener mOnReceivedListener = null;
        private int mPort = -1;

        public UDPServer(OnReceivedMortDeivceInfoListener l, int port){
            mOnReceivedListener = l;
            mPort = port;
        }

        public void onStartServer(){
            isRun = true;
            start();
        }
        public void onStopServer(){
            isRun = false;
        }

        @Override
        public void run() {
            Gson gson = new Gson();
            try {
                mSocket = new DatagramSocket(mPort);
            } catch (SocketException e) {
                e.printStackTrace();
            }
            while(isRun){
                try {
                    byte[] buf = new byte[1024 * 100];
                    DatagramPacket dgp = new DatagramPacket(buf, buf.length);
                    mSocket.receive(dgp);
                    String packet = new String(dgp.getData(), 0, dgp.getLength());
                    MORTNetworkData data = gson.fromJson(packet, MORTNetworkData.class);
                    if(mOnReceivedListener != null){
                        if(mPort == MORTNetworkConstants.MORT_CAMERA_PORT){
                            mOnReceivedListener.onReceivcedCameraData(data);
                        }else if(mPort == MORTNetworkConstants.MORT_DEVICE_SENSOR_PORT){
                            mOnReceivedListener.onReceivcedSensorData(data);
                        }
                    }
                    buf = null;
                    dgp = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
