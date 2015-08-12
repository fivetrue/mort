package com.fivetrue.commonsdk.network.server;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Created by Fivetrue on 2015-03-20.
 */
public class MORTServer extends Thread {

    private static final String TAG = "ControlServer";

    private final int INVALID_VALUE = -1;

    private Context mContext = null;
    private ServerSocket mServerSocket = null;
    private int mPort = INVALID_VALUE;
    private boolean isRun = false;
    private MORTServerImpl mNetwork = null;
    private Gson mGson = new Gson();

    public MORTServer(Context context, int port){
        mContext = context;
        mPort = port;
    }

    public void setNetworkMortImplement(MORTServerImpl mortNetwork){
        mNetwork = mortNetwork;
    }


    @Override
    public synchronized void start() {
        super.start();
        isRun = true;
    }

    public void bind(){
        Log.d(TAG, "bind");
        start();
    }

    public void unbind(){
        Log.d(TAG, "unbind");
        isRun = false;
        if(mServerSocket != null){
            try {
                mServerSocket.close();
                mServerSocket = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        try{
            mServerSocket = new ServerSocket(mPort);
            WifiManager wm = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
            String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
            Log.d(TAG, ip);
            Log.d(TAG, "start");
            while(isRun){
                try {
                    Socket socket = mServerSocket.accept();
                    receivedSocket(socket, ip);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }catch (IOException e) {
            e.printStackTrace();
            start();
        }
    }

    public void setRun(boolean isRun){
        this.isRun = isRun;
    }

    private void receivedSocket(final Socket socket, final String serverIpAdress){
        new Thread(new Runnable() {
            @Override
            public void run() {
                DataInputStream in = null;
                try {
                    in = new DataInputStream(socket.getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                while(socket.isConnected()){
                    try{
                        String received = null;
                        try{
                            received = in.readUTF();
                        }catch (EOFException e){
                            e.printStackTrace();
                            onDisconnected(socket, null);
                            break;
                        }
                        Log.d(TAG, "in.readUTF() from = " + received);
                        MORTNetworkData data = null;
                        if (received != null) {
                            data = mGson.fromJson(received, MORTNetworkData.class);
                        }
                        if (data != null) {
                            if (data.getType().equals(MORTNetworkData.TYPE_CONNECTED)) {
                                onConnected(socket, data, serverIpAdress);
                            } else if (data.getType().equals(MORTNetworkData.TYPE_OPERATION)) {
                                onRecevedOperation(socket, data);
                            } else if (data.getType().equals(MORTNetworkData.TYPE_CONTROLVIEW)) {
                                onReceivedControlView(socket, data);
                            }else if (data.getType().equals(MORTNetworkData.TYPE_DISCONNECT)) {
                                onDisconnected(socket, data);
                            }
                        }
                    }catch (IOException ex){
                        ex.printStackTrace();
                    }
                }
            }
        }).start();
    }

    private synchronized void onRecevedOperation(Socket socket, MORTNetworkData data){
        if(mNetwork != null){
            mNetwork.onReceiveOperation(socket, data);
        }
    }

    private synchronized  void onReceivedControlView(Socket socket, MORTNetworkData data){
        if(mNetwork != null){
            mNetwork.onReceiveControlView(socket, data);
        }
    }

    private synchronized  void onConnected(Socket socket, MORTNetworkData data, String serverIpAddr) throws IOException {
        data.setExtra(serverIpAddr);
        if(mNetwork != null){
            mNetwork.onConnected(socket, data);
        }
    }

    private synchronized  void onDisconnected(Socket socket, MORTNetworkData data){
        if(mNetwork != null){
            mNetwork.onDisconnected(socket, data);
        }
    }
}
