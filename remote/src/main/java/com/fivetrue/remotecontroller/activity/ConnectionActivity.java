package com.fivetrue.remotecontroller.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.fivetrue.commonsdk.network.data.MORTNetworkData;

import java.net.Socket;

/**
 * Created by Fivetrue on 2015-04-19.
 */
public class ConnectionActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchMortDevice();
    }

    @Override
    public void onConnected(Socket socket, String ipAddress, MORTNetworkData data) {
        Toast.makeText(this, ipAddress, Toast.LENGTH_SHORT).show();
        Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ControlViewActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDisconnectd() {

    }

    @Override
    public void onFailConnect() {

    }
}
