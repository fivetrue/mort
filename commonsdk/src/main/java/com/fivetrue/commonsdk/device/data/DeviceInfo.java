package com.fivetrue.commonsdk.device.data;

import android.os.Parcel;

import com.fivetrue.commonsdk.device.DeviceObject;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;

/**
 * Created by kwonojin on 15. 8. 12..
 */
public class DeviceInfo extends DeviceObject{


    public static enum Type{
        GRAVITY
    }

    public Type type = null;
    public float x;
    public float y;
    public float z;
    public String message = null;

    public DeviceInfo(MORTNetworkData data){
        super(data);
        DeviceInfo info = sGson.fromJson(data.getExtra(), getClass());
        x = info.x;
        y = info.y;
        z = info.z;
        type = info.type;
        message = info.message;
        info = null;
    }

    public DeviceInfo(Parcel source){
        super(source);
        x = source.readFloat();
        y = source.readFloat();
        z = source.readFloat();
        type = Type.valueOf(source.readString());
        message = source.readString();
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeFloat(z);
        dest.writeString(type.name());
        dest.writeString(message);
    }

    public static Creator<DeviceInfo> CREATOR = new Creator<DeviceInfo>() {
        @Override
        public DeviceInfo createFromParcel(Parcel source) {
            return new DeviceInfo(source);
        }

        @Override
        public DeviceInfo[] newArray(int size) {
            return new DeviceInfo[size];
        }
    };
}
