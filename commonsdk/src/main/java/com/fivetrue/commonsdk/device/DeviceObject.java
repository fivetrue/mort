package com.fivetrue.commonsdk.device;

import android.os.Parcel;
import android.os.Parcelable;

import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.google.gson.Gson;

/**
 * Created by kwonojin on 15. 8. 12..
 */
public abstract class DeviceObject implements Parcelable{

    protected static Gson sGson = new Gson();

    public DeviceObject(){};
    public DeviceObject(MORTNetworkData data){

    }

    public DeviceObject(Parcel source){

    }

    public String convertJson(){
        return sGson.toJson(this);
    };

}
