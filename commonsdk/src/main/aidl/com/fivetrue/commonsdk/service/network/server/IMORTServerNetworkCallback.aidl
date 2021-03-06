// IControlServerCallback.aidl
package com.fivetrue.commonsdk.service.network.server;

// Declare any non-default types here with import statements
import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.fivetrue.commonsdk.device.data.Sensor;
import com.fivetrue.commonsdk.device.data.Camera;
import com.fivetrue.commonsdk.device.data.DeviceInfo;

oneway interface IMORTServerNetworkCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.s
     */
      void onRecevedOperation(out MORTNetworkData data);
      void onRecevedDeviceInfo(out DeviceInfo data);
}
