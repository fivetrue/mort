package com.fivetrue.remotecontroller.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.fivetrue.commonsdk.network.client.MORTClient;
import com.fivetrue.commonsdk.network.client.MORTClientImpl;



abstract public class BaseActivity extends FragmentActivity implements MORTClient.MORTClientNetworkListener{

    private static MORTClient mMortClient = null;
    private ProgressDialog mProgress = null;

    protected void searchMortDevice(){
        mMortClient.searchMortDevice();
    }
    protected void sendData(MORTClientImpl client){
        mMortClient.sendData(client);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(mMortClient == null){
            mMortClient = new MORTClient(this);
            mMortClient.setMORTClientNetworkListener(this);
        }
        mProgress = new ProgressDialog(this);
    }

    protected void showProgress(String title, String message){
        if(mProgress != null && !mProgress.isShowing()){
            mProgress.setTitle(title);
            mProgress.setMessage(message);
            mProgress.show();
        }
    }

    protected void dismissProgress(){
        if(mProgress != null && mProgress.isShowing()){
            mProgress.dismiss();
        }
    }
}
