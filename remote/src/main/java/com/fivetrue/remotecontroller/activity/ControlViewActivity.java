package com.fivetrue.remotecontroller.activity;

import android.graphics.Bitmap;
import android.hardware.SensorEvent;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import com.fivetrue.commonsdk.network.client.MORTClientImpl;
import com.fivetrue.commonsdk.network.client.MORTDeviceInfoServer;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.fivetrue.commonsdk.utils.BitmapConverter;
import com.fivetrue.remotecontroller.fragment.control.CarStateInfoFragment;
import com.fivetrue.remotecontroller.fragment.control.ControlScreenFragment;
import com.google.gson.Gson;


/**
 * Created by Fivetrue on 2015-04-19.
 */
public class ControlViewActivity extends BaseActivity{
    public static final String TAG = "ControlViewActivity";
    public static final int VIEW_PAGER_ID = 0x0411;
    private MORTNetworkData mMortNetworkData = null;
    private float value = 0.05f;

    private ControlScreenFragment mControlScreenFragment = null;
    private CarStateInfoFragment mCarStateFragment = null;
    private Gson mGson = new Gson();

    private ViewGroup mLayoutScreen = null;
    private ViewGroup mLayoutSystemInfo = null;
    private ViewGroup mLayoutCarInfo = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        initViews();
        initData();
        showProgress("확인 중", "확인 중.....");
    }

    private void initViews(){
        setContentView(R.layout.activity_control);
        mLayoutScreen = (ViewGroup) findViewById(R.id.layout_screen);
        mLayoutCarInfo = (ViewGroup) findViewById(R.id.layout_car_infomation);
        mLayoutSystemInfo  = (ViewGroup) findViewById(R.id.layout_system_infomation);
        mControlScreenFragment = new ControlScreenFragment();
        mCarStateFragment = new CarStateInfoFragment();

        getSupportFragmentManager().beginTransaction()
                .add(mLayoutScreen.getId(), mControlScreenFragment, mControlScreenFragment.TAG)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(mLayoutCarInfo.getId(), mCarStateFragment, mCarStateFragment.TAG)
                .commit();
    }

    private void initData(){
        mMortNetworkData = new MORTNetworkData();
        mMortNetworkData.setType(MORTNetworkData.Type.OPERATION);
    }


//    @Override
//    public MORTNetworkData makeSendData() {
//        return mMortNetworkData;
//    }
//
//    @Override
//    public void receivedData(final MORTNetworkData data) {
//        this.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                dismissProgress();
//                if(data != null){
//                    Log.e(TAG, data.toString());
////                    Toast.makeText(ControlViewActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//    }

//    private MORTDeviceInfoServer.OnReceivedMortDeivceInfoListener onReceivedMortDeivceInfoListener = new MORTDeviceInfoServer.OnReceivedMortDeivceInfoListener() {
//        @Override
//        public void onReceivcedCameraData(final MORTNetworkData data) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    if(mControlScreenFragment != null){
//                        Bitmap bm = BitmapConverter.Base64StringToBitmap(data.getMessage());
//                        mControlScreenFragment.setCameraImage(bm);
//                        bm = null;
//                    }
//                }
//            });
//
//        }
//
//        @Override
//        public void onReceivcedSensorData(MORTNetworkData data) {
//            if(mCarStateFragment != null && data.getMessage() != null){
//                SensorEvent event = mGson.fromJson(data.getMessage(), SensorEvent.class);
//                if(event != null){
//                    mCarStateFragment.onSensorChanged(event);
//                }
//            }
//        }
//    };
}
