package com.fivetrue.remotecontroller.fragment.control;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.fivetrue.remotecontroller.R;
import com.fivetrue.remotecontroller.activity.TestViewActivity;


public class ControlScreenFragment extends Fragment {

    public static final String TAG = "ControlFragment";

//    private Button mHandleButton = null;
//    private Button mEngineButton = null;
//    private Button mClockButton = null;
//    private Button mLeftButton = null;
//    private Button mRightButton = null;
    private ImageView mIvCamera = null;


    public ControlScreenFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = initViews(inflater);
        return view;
    }

    private View initViews(LayoutInflater inflater){
        View view = inflater.inflate(R.layout.layout_controlview, null);

//        mHandleButton = (Button) view.findViewById(R.id.btn_controlview_handle);
//        mEngineButton = (Button) view.findViewById(R.id.btn_controlview_engine);
//        mClockButton = (Button) view.findViewById(R.id.btn_controlview_crock);
//        mLeftButton = (Button) view.findViewById(R.id.btn_controlview_left);
//        mRightButton = (Button) view.findViewById(R.id.btn_controlview_right);
        mIvCamera = (ImageView) view.findViewById(R.id.iv_camera);

//        mHandleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), TestViewActivity.class);
//                startActivity(intent);
////                mMortNetworkData.setType(MORTNetworkData.TYPE_OPERATION);
////                mMortNetworkData.setAction(MORTNetworkData.OPERATION_CENTER_LED);
////                sendData(ControlViewActivity.this);
//            }
//        });
//
//        mEngineButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMortNetworkData.setType(MORTNetworkData.TYPE_OPERATION);
//                mMortNetworkData.setAction(MORTNetworkData.OPERATION_MOTOR);
//                sendData(ControlViewActivity.this);
//            }
//        });
//
//        mClockButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mMortNetworkData.setType(MORTNetworkData.TYPE_OPERATION);
//                mMortNetworkData.setAction(MORTNetworkData.OPERATION_BUZZER);
//                sendData(ControlViewActivity.this);
//            }
//        });
//
//        mLeftButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(value > 0){
//                    mMortNetworkData.setType(MORTNetworkData.TYPE_OPERATION);
//                    mMortNetworkData.setAction(MORTNetworkData.OPERATION_SERVO);
//                    mMortNetworkData.setExtra(String.valueOf(value -= 0.01f));
//                    sendData(ControlViewActivity.this);
//                }
//            }
//        });
//
//        mRightButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(value < 0.1f){
//                    mMortNetworkData.setType(MORTNetworkData.TYPE_OPERATION);
//                    mMortNetworkData.setAction(MORTNetworkData.OPERATION_SERVO);
//                    mMortNetworkData.setExtra(String.valueOf(value += 0.01f));
//                    sendData(ControlViewActivity.this);
//                }
//            }
//        });
        return view;
    }

    public void setCameraImage(Bitmap image){
        if(image != null && mIvCamera != null && !image.isRecycled()){
            mIvCamera.setImageBitmap(image);
        }
    }

}
