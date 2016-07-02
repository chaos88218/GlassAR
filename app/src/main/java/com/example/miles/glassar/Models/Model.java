package com.example.miles.glassar.Models;

import android.util.Log;

import com.example.miles.glassar.LoaderNCalculater.FileReader;
import com.example.miles.glassar.LoaderNCalculater.VectorCal;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2015/11/17.
 */
public class Model {
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;
    private FloatBuffer normalBuffer;

    private float[] vertex;
    private float[] normals;
    private float color[];

    private int Loaded = 0;

    public Model(String string) {
        vertex = FileReader.ReadStlBinary(string);
        if(vertex[0] != -1) {
            normals = VectorCal.getNormByPtArray(vertex);
            color = new float[vertex.length/3*4];
            for(int i = 0; i < color.length ; i = i+4)
            {
                color[i+0] =0.5f;
                color[i+1] =0.5f;
                color[i+2] =0.5f;
                color[i+3] =0.5f;
            }
            System.gc();
            Log.v(string + " loaded: ", "Loaded");
            Loaded = 1;

            vertexBuffer = RenderUtils.buildFloatBuffer(vertex);
            normalBuffer = RenderUtils.buildFloatBuffer(normals);
            colorBuffer = RenderUtils.buildFloatBuffer(color);

        }else {
            Log.v(string + "loaded E*: ", "UnLoaded");
        }
    }

    public int isLoaded(){
        return Loaded;
    }

    public void draw(GL10 gl) {

        gl.glColorPointer(4, gl.GL_FLOAT, 0, colorBuffer);
        gl.glVertexPointer(3, gl.GL_FLOAT, 0, vertexBuffer);
        gl.glNormalPointer(gl.GL_FLOAT, 0, normalBuffer);

        gl.glEnableClientState(gl.GL_COLOR_ARRAY);
        gl.glEnableClientState(gl.GL_VERTEX_ARRAY);
        gl.glEnableClientState(gl.GL_NORMAL_ARRAY);

        gl.glDrawArrays(gl.GL_TRIANGLES, 0, vertex.length / 3);

        gl.glDisableClientState(gl.GL_COLOR_ARRAY);
        gl.glDisableClientState(gl.GL_VERTEX_ARRAY);
        gl.glEnableClientState(gl.GL_NORMAL_ARRAY);

    }
}
