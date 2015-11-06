package com.fivetrue.commonsdk.device.data;

import android.os.Parcel;

import com.fivetrue.commonsdk.device.DeviceObject;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;

/**
 * Created by kwonojin on 15. 8. 12..
 */
public class Sensor extends DeviceObject{

    public static enum Type{
        GRAVITY
    }

    public Sensor(){

    }

    public Sensor(MORTNetworkData data){
        super(data);
        Sensor sensor = sGson.fromJson(data.getExtra(), this.getClass());
        x = sensor.x;
        y = sensor.y;
        z = sensor.z;
        type = sensor.type;
    }

    public Sensor(Parcel source){
        super(source);
        x = source.readFloat();
        y = source.readFloat();
        z = source.readFloat();
        type = Type.valueOf(source.readString());
    }

    public Type type = null;
    public float x;
    public float y;
    public float z;

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

    }


    @Override
    public String toString() {
        return "Sensor{" +
                "type=" + type +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }

    public static Creator<Sensor> CREATOR = new Creator<Sensor>() {
        @Override
        public Sensor createFromParcel(Parcel source) {
            return new Sensor(source);
        }

        @Override
        public Sensor[] newArray(int size) {
            return new Sensor[size];
        }
    };
}
