package com.fivetrue.commonsdk.device.data;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import com.fivetrue.commonsdk.device.DeviceObject;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.fivetrue.commonsdk.utils.BitmapConverter;

/**
 * Created by kwonojin on 15. 8. 12..
 */
public class Camera extends DeviceObject {

    public Bitmap image = null;
    public String message = null;

    public Camera(MORTNetworkData data){
        super(data);
        image = BitmapConverter.Base64StringToBitmap(data.getExtra());
        message = data.getMessage();
    }

    public Camera(Parcel source){
        super(source);
        image = source.readParcelable(null);
        message = source.readString();
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(image, flags);
        dest.writeString(message);
    }

    public static Creator<Camera> CREATOR = new Creator<Camera>() {
        @Override
        public Camera createFromParcel(Parcel source) {
            return new Camera(source);
        }

        @Override
        public Camera[] newArray(int size) {
            return new Camera[size];
        }
    };
}
