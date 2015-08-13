// IControlServerCallback.aidl
package com.fivetrue.commonsdk.service.network.client;

// Declare any non-default types here with import statements
import com.fivetrue.commonsdk.device.data.Sensor;
import com.fivetrue.commonsdk.device.data.Camera;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;

oneway interface IMORTClientNetworkCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.s
     */
      void onReceivedCameraData(out Camera data);
      void onReceivedSensorData(out Sensor data);
      void onConnected(String ip, out MORTNetworkData data);
      void onDisconnected();
      void onFailConnect();

}
