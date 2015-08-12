package com.fivetrue.common.utils;

import android.util.Log;

/**
 * Created by Fivetrue on 2015-05-30.
 */
public class Logger {

    private static final boolean isShow = true;

    public static void d(String tag, String msg){
        if(isShow)
            Log.d(tag,  msg);
    }

    public static void e(String tag, String msg){
        if(isShow)
            Log.e(tag, msg);
    }


}
