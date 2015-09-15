package com.fivetrue.childrencar;

import com.fivetrue.childrencar.network.server.MORTServer;
import com.fivetrue.commonsdk.MortApplication;
import com.fivetrue.commonsdk.network.constants.MORTNetworkConstants;

/**
 * Created by Fivetrue on 2015-09-15.
 */
public class HostApplication extends MortApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        MORTServer.init(this, MORTNetworkConstants.MORT_PORT);
    }
}
