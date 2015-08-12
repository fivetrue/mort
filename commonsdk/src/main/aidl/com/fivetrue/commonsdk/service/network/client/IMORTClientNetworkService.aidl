// IControlServer.aidl
package com.fivetrue.commonsdk.service.network.client;

// Declare any non-default types here with import statements
import com.fivetrue.commonsdk.service.network.client.IMORTClientNetworkCallback;

interface IMORTClientNetworkService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void registerCallback(IMORTClientNetworkCallback callback);
    void unregisterCallback(IMORTClientNetworkCallback callback);
}
