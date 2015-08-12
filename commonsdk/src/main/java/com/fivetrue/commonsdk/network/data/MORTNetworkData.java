package com.fivetrue.commonsdk.network.data;


import android.os.Parcel;
import android.os.Parcelable;

import com.fivetrue.commonsdk.control.data.BaseMORTControlData;

import java.net.Socket;

/**
 * Created by Fivetrue on 2015-03-22.
 */
public class MORTNetworkData implements Parcelable{

    public static final String TYPE_CONNECTED = "connected";
    public static final String TYPE_CONTROLVIEW = "controlview";
    public static final String TYPE_OPERATION = "operation";
    public static final String TYPE_DISCONNECT = "disconnect";

    public static final String DEVICE_INFO_CAMERA = "device_info_camera";
    public static final String DEVICE_INFO_SENSOR = "device_info_sensor";

    public static final String ACTION_CHECK_CONTROLVIEW = "check_control";
    public static final String ACTION_ADD_CONTROL = "add_control";
    public static final String ACTION_DELETE_CONTROL = "del_control";

    public static final String CONTROLVIEW_HANDLE = "handle";
    public static final String CONTROLVIEW_ENGINE = "engine";
    public static final String CONTROLVIEW_CROCK = "crock";

    public static final String OPERATION_CENTER_LED = "center_led";
    public static final String OPERATION_BUZZER = "buzzer";
    public static final String OPERATION_SERVO = "servo";
    public static final String OPERATION_MOTOR = "motor";

    private String type = null;
    private String action = null;
    private String message = null;
    private String extra = null;
    private BaseMORTControlData control = null;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public MORTNetworkData(){};

    public MORTNetworkData(MORTNetworkData data){
        this.type = data.type;
        this.action = data.action;
        this.message = data.message;
        this.extra = data.extra;
    }

    public MORTNetworkData(Parcel in){
        type = in.readString();
        action = in.readString();
        message = in.readString();
        extra = in.readString();
        control = in.readParcelable(BaseMORTControlData.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(action);
        dest.writeString(message);
        dest.writeString(extra);
        dest.writeParcelable(control, flags);
    }

    static Creator<MORTNetworkData> CREATOR = new Creator<MORTNetworkData>() {
        @Override
        public MORTNetworkData createFromParcel(Parcel source) {
            return new MORTNetworkData(source);
        }

        @Override
        public MORTNetworkData[] newArray(int size) {
            return new MORTNetworkData[size];
        }
    };

    @Override
    public String toString() {
        return "MORTNetworkData{" +
                "type='" + type + '\'' +
                ", action='" + action + '\'' +
                ", message='" + message + '\'' +
                ", extra='" + extra + '\'' +
                ", control=" + control +
                '}';
    }
}
