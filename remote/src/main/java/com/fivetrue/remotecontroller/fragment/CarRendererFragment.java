package com.fivetrue.remotecontroller.fragment;

import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.opengl.GLSurfaceView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.remotecontroller.R;
import com.fivetrue.remotecontroller.fragment.BasePictureFragment;
import com.fivetrue.remotecontroller.opengl.polygon.Cube;
import com.fivetrue.remotecontroller.opengl.polygon.Mesh;

import min3d.core.Object3dContainer;
import min3d.core.RendererFragment;
import min3d.objectPrimitives.SkyBox;
import min3d.parser.IParser;
import min3d.parser.Max3DSParser;
import min3d.parser.Parser;
import min3d.vos.Light;


public class CarRendererFragment extends RendererFragment{

    private SkyBox mSkyBox;
    public static final String TAG = "CarRendererFragment";
    private Object3dContainer mTruck;

	public CarRendererFragment(){
	}
	
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
//		if(event != null){
//			if(event.sensor.getType() == Sensor.TYPE_GRAVITY){
//				if(mOpenGlRenderer != null && mOpenGlRenderer.meshState != null){
//                    float x =  event.values[0];
//                    float y =  event.values[1];
//                    float z =  event.values[2];
////                    Log.e(TAG, "event.values[0] x = " + event.values[0]);
////                    Log.e(TAG, "event.values[1] y = " + event.values[1]);
////                    Log.e(TAG, "event.values[2] z = " + event.values[2]);
//                    float rz = x * 10;
//                    float rx = z > 0 ? (y - 10) * 10 : Math.abs((y - 10)) * 10;
//                    mOpenGlRenderer.meshState.rz = rz;
//                    mOpenGlRenderer.meshState.rx =rx;
////                    Log.e(TAG, "rz = " + rz);
////                    Log.e(TAG, "rx = " + rx);
//				}
//			}
//		}
	}


    @Override
    public void initScene() {
        scene.lights().add(new Light());
//
//        mSkyBox = new SkyBox(5.0f, 2);
//        mSkyBox.addTexture(SkyBox.Face.North, R.drawable.car_top, 	"north");
//        mSkyBox.addTexture(SkyBox.Face.East, 	R.drawable.car_left, 	"east");
//        mSkyBox.addTexture(SkyBox.Face.South, 	R.drawable.car_back, 	"south");
//        mSkyBox.addTexture(SkyBox.Face.West, 	R.drawable.car_left, 	"west");
//        mSkyBox.addTexture(SkyBox.Face.Up,		R.drawable.car_top, 	"up");
//        mSkyBox.addTexture(SkyBox.Face.Down, 	R.drawable.car_left, 	"down");
//        mSkyBox.scale().y = 0.8f;
//        mSkyBox.scale().z = 2.0f;
//        scene.addChild(mSkyBox);

        IParser parser = Parser.createParser(Parser.Type.MAX_3DS,
                getResources(), "com.fivetrue.remotecontroller:raw/truck", false);
        parser.parse();

        mTruck = parser.getParsedObject();
        mTruck.scale().x = mTruck.scale().y = mTruck.scale().z  = 1.0f;
        scene.addChild(mTruck);
        scene.camera().target = mTruck.position();

//        mTruck.scale().x = mTruck.scale().y = mTruck.scale().z  = .5f;
//        mTruck.position().y = -10;
//        scene.addChild(mTruck);
//        scene.camera().target = mTruck.position();
    }
}
