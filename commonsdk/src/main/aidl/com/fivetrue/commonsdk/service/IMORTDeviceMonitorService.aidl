// IControlServer.aidl
package com.fivetrue.commonsdk.service;

// Declare any non-default types here with import statements

interface IMORTDeviceMonitorService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    float getAnalogSensorValue(int sensorPin);

    boolean getDigitalSensorValue(int sensorPin);
}
