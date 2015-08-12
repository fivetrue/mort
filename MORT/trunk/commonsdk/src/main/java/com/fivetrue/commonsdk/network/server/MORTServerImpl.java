package com.fivetrue.commonsdk.network.server;

import com.fivetrue.commonsdk.network.data.MORTNetworkData;

import java.net.Socket;

/**
 * Created by Fivetrue on 2015-03-22.
 */
public interface MORTServerImpl {

    public MORTNetworkData getDraftData();

    public void onReceiveOperation(Socket socket, MORTNetworkData data);

    public void onReceiveControlView(Socket socket, MORTNetworkData data);

    public void onConnected(Socket socket, MORTNetworkData data);

    public void onDisconnected(Socket socket, MORTNetworkData data);

}
