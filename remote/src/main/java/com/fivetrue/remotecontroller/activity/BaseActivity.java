package com.fivetrue.remotecontroller.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.fivetrue.commonsdk.network.client.MORTClient;
import com.fivetrue.commonsdk.network.client.MORTClientImpl;



abstract public class BaseActivity extends FragmentActivity{

    private ProgressDialog mProgress = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
