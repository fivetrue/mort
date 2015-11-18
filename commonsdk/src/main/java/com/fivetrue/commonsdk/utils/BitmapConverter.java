package com.fivetrue.commonsdk.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Fivetrue on 2015-05-27.
 */
public class BitmapConverter {

    public static final String TAG = "BitmapConverter";

    public static String BitmapToBase64String(Bitmap bm, int compress){
        if(bm == null){
            return  null;
        }
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, compress, stream);
        byte[] byteArray = stream.toByteArray();
//        Log.e(TAG, " BitmapToBase64String : " + byteArray.length );
        String data = Base64.encodeToString(byteArray, Base64.DEFAULT);
        byteArray = null;
        stream.reset();
        try {
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static Bitmap Base64StringToBitmap(String data){
        if(data == null){
            return null;
        }
        byte[] byteArray = Base64.decode(data.getBytes(), Base64.DEFAULT);
        Bitmap bm = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        byteArray = null;
        return bm;
    }
}
