package com.fivetrue.remotecontroller.fragment.control;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.commonsdk.device.data.Sensor;

import min3d.core.Object3d;
import min3d.core.Object3dContainer;
import min3d.core.RendererFragment;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;

/**
 * Created by Fivetrue on 2015-07-08.
 */
public class CarStateInfoFragment extends RendererFragment {

    public static final String TAG = "CarStateInfoFragment";

    private final float MAX_ROTATION = 40;
    private final float MAX_CAM_X = 6f;

    private Object3dContainer mCar;
    private Object3d tireRR;
    private Object3d tireRF;
    private Object3d tireLR;
    private Object3d tireLF;
    private int rotationDirection;
    private float camDirection;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                initScene();
            }
        }, 2000L);
    }

    public void initScene()
    {
        IParser parser = Parser.createParser(Parser.Type.OBJ,
                getResources(), "com.fivetrue.remotecontroller:raw/camaro2_obj", true);
        parser.parse();
        mCar = parser.getParsedObject();
        scene.addChild(mCar);

        tireRR = mCar.getChildByName("tire_rr");
        tireRF = mCar.getChildByName("tire_rf");
        tireLR = mCar.getChildByName("tire_lr");
        tireLF = mCar.getChildByName("tire_lf");

        tireLF.position().x = -.6f;
        tireLF.position().y = 1.11f;
        tireLF.position().z = .3f;

        tireRF.position().x = .6f;
        tireRF.position().y = 1.11f;
        tireRF.position().z = .3f;

        tireRR.position().x = .6f;
        tireRR.position().y = -1.05f;
        tireRR.position().z = .3f;

        tireLR.position().x = -.6f;
        tireLR.position().y = -1.05f;
        tireLR.position().z = .3f;

//        mCar.rotation().x = -90;
//        mCar.rotation().z = 180;

        scene.camera().position.x = MAX_CAM_X;
        scene.camera().position.z = 3.5f;
        scene.camera().position.y = 3.5f;

        Light light = new Light();
        light.position.setAllFrom(scene.camera().position);
        scene.lights().add(light);

        rotationDirection = 1;
        camDirection = -.01f;
    }

    private static final float VALUE = 0.1f;
    private static final int MAX = 1;
    float operand = 0.1f;
    float angleRotateX = 0;
    float angleRotateZ = 0;
    public void onSensorChanged(Sensor sensor) {
        // TODO Auto-generated method stub
        if (sensor != null && sensor.type != null) {
            switch (sensor.type) {
                case GRAVITY:
                    float rz = sensor.x * 10;
                    float rx = sensor.z > 0 ? (sensor.y - 10) * 10 : Math.abs((sensor.y - 10)) * 10;
                    mCar.rotation().z = rz;
                    mCar.rotation().x = rx;

                    break;
            }
        }
    }

    @Override
    public void updateScene() {
//        tireRF.rotation().z += rotationDirection;
//        tireLF.rotation().z += rotationDirection;
//
//        if(Math.abs(tireRF.rotation().z) >= MAX_ROTATION)
//            rotationDirection = -rotationDirection;
//
//        scene.camera().position.x += camDirection;
//        scene.lights().get(0).position.setAllFrom(scene.camera().position);
//
//        if(Math.abs(scene.camera().position.x) >= MAX_CAM_X)
//            camDirection = -camDirection;
    }
}
