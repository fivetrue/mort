package com.fivetrue.commonsdk.device.control;

import android.os.Parcel;

import com.fivetrue.commonsdk.device.DeviceObject;

/**
 * Created by kwonojin on 15. 8. 12..
 */
public class ControlOperation extends DeviceObject {

    public static final int TOUCH_ARRAY_COUNT = 5;
    private int x = 0;
    private int y = 0;

    public ControlOperation(){

    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public ControlOperation(Parcel in){
        x = in.readInt();
        y = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(x);
        dest.writeInt(y);
    }

    @Override
    public String toString() {
        return "ControlOperation{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
