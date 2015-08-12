package com.fivetrue.commonsdk.network.server;

import com.fivetrue.commonsdk.network.data.MORTNetworkData;

import java.net.Socket;

/**
 * Created by Fivetrue on 2015-03-22.
 */
public interface MORTServerImpl {

    public MORTNetworkData getDraftData();

    public void onReceiveOperation(Socket socket, MORTNetworkData data);

    public void onReceiveDeviceInfo(Socket socket, MORTNetworkData data);

    public void onReceiveConnection(Socket socket, MORTNetworkData data, String serverAddr);
}
