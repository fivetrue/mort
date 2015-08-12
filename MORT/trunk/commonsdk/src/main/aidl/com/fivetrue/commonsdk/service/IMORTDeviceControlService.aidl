// IControlServer.aidl
package com.fivetrue.commonsdk.service;

// Declare any non-default types here with import statements

interface IMORTDeviceControlService {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
     void writeAnalogSensorValue(int sensorPin, int value);

     void writeDigitalSensorValue(int sensorPin, boolean value);
}
