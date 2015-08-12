// IControlServerCallback.aidl
package com.fivetrue.commonsdk.service.network.client;

// Declare any non-default types here with import statements
import com.fivetrue.commonsdk.device.data.Sensor;
import com.fivetrue.commonsdk.device.data.Camera;

oneway interface IMORTClientNetworkCallback {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.s
     */
      void onReceivedCameraData(out Camera data);
      void onReceivedSensorData(out Sensor data);
}
