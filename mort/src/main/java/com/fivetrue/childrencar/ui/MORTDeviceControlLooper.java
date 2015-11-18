package com.fivetrue.childrencar.ui;

import com.fivetrue.commonsdk.device.control.ControlOperation;

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

    private static final int MAX_PWM_VALUE = 0xFF;
    private static final float SERVO_MOTOR_VALUE_MAX = 0.1f;
    private static final float SERVO_MORTOR_VALUE_MIN = 0.01f;
    /** The on-board LED. */
    private static final int PIN_CENTER_LED = 0;
    private static final int PIN_BUZZER = 11;
    private static final int PIN_MOTOR = 10;
    private static final int PIN_SERVO_MOTOR = 5;

    private static final int PIN_MOTOR_IN_1 = 1;
    private static final int PIN_MOTOR_IN_2 = 2;

    private static final int PIN_MOTOR_IN_3 = 3;
    private static final int PIN_MOTOR_IN_4 = 4;

    private DigitalOutput mCenterLED = null;
    private DigitalOutput mMotor = null;
    private PwmOutput mServoMotor;

    private PwmOutput mLeftMotorSpeed = null;

    //Right
    private DigitalOutput mMotor_In1 = null;
    private DigitalOutput mMotor_In2 = null;

    //Left
    private DigitalOutput mMotor_In3 = null;
    private DigitalOutput mMotor_In4 = null;

    private IOIO mIOIO = null;

    private boolean isCenterLed = false;
    private boolean isMotor = false;

    private float mServoMotorDegree = 0.01f;

    private DeviceControlImpl mControlImpl = null;

    private ControlOperation mPreControlOperation = null;

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
        mMotor_In1 = ioio.openDigitalOutput(PIN_MOTOR_IN_1);
        mMotor_In2 = ioio.openDigitalOutput(PIN_MOTOR_IN_2);
//        mMotor_in3 = ioio.openPwmOutput(PIN_MOTOR_IN_3, 0);

        mMotor_In3 = ioio.openDigitalOutput(PIN_MOTOR_IN_3);
        mMotor_In4 = ioio.openDigitalOutput(PIN_MOTOR_IN_4);
//        mMotor_in13 = ioio.openPwmOutput(PIN_MOTOR_IN_13, 0);
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
        doSpinMoter(mPreControlOperation);
        Thread.sleep(10L);
    }
    /**
     * Called when the IOIO is disconnected.
     *
     * @see ioio.lib.util.IOIOLooper#disconnected()
     */
    @Override
    public void disconnected() {
        mControlImpl.disconnected();
        try {
            mMotor_In1.write(false);
            mMotor_In2.write(false);
//            mMotor_in3.setDutyCycle(0f);
        } catch (ConnectionLostException e) {
            e.printStackTrace();
        }

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

    public void setControlOperation(ControlOperation operation){
//        if(operation != null){
//            int x = 0;
//            int y = 0;
//            if(mPreControlOperation != null){
//                if(mPreControlOperation.getX() == operation.getX()
//                        && mPreControlOperation.getY() == operation.getY()){
//                    return;
//                }
//            }else{
                mPreControlOperation = operation;
//        doSpinMoter(mPreControlOperation);
//            }
//        }
    }

    private void doSpinMoter(ControlOperation operation){
        if(operation != null && mMotor_In1 != null && mMotor_In2 != null
                && mMotor_In3 != null && mMotor_In4 != null){
//            Log.d("doSpinMoter", operation.toString());
            boolean doRight = operation.getX() > 0;
            boolean doForward = operation.getY() > 0;
            try {
                //Stop
                if(operation.getY() == 0 && operation.getX() == 0){
                    mMotor_In1.write(false);
                    mMotor_In3.write(false);

                    mMotor_In2.write(false);
                    mMotor_In4.write(false);
                }else{
                    if(operation.getX() == 0){
                        mMotor_In1.write(doForward);
                        mMotor_In3.write(doForward);

                        mMotor_In2.write(!doForward);
                        mMotor_In4.write(!doForward);
                    }else{
                        mMotor_In1.write(!doRight);
                        mMotor_In3.write(doRight);

                        mMotor_In2.write(doRight);
                        mMotor_In4.write(!doRight);
                    }
                }
            }catch(ConnectionLostException e){
                e.printStackTrace();
            }
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
            mMotor_In1.write(!reverse);
            mMotor_In2.write(reverse);
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
