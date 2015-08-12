package com.fivetrue.commonsdk.network.client;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.fivetrue.commonsdk.network.constants.MORTNetworkConstants;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.fivetrue.commonsdk.preference.SharedPrefrenceManager;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by Fivetrue on 2015-03-22.
 */
public class MORTClient{

    public interface MORTClientNetworkListener{
        public void onConnected(Socket socket, String ipAddress, MORTNetworkData data);
        public void onDisconnectd();
        public void onFailConnect();
    }

    private static final String LAST_CONNECTED_SERVER = "lastConnServer";
    private static final String TAG = "RemoteClient";
    private static int SEARCH_TASK = 20;
    private static int SEARCH_TIMEOUT = 1000;

    private final int INVALID_VALUE = -1;
    private Activity mActivity = null;
    private Socket mSocket = null;
    private MORTClientNetworkListener mMortClientNetworkListener = null;
    private ProgressDialog mProgressDialog = null;
    private SharedPrefrenceManager mPref = null;

    private DeviceSearchThread[] mDeviceSearchTasks = new DeviceSearchThread[SEARCH_TASK];

    public MORTClient(Activity activity){
        mActivity = activity;
        mPref = new SharedPrefrenceManager(activity, TAG);
        mProgressDialog = new ProgressDialog(mActivity);
    }

    private MORTClientNetworkListener mortClientNetworkListener = new MORTClientNetworkListener() {
        @Override
        public void onFailConnect() {
            if(mMortClientNetworkListener != null && mActivity != null){
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMortClientNetworkListener.onFailConnect();
                    }
                });
            }
        }

        @Override
        public void onConnected(final Socket socket, final String ipAddress, final MORTNetworkData data) {
            mSocket = socket;
            mPref.savePref(LAST_CONNECTED_SERVER, ipAddress);
            if(mMortClientNetworkListener != null && mActivity != null){
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissProgressDialog();
                        mMortClientNetworkListener.onConnected(socket, ipAddress, data);
                    }
                });
            }
        }

        @Override
        public void onDisconnectd() {
            if(mMortClientNetworkListener != null && mActivity != null){
                mActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mMortClientNetworkListener.onDisconnectd();
                    }
                });
            }
        }
    };

    /**
     * MortDevice 찾기
     */
    public void searchMortDevice(){
        showProgressDialog("검색", "디바이스 찾는 중..");
        final String lastIp = mPref.loadStringPref(LAST_CONNECTED_SERVER, null);
        if(lastIp != null){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    DataOutputStream out = null;
                    DataInputStream in = null;

                    try {
                        SocketAddress addr = new InetSocketAddress(lastIp, MORTNetworkConstants.MORT_PORT);
                        Socket socket = new Socket();
                        socket.connect(addr, MORTNetworkConstants.SEARCH_SERVER_TIME_OUT);
                        if(socket.isConnected()){
                            Log.e(TAG, "Connected : " + toString());
                            out = new DataOutputStream(socket.getOutputStream());
                            MORTNetworkData data = null;
                            Gson gson = new Gson();
                            if(out != null){
                                data = new MORTNetworkData();
                                data.setType(MORTNetworkData.TYPE_CONNECTED);
                                String json = gson.toJson(data);
                                Log.e(TAG, "Object to json : " + json);
                                out.writeUTF(json);
                            }
                            in = new DataInputStream(socket.getInputStream());
                            if(in != null){
                                String message = in.readUTF();
                                Log.e(TAG, "Received json : " + message);
                                data = gson.fromJson(message, MORTNetworkData.class);
                                data.setMessage(lastIp);
                                Log.d(TAG, data.toString());
                            }
                            mortClientNetworkListener.onConnected(socket, lastIp, data);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        doSearchThead();
                    }
                }
            }).start();
        }else{
            doSearchThead();
        }
    }

    private void doSearchThead(){
        for(int i = 0 ; i < mDeviceSearchTasks.length ; i++){
            WifiManager wm = (WifiManager) mActivity.getSystemService(Context.WIFI_SERVICE);
            final String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            mDeviceSearchTasks[i] = new DeviceSearchThread(i, ip, mortClientNetworkListener);
            mDeviceSearchTasks[i].start();
        }
    }

    public void showProgressDialog(String title, String message){
        if(mActivity != null && !mProgressDialog.isShowing()){
            mProgressDialog.setTitle(title);
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    public void dismissProgressDialog(){
        if(mProgressDialog != null && mProgressDialog.isShowing()){
            if(mProgressDialog.getWindow() != null){
                mProgressDialog.dismiss();
            }
        }
    }

    public void sendData(MORTClientImpl client){
        sendData(mSocket, client);
    }

    public void sendData(final Socket socket, final MORTClientImpl client){
        if(socket != null && client != null){
            Log.d(TAG, "sendData = " + client.makeSendData().toString());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if(socket.isConnected()){
                        if(client.makeSendData() != null){
                            DataOutputStream out = null;
                            DataInputStream in = null;
                            try {
                                out = new DataOutputStream(socket.getOutputStream());
                                in = new DataInputStream(socket.getInputStream());
                                Gson gson = new Gson();
                                if(out != null && client.makeSendData() != null){
                                    out.writeUTF(gson.toJson(client.makeSendData()));
                                }
                                if(in != null){
                                    client.receivedData(gson.fromJson(in.readUTF(), MORTNetworkData.class));
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }else{
                        mortClientNetworkListener.onDisconnectd();
                    }
                }
            }).start();
        }
    }

    public void setMORTClientNetworkListener(MORTClientNetworkListener l){
        this.mMortClientNetworkListener = l;
    }
    private static class DeviceSearchThread extends Thread{
        public static final String TAG = "DeviceSeasrchThread";
        public static boolean isRun = true;
        private static int SUBNET_MASK_COUNT = 255;
        private static int SEARCH_COUNT = Math.round((float)(SUBNET_MASK_COUNT / SEARCH_TASK));
        private int mTaksNumber = 0;
        private MORTClientNetworkListener mClientNetworkListener = null;
        private int mStartIndex = 0;
        private int mEndIndex = 0;
        private String mIpAddress = null;


        public DeviceSearchThread(int taskNumber, String ipAddr, MORTClientNetworkListener l){
            this.mTaksNumber = taskNumber;
            this.mIpAddress = ipAddr;
            this.mClientNetworkListener = l;
            mStartIndex = SEARCH_COUNT * taskNumber;
            mEndIndex = SEARCH_TASK == taskNumber + 1 ? SUBNET_MASK_COUNT : SEARCH_COUNT * (taskNumber + 1);
        }

        public static void setRun(boolean b){
            isRun = b;
        }


        @Override
        public void run() {
            super.run();
            Log.d(TAG, "ip = " + mIpAddress);
            int lastDot = mIpAddress.lastIndexOf(".");
            String preIpaddress = mIpAddress.substring(0, lastDot);
            int myIp = Integer.parseInt(mIpAddress.substring(lastDot + 1));
            Log.d(TAG, "myIp = " + myIp);
            Log.d(TAG, "preIpaddress = " + preIpaddress);
            Log.d(TAG, "ip = " + mIpAddress);

            for(int i = mStartIndex ; i <= mEndIndex ; i ++){
                if(!isRun){
                    Log.e(this.getClass().getSimpleName(), "onCancelled = " + toString());
                    return;
                }

                Log.e("task", "subnet  = " + i + " /// "  +toString());
                if(myIp == i){
                    continue;
                }
                String remoteIp = preIpaddress + "." + i;
                Log.d(TAG, remoteIp);

                DataOutputStream out = null;
                DataInputStream in = null;

                try {
                    SocketAddress addr = new InetSocketAddress(remoteIp, MORTNetworkConstants.MORT_PORT);
                    Socket socket = new Socket();
                    connect(socket, addr);
                    if(socket.isConnected()){
                        Log.e(TAG, "Connected : " + toString());
                        out = new DataOutputStream(socket.getOutputStream());
                        MORTNetworkData data = null;
                        Gson gson = new Gson();
                        if(out != null){
                            data = new MORTNetworkData();
                            data.setType(MORTNetworkData.TYPE_CONNECTED);
                            String json = gson.toJson(data);
                            Log.e(TAG, "Object to json : " + json);
                            out.writeUTF(json);
                        }
                        in = new DataInputStream(socket.getInputStream());
                        if(in != null){
                            String message = in.readUTF();
                            Log.e(TAG, "Received json : " + message);
                            data = gson.fromJson(message, MORTNetworkData.class);
                            data.setMessage(remoteIp);
                            Log.d(TAG, data.toString());
                        }
                        mClientNetworkListener.onConnected(socket, remoteIp, data);
                        return;
                    }else{
                        continue;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        if(in != null){
                            in.close();
                        }
                        if(out != null){
                            out.close();
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            mClientNetworkListener.onFailConnect();
        }

        private synchronized  void connect(Socket sock, SocketAddress addr){
            if(sock != null && addr != null && isRun){
                try{
                    sock.connect(addr, MORTNetworkConstants.SEARCH_SERVER_TIME_OUT);
                    isRun = false;
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }

        @Override
        public String toString() {
            return "DeviceSearchAsyncTask{" +
                    "mTaksNumber=" + mTaksNumber +
                    ", mStartIndex=" + mStartIndex +
                    ", mEndIndex=" + mEndIndex +
                    '}';
        }
    }
}
