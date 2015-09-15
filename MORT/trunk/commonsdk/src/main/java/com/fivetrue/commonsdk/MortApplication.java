package com.fivetrue.commonsdk;

import android.app.Application;
import android.os.Build;

import com.fivetrue.commonsdk.network.constants.MORTNetworkConstants;

/**
 * Created by Fivetrue on 2015-08-26.
 */
public class MortApplication extends Application{

    private static boolean isLowPerfomance = Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    private void initApplication(){

    }
}
