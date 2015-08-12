package com.fivetrue.remotecontroller.opengl.polygon;

import android.content.Context;

import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

public class Group extends Mesh {
	
	private Vector<Mesh> children = new Vector<Mesh>();
	
	
	@Override
	public void draw(GL10 gl) {
		// TODO Auto-generated method stub
		for(int i = 0 ; i < children.size() ; i ++){
			children.get(i).draw(gl);
		}
	}
	public boolean add(Mesh mesh){
		return children.add(mesh);
	}
	
	public boolean remove(Mesh mesh){
		return children.remove(mesh);
	}
	
	public Mesh get(int location){
		return children.get(location);
	}
	
	public void clear(){
		children.clear();
	}
	
	public int size(){
		return children.size();
	}
	@Override
	public int getNumOfFace() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public float[] getVertexArray() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public float[] getColorArray() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void loadTexture(GL10 gl, Context context) {
		// TODO Auto-generated method stub
		
	}
}
