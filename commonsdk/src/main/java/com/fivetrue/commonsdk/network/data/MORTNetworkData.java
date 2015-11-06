package com.fivetrue.commonsdk.network.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.Gson;

public class MORTNetworkData implements Parcelable {

    public static enum Type{
        CONNECTION,
        DEVICE,
        OPERATION,
    }

    public static enum Connection{
        CONNECTED,
        DISCONNECTED
    }

    public static enum Device{
        CAMERA,
        SENSOR
    }

    private static final Gson sGson = new Gson();

    private Type type = null;
    private Connection connection = null;
    private Device device = null;
    private String message = null;
    private String extra = null;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
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

    public static MORTNetworkData fromJson(String json){
        MORTNetworkData data = sGson.fromJson(json, MORTNetworkData.class);
        return data;
    }

    public MORTNetworkData(MORTNetworkData data){
        this.type = data.type;
        this.device = data.device;
        this.connection = data.connection;
        this.message = data.message;
        this.extra = data.extra;
    }

    public MORTNetworkData(Parcel in){
        type = Type.valueOf(in.readString());
        connection = Connection.valueOf(in.readString());
        device = Device.valueOf(in.readString());
        message = in.readString();
        extra = in.readString();
    }

    public String convertJson(){
        return sGson.toJson(this);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type.name());
        dest.writeString(connection.name());
        dest.writeString(device.name());
        dest.writeString(message);
        dest.writeString(extra);
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
                "type=" + type +
                ", connection=" + connection +
                ", device=" + device +
                ", message='" + message + '\'' +
                ", extra='" + extra + '\'' +
                '}';
    }
}