package com.fivetrue.childrencar.ui;

import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;

/**
 * Created by Fivetrue on 2015-04-25.
 */
public class MORTDeviceControlLooper extends BaseIOIOLooper {

    public interface DeviceControlImpl{
        void showVersionInfo(IOIO ioio, String msg);
        void disconnected();
    }

    private static final float SERVO_MOTOR_VALUE_MAX = 0.1f;
    private static final float SERVO_MORTOR_VALUE_MIN = 0.01f;
    /** The on-board LED. */
    private static final int PIN_CENTER_LED = 0;
    private static final int PIN_BUZZER = 11;
    private static final int PIN_MOTOR = 10;
    private static final int PIN_SERVO_MOTOR = 5;

    private static final int PIN_LEFT_MOTOR_SPEED_PIN = 18;
    private static final int PIN_LEFT_MOTOR_IN_1 = 17;
    private static final int PIN_LEFT_MOTOR_IN_2 = 16;

    private DigitalOutput mCenterLED = null;
    private DigitalOutput mMotor = null;
    private PwmOutput mServoMotor;

    private PwmOutput mLeftMotorSpeed = null;
    private DigitalOutput mLeftMotor_In1 = null;
    private DigitalOutput mLeftMotor_In2 = null;

    private IOIO mIOIO = null;

    private boolean isCenterLed = false;
    private boolean isMotor = false;

    private float mServoMotorDegree = 0.01f;

    private DeviceControlImpl mControlImpl = null;

    public MORTDeviceControlLooper(DeviceControlImpl impl){
        mControlImpl = impl;
    }

    /**
     * Called every time a connection with IOIO has been established.
     * Typically used to open pins.
     *
     * @throws ioio.lib.api.exception.ConnectionLostException
     *             When IOIO connection is lost.
     *
     */

    @Override
    protected void setup() throws ConnectionLostException {
        mIOIO = ioio_;
        mControlImpl.showVersionInfo(mIOIO, "IOIO connected!");
//        mCenterLED = mIOIO.openDigitalOutput(PIN_CENTER_LED, false);
//        mServoMotor = mIOIO.openPwmOutput(PIN_SERVO_MOTOR, 50);
//        mMotor = mIOIO.openDigitalOutput(PIN_MOTOR);
        initMotors(mIOIO);
    }

    private void initMotors(IOIO ioio) throws ConnectionLostException {
        mLeftMotorSpeed = ioio.openPwmOutput(PIN_LEFT_MOTOR_SPEED_PIN, 0);
        mLeftMotor_In1 = ioio.openDigitalOutput(PIN_LEFT_MOTOR_IN_1);
        mLeftMotor_In2 = ioio.openDigitalOutput(PIN_LEFT_MOTOR_IN_2);
    }

    /**
     * Called repetitively while the IOIO is connected.
     *
     * @throws ConnectionLostException
     *             When IOIO connection is lost.
     * @throws InterruptedException
     * 				When the IOIO thread has been interrupted.
     *
     * @see ioio.lib.util.IOIOLooper#loop()
     */
    @Override
    public void loop() throws ConnectionLostException, InterruptedException {
    }

    /**
     * Called when the IOIO is disconnected.
     *
     * @see ioio.lib.util.IOIOLooper#disconnected()
     */
    @Override
    public void disconnected() {
        mControlImpl.disconnected();
    }

    /**
     * Called when the IOIO is connected, but has an incompatible firmware version.
     *
     * @see ioio.lib.util.IOIOLooper#incompatible(ioio.lib.api.IOIO)
     */
    @Override
    public void incompatible() {
        mControlImpl.showVersionInfo(mIOIO, "Incompatible firmware version!");
    }

    public void setOnCenterLed(final boolean onCenterLed){
        try {
            isCenterLed = onCenterLed;
            mCenterLED.write(isCenterLed);
        } catch (ConnectionLostException e) {
            e.printStackTrace();
        }
    }

    public void setOnBuzzer(final int freqHz, final long milliSecond){
        try {
            PwmOutput output = ioio_.openPwmOutput(PIN_BUZZER, freqHz);
            output.setDutyCycle(0.5f);
            try {
                Thread.sleep(milliSecond);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            output.close();
        } catch (ConnectionLostException e) {
            e.printStackTrace();
        }
    }

    public void setLeftMotorCommand(int speed, boolean reverse){
        try {
            mLeftMotorSpeed.setDutyCycle(1.0f);
            mLeftMotor_In1.write(!reverse);
            mLeftMotor_In2.write(reverse);
        } catch (ConnectionLostException e) {
            e.printStackTrace();
        }
    }

    public void setRightMotorCommand(int speed, boolean reverse){

    }

    public void setServoMotorValue(final float value){
        if(value >= SERVO_MORTOR_VALUE_MIN && value <= SERVO_MOTOR_VALUE_MAX && mServoMotor != null){
            try {
                mServoMotor.setDutyCycle(value);
            } catch (ConnectionLostException e) {
                e.printStackTrace();
            }
        }
    }

    public void setMotorOn(boolean isOn){
        try {
            isMotor = isOn;
            mMotor.write(isMotor);
        } catch (ConnectionLostException e) {
            e.printStackTrace();
        }
    }

    public boolean isCenterLed() {
        return isCenterLed;
    }

    public boolean isMotor() {
        return isMotor;
    }
}
