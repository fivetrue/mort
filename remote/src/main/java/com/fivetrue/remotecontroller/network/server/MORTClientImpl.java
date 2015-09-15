package com.fivetrue.remotecontroller.network.server;

import com.fivetrue.commonsdk.network.data.MORTNetworkData;

/**
 * Created by Fivetrue on 2015-03-22.
 */
public interface MORTClientImpl {
    public MORTNetworkData makeSendData();
    public void receivedData(MORTNetworkData data);
}
