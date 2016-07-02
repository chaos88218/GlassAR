package com.example.miles.glassar.Models;

import android.util.Log;

import com.example.miles.glassar.LoaderNCalculater.FileReader;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

/**
 * Created by miles on 2015/11/17.
 */
public class OSP {
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private float[] vertex;
    private float[] color;

    private int Loaded = 0;

    public OSP(String string, float[] colorIn) {
        vertex = FileReader.ReadStlBinary(string);

        color = new float[vertex.length/3*4];
        for(int i = 0; i < color.length ; i = i+4)
        {
            color[i+0] =colorIn[0];
            color[i+1] =colorIn[1];
            color[i+2] =colorIn[2];
            color[i+3] =0.5f;
        }

        if(vertex[0] != -1) {
            System.gc();
            Log.v(string + " loaded: ", "Loaded");
            Loaded = 1;

            vertexBuffer = RenderUtils.buildFloatBuffer(vertex);
            colorBuffer = RenderUtils.buildFloatBuffer(color);

        }else {
            Log.v(string+" loaded E*: ", "UnLoaded");
        }

    }

    public int isLoaded(){
        return Loaded;
    }

    public void draw(GL10 gl) {

        gl.glColorPointer(4, gl.GL_FLOAT, 0, colorBuffer);
        gl.glVertexPointer(3, gl.GL_FLOAT, 0, vertexBuffer);

        gl.glEnableClientState(gl.GL_COLOR_ARRAY);
        gl.glEnableClientState(gl.GL_VERTEX_ARRAY);

        gl.glDisable(gl.GL_CULL_FACE);
        gl.glDrawArrays(gl.GL_TRIANGLES, 0, vertex.length / 3);
        gl.glEnable(gl.GL_CULL_FACE);

        gl.glDisableClientState(gl.GL_COLOR_ARRAY);
        gl.glDisableClientState(gl.GL_VERTEX_ARRAY);

    }
}
