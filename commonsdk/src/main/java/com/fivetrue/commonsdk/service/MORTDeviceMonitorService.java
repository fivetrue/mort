package com.fivetrue.commonsdk.service;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.IOIO;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;

public class MORTDeviceMonitorService extends IOIOService implements IOIOLooper{

    public static final String ACTION_BIND = "com.fivetrue.service.mortmonitor.bind";

    public IOIO mIOIO = null;

    public static void bind(Context context, ServiceConnection serviceConn){
        Intent i = new Intent(context, MORTDeviceMonitorService.class);
        i.setAction(ACTION_BIND);
        context.bindService(i, serviceConn, Context.BIND_AUTO_CREATE);
    }

    public static void unbind(Context context, ServiceConnection serviceConn){
        context.unbindService(serviceConn);
    }

    public MORTDeviceMonitorService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        if(intent.getAction() != null && intent.getAction().equals(ACTION_BIND)){
            return mBinder;
        }
        return null;
    }

    @Override
    protected IOIOLooper createIOIOLooper() {
        return this;
    }

    @Override
    public void setup(IOIO ioio) throws ConnectionLostException, InterruptedException {
        mIOIO = ioio;
    }

    @Override
    public void loop() throws ConnectionLostException, InterruptedException {

    }

    @Override
    public void disconnected() {

    }

    @Override
    public void incompatible() {

    }

    @Override
    public void incompatible(IOIO ioio) {
        mIOIO = ioio;
        incompatible();;

    }

    private IBinder mBinder = new IMORTDeviceMonitorService.Stub() {
        @Override
        public float getAnalogSensorValue(int sensorPin) throws RemoteException {
            if(mIOIO != null){
                try {
                    return mIOIO.openAnalogInput(sensorPin).read();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ConnectionLostException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }

        @Override
        public boolean getDigitalSensorValue(int sensorPin) throws RemoteException {
            if(mIOIO != null){
                try {
                    return mIOIO.openDigitalInput(sensorPin).read();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ConnectionLostException e) {
                    e.printStackTrace();
                }
            }
            return false;
        }
    };



}
