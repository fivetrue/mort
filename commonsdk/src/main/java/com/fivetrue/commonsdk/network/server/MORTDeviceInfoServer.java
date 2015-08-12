package com.fivetrue.commonsdk.network.server;

import android.util.Log;

import com.fivetrue.commonsdk.network.constants.MORTNetworkConstants;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.google.gson.Gson;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * Created by Fivetrue on 2015-03-20.
 */
public class MORTDeviceInfoServer {
    public interface OnReceivedMortDeivceInfoListener {
        void onReceivcedCameraData(MORTNetworkData data);
        void onReceivcedSensorData(MORTNetworkData data);
    }

    private OnReceivedMortDeivceInfoListener mOnReceivedListener = null;

    public MORTDeviceInfoServer(OnReceivedMortDeivceInfoListener l){
        mOnReceivedListener = l;
    }

    public void onStartCameraServer(){
        new UDPServer(mOnReceivedListener, MORTNetworkConstants.MORT_CAMERA_PORT).onStartServer();
    }

    public void onStartSensorServer(){
        new UDPServer(mOnReceivedListener, MORTNetworkConstants.MORT_DEVICE_SENSOR_PORT).onStartServer();
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
