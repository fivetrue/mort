// IControlServerCallback.aidl
package com.fivetrue.commonsdk.service;

// Declare any non-default types here with import statements
import com.fivetrue.commonsdk.network.data.MORTNetworkData;

oneway interface IMORTServerNetworkCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.s
     */
      void onRecevedOperation(out MORTNetworkData data);

      void onReceivedControlView(out MORTNetworkData data);
}
