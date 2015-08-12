package com.fivetrue.clubflash.ui.activity.flash;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.fivetrue.clubflash.R;
import com.fivetrue.clubflash.constants.NetworkActionConstatns;
import com.fivetrue.clubflash.ui.fragment.VisualizerFragment;
import com.fivetrue.light.FlashHelper;
import com.fivetrue.light.model.Flash;
import com.fivetrue.network.activity.BaseNetworkActivity;
import com.fivetrue.network.data.UdpData;
import com.fivetrue.network.helper.UdpSendHelper;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by Fivetrue on 2015-06-04.
 */
public class FlashActivity extends BaseNetworkActivity {
    public static final String TAG = "SearchHostActivity";
    private static Gson sGson = new Gson();
    private VisualizerFragment mVisualizerFragment = null;
    private Camera mCamera = null;
    private boolean isCameraOn = false;
    private ArrayList<Flash> mFlashInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        initView();
    }

    private void initView(){
        mVisualizerFragment = new VisualizerFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.layout_flash_anchor, mVisualizerFragment, VisualizerFragment.TAG).commit();
    }

    @Override
    protected void onReceivedFoundHost(final UdpData data) {

    }

    @Override
    protected void onReceivedUdpData(final UdpData data) {
        if(data != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(data.getHeader().equals(NetworkActionConstatns.ACTION_VISUALIZER)){
                        mVisualizerFragment.updateVisualizer(Base64.decode(data.getBody().getBytes(), Base64.DEFAULT));
                        if(data.getExtra() != null){
                            String json = data.getExtra();
                            setFlashInfo(FlashHelper.getInstance().jsonToList(json));
                        }
                    }else if(data.getHeader().equals(NetworkActionConstatns.ACTION_FLASH_LIST)){
//                        FlashData info = sGson.fromJson(json, FlashData.class);
//                        setFlashInfo(info);
                    }else if(data.getHeader().equals(NetworkActionConstatns.ACTION_FLASH_SINGLE)){
                        setFlashSingle(FlashHelper.getInstance().jsonToFlash(data.getBody()));
                    }else{
                        Toast.makeText(getApplicationContext(), data.toString(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        turnOffFlash(null);
    }

    private void setFlashSingle(Flash f){
        if(f != null){
            findViewById(R.id.layout_flash_anchor).setBackgroundColor(f.getColor());
        }
    }

    private void doClubFlash(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                View view = null;
                if(mVisualizerFragment != null){
                    view = mVisualizerFragment.getView();
                }
                if(isCameraOn){
                    turnOnFlash(view);
                }else{
                    turnOffFlash(view);
                }
                isCameraOn = !isCameraOn;
            }
        });
    }

    private void turnOnFlash(View view){
        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                mCamera = Camera.open();
                Camera.Parameters p = mCamera.getParameters();
                p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                mCamera.setParameters(p);
                mCamera.startPreview();
            }
            if(view != null){
                view.setBackgroundColor(Color.WHITE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception flashLightOn()",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void turnOffFlash(View view){
        try {
            if (getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_CAMERA_FLASH)) {
                mCamera.stopPreview();
                mCamera.release();
                mCamera = null;
            }
            if(view != null){
                view.setBackgroundColor(Color.BLACK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getBaseContext(), "Exception flashLightOff",
                    Toast.LENGTH_SHORT).show();
        }
    }

    synchronized private void setFlashInfo(ArrayList<Flash> flashs){
        mFlashInfo = flashs;
    }

    synchronized private ArrayList<Flash> getFlashInfo(){
        return mFlashInfo;
    }

    @Override
    protected void onConnectedService() {

    }

    @Override
    protected void onDisconnectedService() {

    }

    private UdpSendHelper.OnSearchHostListener onSearchHostListener = new UdpSendHelper.OnSearchHostListener() {
        @Override
        public void onStartSearch() {
        }

        @Override
        public void onCancelSearch() {
        }

        @Override
        public void onFinishSearch() {
        }
    };
}
