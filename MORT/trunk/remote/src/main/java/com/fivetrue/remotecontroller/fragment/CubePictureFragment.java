package com.fivetrue.remotecontroller.fragment;

import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.fivetrue.remotecontroller.opengl.polygon.Cube;
import com.fivetrue.remotecontroller.opengl.polygon.Mesh;


public class CubePictureFragment extends BasePictureFragment{

    public static final String TAG = "CubePictureFragment";
	private Cube mCube = null;

	public CubePictureFragment(){
		mCube = new Cube();
		mCube.status.rx = 1.0f;
		mCube.status.ry = 1.0f;
		mCube.status.rz = 1.0f;
	}
	
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		if(event != null){
			if(event.sensor.getType() == Sensor.TYPE_GRAVITY){
				if(mOpenGlRenderer != null && mOpenGlRenderer.meshState != null){
                    float x =  event.values[0];
                    float y =  event.values[1];
                    float z =  event.values[2];
//                    Log.e(TAG, "event.values[0] x = " + event.values[0]);
//                    Log.e(TAG, "event.values[1] y = " + event.values[1]);
//                    Log.e(TAG, "event.values[2] z = " + event.values[2]);
                    float rz = x * 10;
                    float rx = z > 0 ? (y - 10) * 10 : Math.abs((y - 10)) * 10;
                    mOpenGlRenderer.meshState.rz = rz;
                    mOpenGlRenderer.meshState.rx =rx;
//                    Log.e(TAG, "rz = " + rz);
//                    Log.e(TAG, "rx = " + rx);
				}
			}
		} 
	}

	@Override
	public Mesh getMesh() {
		// TODO Auto-generated method stub
		return mCube;
	}
	
	public void setCubePictrues(Bitmap[] bitmaps){
        mCube.setTextureBitmaps(bitmaps);
	}
	
	public Cube getCube(){
		return mCube;
	}
	
	public synchronized void setCubePictrue(Bitmap bm){
		int idx = mCube.getTextureBitmaps().length;
        mCube.getTextureBitmaps()[idx] = bm;
	}
}
