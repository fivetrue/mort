// IControlServer.aidl
package com.fivetrue.commonsdk.service.network;

// Declare any non-default types here with import statements
import com.fivetrue.commonsdk.service.network.IMORTServerNetworkCallback;

interface IMORTServerNetworkService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void registerCallback(IMORTServerNetworkCallback callback);
    void unregisterCallback(IMORTServerNetworkCallback callback);
    void responseOperationData(String response);
    void responseControlViewData(String response);
    void sendBroadcastToClient(String data);
}
