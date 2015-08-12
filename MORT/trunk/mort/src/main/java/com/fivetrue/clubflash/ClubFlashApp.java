package com.fivetrue.clubflash;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;

import com.fivetrue.light.FlashHelper;
import com.fivetrue.network.helper.UdpSendHelper;
import com.fivetrue.network.service.NetworkServerService;
import com.fivetrue.player.helper.MusicHelper;

/**
 * Created by Fivetrue on 2015-05-30.
 */
public class ClubFlashApp extends Application implements Application.ActivityLifecycleCallbacks{

    @Override
    public void onCreate() {
        super.onCreate();
        MusicHelper.init(this);
        UdpSendHelper.init(this);
        FlashHelper.init(this);
        registerActivityLifecycleCallbacks(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity){};

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
