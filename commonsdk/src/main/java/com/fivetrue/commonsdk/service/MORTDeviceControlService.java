package com.fivetrue.commonsdk.service;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import java.util.ArrayList;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.spi.Log;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

public class MORTDeviceControlService extends IOIOService{
    public static final int INVALID_VALUE = -1;
    private static class Sensor{
        public int pin = INVALID_VALUE;
        public int analogValue = INVALID_VALUE;
        public boolean digitalValue = false;
        public boolean isDigital = false;

        @Override
        public String toString() {
            return "Sensor{" +
                    "pin=" + pin +
                    ", analogValue=" + analogValue +
                    ", digitalValue=" + digitalValue +
                    ", isDigital=" + isDigital +
                    '}';
        }
    }

    public static final String TAG = "DeviceControlService";
    public static final String ACTION_BIND = "com.fivetrue.mortdevicecontrol.bind";


    public static void bind(Context context, ServiceConnection serviceConn){
        Intent i = new Intent(context, MORTDeviceControlService.class);
        i.setAction(ACTION_BIND);
        context.bindService(i, serviceConn, Context.BIND_AUTO_CREATE);
    }


    public static void unbind(Context context, ServiceConnection serviceConn){
        context.unbindService(serviceConn);
    }

    public MORTDeviceControlService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        if(intent != null && intent.getAction().equals(ACTION_BIND)){
            return mBinder;
        }
        return null;
    }


    private IBinder mBinder = new IMORTDeviceControlService.Stub() {
        @Override
        public void writeAnalogSensorValue(int sensorPin, int value) throws RemoteException {

        }

        @Override
        public void writeDigitalSensorValue(int sensorPin, boolean value) throws RemoteException {
            Log.e(TAG, "writeDigitalSensorValue = pin = " + sensorPin
                    + " / value = " + value);
//            boolean hasSensor = false;
//            for(Sensor s : mSensorList){
//                if(s.pin == sensorPin){
//                    hasSensor = true;
//                    s.digitalValue = value;
//                }
//            }
//
//            if(!hasSensor){
//                Sensor sensor = new Sensor();
//                sensor.isDigital = true;
//                sensor.pin = sensorPin;
//                sensor.digitalValue = value;
//                mSensorList.add(sensor);
//            }
        }
    };


}
