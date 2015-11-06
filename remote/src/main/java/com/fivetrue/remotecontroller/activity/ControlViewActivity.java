package com.fivetrue.remotecontroller.activity;

import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.commonsdk.device.control.ControlOperation;
import com.fivetrue.commonsdk.device.data.Camera;
import com.fivetrue.commonsdk.device.data.Sensor;
import com.fivetrue.commonsdk.network.data.MORTNetworkData;
import com.fivetrue.commonsdk.service.network.client.IMORTClientNetworkService;
import com.fivetrue.remotecontroller.R;
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

    private ControlOperation  mOperation = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        mOperation =  new ControlOperation();
    }

    private void initViews(){
        mLayoutScreen = (ViewGroup) findViewById(R.id.layout_screen);
        mLayoutCarInfo = (ViewGroup) findViewById(R.id.layout_car_infomation);
        mLayoutSystemInfo  = (ViewGroup) findViewById(R.id.layout_system_infomation);

        mControlScreenFragment = new ControlScreenFragment();
        mCarStateFragment = new CarStateInfoFragment();

        findViewById(R.id.btn_speaker).setOnClickListener(onClickListener);
        findViewById(R.id.btn_up).setOnClickListener(onClickListener);
        findViewById(R.id.btn_function1).setOnClickListener(onClickListener);
        findViewById(R.id.btn_left).setOnClickListener(onClickListener);
        findViewById(R.id.btn_stop).setOnClickListener(onClickListener);
        findViewById(R.id.btn_right).setOnClickListener(onClickListener);
        findViewById(R.id.btn_function2).setOnClickListener(onClickListener);
        findViewById(R.id.btn_down).setOnClickListener(onClickListener);
        findViewById(R.id.btn_function3).setOnClickListener(onClickListener);


        getSupportFragmentManager().beginTransaction()
                .add(mLayoutScreen.getId(), mControlScreenFragment, mControlScreenFragment.TAG)
                .commit();

        getSupportFragmentManager().beginTransaction()
                .add(mLayoutCarInfo.getId(), mCarStateFragment, mCarStateFragment.TAG)
                .commit();
    }

    private void initData(){
        mMortNetworkData = new MORTNetworkData();
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mOperation != null){
                int x = 0;
                int y = 0;
                switch(v.getId()){
                    case R.id.btn_speaker:
                        break;
                    case R.id.btn_up :
                        y = 1;
                        break;
                    case R.id.btn_function1:
                        break;
                    case R.id.btn_function2:
                        break;
                    case R.id.btn_down :
                        y = -1;
                        break;
                    case R.id.btn_function3:
                        break;
                    case R.id.btn_left:
                        x = -1;
                        break;
                    case R.id.btn_right:
                        x = 1;
                        break;

                    case R.id.btn_stop :
                    default:
                        y = 0;
                        x = 0;
                        break;
                }
                mOperation.setY(y);
                mOperation.setX(x);
                mMortNetworkData.setType(MORTNetworkData.Type.OPERATION);
                mMortNetworkData.setMessage(mGson.toJson(mOperation));
                sendData(mMortNetworkData);
            }
        }
    };
//    private View.OnTouchListener onTouchLayerListener = new View.OnTouchListener() {
//        @Override
//        public boolean onTouch(View v, MotionEvent event) {
//            if(event != null){
//                switch(event.getAction()){
//                    case MotionEvent.ACTION_DOWN :
//                        isTouchLayer = true;
//                        mOperation.setX(getPositionX(event.getX()));
//                        mOperation.setY(getPositionY(event.getY()));
//                        return true;
//
//                    case MotionEvent.ACTION_MOVE :
//                        if(isTouchLayer){
//                            mOperation.setX(getPositionX(event.getX()));
//                            mOperation.setY(getPositionY(event.getY()));
//                        }
//                        return true;
//
//                    case MotionEvent.ACTION_UP:
//                    case MotionEvent.ACTION_CANCEL:
//                        isTouchLayer = false;
//                        mMortNetworkData.setType(MORTNetworkData.Type.OPERATION);
//                        mMortNetworkData.setMessage(mGson.toJson(mOperation));
//                        sendData(mMortNetworkData);
//                        return true;
//                }
//            }
//            return false;
//        }
//    };

    private int getPositionX(float x){
        double px = Math.floor((double)(x / (getResources().getDisplayMetrics().widthPixels / ControlOperation.TOUCH_ARRAY_COUNT)));
        px = px - Math.floor(ControlOperation.TOUCH_ARRAY_COUNT / 2);
        return (int) px;
    }

    private int getPositionY(float y){
        double py = Math.floor((double) (y / (getResources().getDisplayMetrics().heightPixels / ControlOperation.TOUCH_ARRAY_COUNT)));
        py =  Math.floor(ControlOperation.TOUCH_ARRAY_COUNT / 2) - py;
        return (int)py;
    }

    @Override
    protected void onServiceBind(IMORTClientNetworkService service) {
        super.onServiceBind(service);
        showProgress("확인 중", "확인 중.....");
        try {
            service.searchDevice();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onConnected(String ip, MORTNetworkData data) {
        super.onConnected(ip, data);
        dismissProgress();
        initViews();
        initData();
    }

    @Override
    protected void onFailConnect() {
        super.onFailConnect();
        dismissProgress();
    }

    @Override
    protected void onCameraData(Camera camera) {
        super.onCameraData(camera);
        if(mControlScreenFragment != null){
            mControlScreenFragment.setCameraData(camera);
        }
    }

    @Override
    protected void onSensorData(Sensor sensor) {
        super.onSensorData(sensor);
        if(mCarStateFragment != null){
            mCarStateFragment.onSensorChanged(sensor);
        }
    }

    @Override
    protected void onReceivedData(MORTNetworkData data) {
        super.onReceivedData(data);
    }

    synchronized private void sendData(MORTNetworkData data){
        if(getClientService() != null){
            try {
                getClientService().sendData(mGson.toJson(data));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

}
