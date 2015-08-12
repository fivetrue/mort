package com.fivetrue.remotecontroller.fragment;

import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fivetrue.remotecontroller.opengl.OpenGLRenderer;
import com.fivetrue.remotecontroller.opengl.polygon.Mesh;


abstract public class BasePictureFragment extends Fragment{
	
	protected OpenGLRenderer mOpenGlRenderer = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mOpenGlRenderer = new OpenGLRenderer(getActivity());
		mOpenGlRenderer.setMesh(getMesh());
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			 ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		GLSurfaceView view = new GLSurfaceView(getActivity());
		view.setRenderer(mOpenGlRenderer);
		return view;
	}

	public OpenGLRenderer getOpenGLRenderer(){
		return mOpenGlRenderer;
	}
	
	public abstract Mesh getMesh();

}
