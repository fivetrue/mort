package com.fivetrue.remotecontroller.opengl.polygon;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Cube extends Mesh {


	private float[] vertexArray = {  // Vertices of the 6 faces
			// FRONT
			-1.0f, -1.0f,  1.0f,  // 0. left-bottom-front
			1.0f, -1.0f,  1.0f,  // 1. right-bottom-front
			-1.0f,  1.0f,  1.0f,  // 2. left-top-front
			1.0f,  1.0f,  1.0f,  // 3. right-top-front
			// BACK
			1.0f, -1.0f, -1.0f,  // 6. right-bottom-back
			-1.0f, -1.0f, -1.0f,  // 4. left-bottom-back
			1.0f,  1.0f, -1.0f,  // 7. right-top-back
			-1.0f,  1.0f, -1.0f,  // 5. left-top-back
			// LEFT
			-1.0f, -1.0f, -1.0f,  // 4. left-bottom-back
			-1.0f, -1.0f,  1.0f,  // 0. left-bottom-front 
			-1.0f,  1.0f, -1.0f,  // 5. left-top-back
			-1.0f,  1.0f,  1.0f,  // 2. left-top-front
			// RIGHT
			1.0f, -1.0f,  1.0f,  // 1. right-bottom-front
			1.0f, -1.0f, -1.0f,  // 6. right-bottom-back
			1.0f,  1.0f,  1.0f,  // 3. right-top-front
			1.0f,  1.0f, -1.0f,  // 7. right-top-back
			// TOP
			-1.0f,  1.0f,  1.0f,  // 2. left-top-front
			1.0f,  1.0f,  1.0f,  // 3. right-top-front
			-1.0f,  1.0f, -1.0f,  // 5. left-top-back
			1.0f,  1.0f, -1.0f,  // 7. right-top-back
			// BOTTOM
			-1.0f, -1.0f, -1.0f,  // 4. left-bottom-back
			1.0f, -1.0f, -1.0f,  // 6. right-bottom-back
			-1.0f, -1.0f,  1.0f,  // 0. left-bottom-front
			1.0f, -1.0f,  1.0f   // 1. right-bottom-front
	};

	private float[][] colors = {  // Colors of the 6 faces
			{1.0f, 0.5f, 0.0f, 1.0f},  // 0. orange
			{1.0f, 0.0f, 1.0f, 1.0f},  // 1. violet
			{0.0f, 1.0f, 0.0f, 1.0f},  // 2. green
			{0.0f, 0.0f, 1.0f, 1.0f},  // 3. blue
			{1.0f, 0.0f, 0.0f, 1.0f},  // 4. red
			{1.0f, 1.0f, 0.0f, 1.0f}   // 5. yellow
	};

	float[] texCoords = { // Texture coords for the above face (NEW)
			0.0f, 1.0f,  // A. left-bottom (NEW)
			1.0f, 1.0f,  // B. right-bottom (NEW)
			0.0f, 0.0f,  // C. left-top (NEW)
			1.0f, 0.0f   // D. right-top (NEW)
	};
	
	private Bitmap[] textureImage = null;
	
	private FloatBuffer vertextBuffer = null;
	
	int numOfFace = 6;
	
	Bitmap[] images = new Bitmap[numOfFace];
	int[] textureIDs = new int[numOfFace];   // Array for 1 texture-ID (NEW)

	private FloatBuffer textureBuffer = null;

	public Cube() {
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexArray.length * 4);
		vbb.order(ByteOrder.nativeOrder()); // Use native byte order
		vertextBuffer = vbb.asFloatBuffer(); // Convert from byte to float
		vertextBuffer.put(vertexArray);         // Copy data into buffer
		vertextBuffer.position(0);           // Rewind

		
		// Setup texture-coords-array buffer, in float. An float has 4 bytes (NEW)
		ByteBuffer tbb = ByteBuffer.allocateDirect(texCoords.length * 4 * 6);
		tbb.order(ByteOrder.nativeOrder());
		textureBuffer = tbb.asFloatBuffer();
		for(int face = 0 ; face < numOfFace ; face ++){
			textureBuffer.put(texCoords);
		}
		textureBuffer.position(0);
		
	}

	@Override
	public void draw(GL10 gl) {
		// TODO Auto-generated method stub
		gl.glTranslatef(status.x, status.y, status.z);
		gl.glRotatef(status.rx, 1, 0, 0);
		gl.glRotatef(status.ry, 0, 1, 0);
		gl.glRotatef(status.rz, 0, 0, 1);

		gl.glFrontFace(GL10.GL_CCW);    // Front face in counter-clockwise orientation
		gl.glEnable(GL10.GL_CULL_FACE); // Enable cull face
		gl.glCullFace(GL10.GL_BACK);    // Cull the back face (don't display)

		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertextBuffer);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);

		for (int face = 0; face < numOfFace; face++) {
			// Set the color for each of the faces
			// Draw the primitive from the vertex-array directly
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[face]);
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, face * 4, 4);
		}
		
		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisable(GL10.GL_CULL_FACE);
	}

	@Override
	public int getNumOfFace() {
		// TODO Auto-generated method stub
		return numOfFace;
	}

	@Override
	public float[] getVertexArray() {
		// TODO Auto-generated method stub
		return vertexArray;
	}

	@Override
	public float[] getColorArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadTexture(GL10 gl, Context context) {
		// TODO Auto-generated method stub
		if(gl != null && textureImage != null && textureImage.length >= numOfFace){
			gl.glGenTextures(numOfFace, textureIDs, 0); // Generate texture-ID array
			for(int i = 0 ; i < numOfFace ; i ++){
				gl.glBindTexture(GL10.GL_TEXTURE_2D, textureIDs[i]);   // Bind to texture ID
				// Set up texture filters
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
				gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
				// Build Texture from loaded bitmap for the currently-bind texture ID
				if(textureImage[i] != null){
					GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, textureImage[i], 0);
				}
			}
		}
	}
	
	public void setTextureBitmaps(Bitmap[] bitmaps){
		textureImage = bitmaps;
	}
	
	public Bitmap[] getTextureBitmaps(){
		if(textureImage == null){
			textureImage = new Bitmap[numOfFace];
		}
		return textureImage;
	}
}
