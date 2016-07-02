package com.example.miles.glassar.Registration;

import android.opengl.GLES10;
import android.util.Log;

import com.example.miles.glassar.LoaderNCalculater.FileReader;
import com.example.miles.glassar.MyARRenderer;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

import static com.example.miles.glassar.LoaderNCalculater.VectorCal.*;

/**
 * Created by miles on 2015/10/14.
 */
public class Arpoints {

    //TODO: rework transformation from angle and axis to matrix
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private FloatBuffer vertexBuffer1;
    private FloatBuffer colorBuffer1;

    float[] squareCoords;
    float[] aRTagPoints = new float[12];
    static float[] skullMSARPoints = new float[9];

    float[] cTVn = new float[3];
    float[] rotVn = new float[3];
    float rotAng;

    float color[];
    float color1[];

    float ameanX = 0;
    float ameanY = 0;
    float ameanZ = 0;

    public Arpoints(String string) {
        squareCoords = FileReader.ReadArPoints(string);//man_OSP

        if(squareCoords[0] != -1) {
            System.gc();
            Log.v("ApointsLoaded: ", "Loaded");
            MyARRenderer.AllSTLLoadingCheck[6] = 1;

            aRTagPoints = Arrays.copyOfRange(squareCoords, 0, 12);
            skullMSARPoints = Arrays.copyOfRange(squareCoords, 12, 21);

            for(int k = 0; k < aRTagPoints.length ;k++)
            {
                switch ((k+1)%3){
                    case 1:{
                        ameanX = ameanX + aRTagPoints[k];
                        break;
                    }
                    case 2:{
                        ameanY = ameanY + aRTagPoints[k];
                        break;
                    }
                    case 0:{
                        ameanZ = ameanZ + aRTagPoints[k];
                        break;
                    }
                }
            }

            ameanX = ameanX /4;
            ameanY = ameanY /4;
            ameanZ = ameanZ /4;


            color = new float[aRTagPoints.length/3*4];
            for(int i = 0; i < color.length ; i = i+4)
            {
                color[i+0] =1.0f;
                color[i+1] =1.0f;
                color[i+2] =1.0f;
                color[i+3] =1.0f;
            }


            color1 = new float[skullMSARPoints.length/3*4];
            for(int i = 0; i < color1.length ; i = i+4)
            {
                color1[i+0] =1.0f;
                color1[i+1] =0.0f;
                color1[i+2] =0.0f;
                color1[i+3] =1.0f;
            }


            for(int ii = 0; ii <9; ii+=3)
            {
                aRTagPoints[0+ii] = aRTagPoints[0+ii]-ameanX;
                aRTagPoints[1+ii] = aRTagPoints[1+ii]-ameanY;
                aRTagPoints[2+ii] = aRTagPoints[2+ii]-ameanZ;

                skullMSARPoints[0+ii] = skullMSARPoints[0 + ii] - ameanX;
                skullMSARPoints[1 + ii] = skullMSARPoints[1 + ii] - ameanY;
                skullMSARPoints[2 + ii] = skullMSARPoints[2+ii]-ameanZ;
            }


            float[] Vec1;
            float[] Vec2;
            Vec1 = getVecByPoint(new float[]{aRTagPoints[0], aRTagPoints[1], aRTagPoints[2]},
                    new float[]{aRTagPoints[3], aRTagPoints[4], aRTagPoints[5]});
            normalize(Vec1);

            Vec2 = new float[]{1f, 0f, 0f};

            rotVn = cross(Vec1, Vec2);
            rotAng = getAngleDeg(Vec1, Vec2);

            aRTagPoints = rotAngMatrixMultiVec(aRTagPoints, rotAng, rotVn);//*********************
            skullMSARPoints = rotAngMatrixMultiVec(skullMSARPoints, rotAng, rotVn);//*********************

            Vec1 = getVecByPoint(new float[]{aRTagPoints[0], aRTagPoints[1], aRTagPoints[2]},
                    new float[]{aRTagPoints[3], aRTagPoints[4], aRTagPoints[5]});
            normalize(Vec1);

            Vec2 = getVecByPoint(new float[]{aRTagPoints[3], aRTagPoints[4], aRTagPoints[5]},
                    new float[]{aRTagPoints[6], aRTagPoints[7], aRTagPoints[8]});
            normalize(Vec2);

            cTVn = cross(Vec1, Vec2);
            Vec2 = cross(cTVn, Vec1);

            rotAng = getAngleDeg(Vec2, new float[]{0, 1, 0});
            rotVn = cross(Vec2, new float[]{0, 1, 0});

            aRTagPoints = rotAngMatrixMultiVec(aRTagPoints, rotAng, rotVn);//*********************
            skullMSARPoints = rotAngMatrixMultiVec(skullMSARPoints, rotAng, rotVn);//*********************

            Log.e("ARPoint Tag", aRTagPoints[0] + " " + aRTagPoints[1] + " " + aRTagPoints[2] + "\n"
                    + aRTagPoints[3] + " " + aRTagPoints[4] + " " + aRTagPoints[5] + "\n"
                    + aRTagPoints[6] + " " + aRTagPoints[7] + " " + aRTagPoints[8]);

            vertexBuffer = RenderUtils.buildFloatBuffer(aRTagPoints);
            vertexBuffer1 = RenderUtils.buildFloatBuffer(skullMSARPoints);
            colorBuffer = RenderUtils.buildFloatBuffer(color);
            colorBuffer1 = RenderUtils.buildFloatBuffer(color1);

        }else {
            Log.v("ProMarkerLoaded E*: ", "UnLoaded");
        }

    }
    public void draw(GL10 unused) {

        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glDisable(GLES10.GL_CULL_FACE);
        GLES10.glPointSize(10);
        GLES10.glDrawArrays(GLES10.GL_POINTS, 0, aRTagPoints.length / 3);
        GLES10.glEnable(GLES10.GL_CULL_FACE);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);




        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer1);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer1);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glDisable(GLES10.GL_CULL_FACE);
        GLES10.glPointSize(10);
        GLES10.glDrawArrays(GLES10.GL_POINTS, 0, skullMSARPoints.length / 3);
        GLES10.glEnable(GLES10.GL_CULL_FACE);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
    }
}
