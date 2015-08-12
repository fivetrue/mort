package com.fivetrue.commonsdk.network.client;

import com.fivetrue.commonsdk.network.data.MORTNetworkData;

/**
 * Created by Fivetrue on 2015-03-22.
 */
public interface MORTClientImpl {
    public MORTNetworkData makeSendData();
    public void receivedData(MORTNetworkData data);
}
