package com.example.miles.glassar.Registration;

import android.opengl.GLES10;
import android.util.Log;

import com.example.miles.glassar.LoaderNCalculater.FileReader;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;
import java.util.Arrays;

import javax.microedition.khronos.opengles.GL10;

import static com.example.miles.glassar.LoaderNCalculater.VectorCal.cross;
import static com.example.miles.glassar.LoaderNCalculater.VectorCal.getAngleDeg;
import static com.example.miles.glassar.LoaderNCalculater.VectorCal.getNormByPtArray;
import static com.example.miles.glassar.LoaderNCalculater.VectorCal.getVecByPoint;
import static com.example.miles.glassar.LoaderNCalculater.VectorCal.normalize;
import static com.example.miles.glassar.LoaderNCalculater.VectorCal.rotAngMatrixMultiVec;

/**
 * Created by miles on 2015/10/8.
 */
public class ProMarker {
    //TODO: rework transformation from angle and axis to matrix
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private FloatBuffer vertexBuffer1;
    private FloatBuffer colorBuffer1;

    private FloatBuffer vertexBuffer2;
    private FloatBuffer colorBuffer2;

    int loaded = 0;
    float[] squareCoords;

    float[] ballsCTPoints = new float[9];
    float[] ballsMSPoints = new float[9];
    float[] skullMSPoints = new float[9];//skull marker on

    float[] vnCT = new float[3];
    float[] vnMS = new float[3];
    float[] rotVn = new float[3];
    float rotAng;

    float color[];
    float color1[];

    public ProMarker(String string) {
        squareCoords = FileReader.ReadProjectSkullMarker(string);//man_OSP

        if (squareCoords[0] != -1) {
            System.gc();
            Log.v("ProMarker1Loaded: ", "Loaded");
            loaded = 1;

            ballsCTPoints = Arrays.copyOfRange(squareCoords, 0, 9);
            ballsMSPoints = Arrays.copyOfRange(squareCoords, 9, 18);
            skullMSPoints = Arrays.copyOfRange(squareCoords, 18, 27);
            float[] temp = new float[]{ballsMSPoints[0], ballsMSPoints[1], ballsMSPoints[2]};

            color = new float[squareCoords.length / 3 * 4];
            for (int i = 0; i < color.length; i = i + 4) {
                color[i + 0] = 0.0f;
                color[i + 1] = 0.0f;
                color[i + 2] = 1.0f;
                color[i + 3] = 1.0f;
            }

            color1 = new float[ballsMSPoints.length / 3 * 4];
            for (int i = 0; i < color1.length; i = i + 4) {
                color1[i + 0] = 1.0f;
                color1[i + 1] = 0.0f;
                color1[i + 2] = 0.0f;
                color1[i + 3] = 1.0f;
            }

            float[] color2 = new float[]{
                    0, 1, 0, 1,
                    0, 1, 0, 1,
                    0, 1, 0, 1,
            };


            for (int ii = 0; ii < 9; ii += 3) {
                ballsMSPoints[0 + ii] = ballsMSPoints[0 + ii] - temp[0];
                ballsMSPoints[1 + ii] = ballsMSPoints[1 + ii] - temp[1];
                ballsMSPoints[2 + ii] = ballsMSPoints[2 + ii] - temp[2];

                skullMSPoints[0 + ii] = skullMSPoints[0 + ii] - temp[0];
                skullMSPoints[1 + ii] = skullMSPoints[1 + ii] - temp[1];
                skullMSPoints[2 + ii] = skullMSPoints[2 + ii] - temp[2];
            }

            vnCT = getNormByPtArray(ballsCTPoints);
            vnMS = getNormByPtArray(ballsMSPoints);

            float[] Vec1;
            float[] Vec2;
            Vec1 = getVecByPoint(new float[]{ballsMSPoints[0], ballsMSPoints[1], ballsMSPoints[2]},
                    new float[]{ballsMSPoints[3], ballsMSPoints[4], ballsMSPoints[5]});
            normalize(Vec1);
            Vec2 = getVecByPoint(new float[]{ballsCTPoints[0], ballsCTPoints[1], ballsCTPoints[2]},
                    new float[]{ballsCTPoints[3], ballsCTPoints[4], ballsCTPoints[5]});
            normalize(Vec2);

            rotVn = cross(Vec1, Vec2);
            rotAng = getAngleDeg(Vec1, Vec2);

            skullMSPoints = rotAngMatrixMultiVec(skullMSPoints, rotAng, rotVn);//*********************
            ballsMSPoints = rotAngMatrixMultiVec(ballsMSPoints, rotAng, rotVn);//*********************

            float[] Vec3;
            float[] Vec4;

            vnMS = getNormByPtArray(ballsMSPoints);
            Vec1 = getVecByPoint(new float[]{ballsMSPoints[0], ballsMSPoints[1], ballsMSPoints[2]},
                    new float[]{ballsMSPoints[3], ballsMSPoints[4], ballsMSPoints[5]});
            normalize(Vec1);
            Vec2 = getVecByPoint(new float[]{ballsCTPoints[0], ballsCTPoints[1], ballsCTPoints[2]},
                    new float[]{ballsCTPoints[3], ballsCTPoints[4], ballsCTPoints[5]});
            normalize(Vec2);

            Vec3 = cross(vnMS, Vec1);
            Vec4 = cross(vnCT, Vec2);

            rotAng = getAngleDeg(Vec3, Vec4);
            rotVn = cross(Vec3, Vec4);

            skullMSPoints = rotAngMatrixMultiVec(skullMSPoints, rotAng, rotVn);//*********************
            ballsMSPoints = rotAngMatrixMultiVec(ballsMSPoints, rotAng, rotVn);//*********************

            for (int ii = 0; ii < 9; ii += 3) {
                ballsMSPoints[0 + ii] = ballsMSPoints[0 + ii] + ballsCTPoints[0];
                ballsMSPoints[1 + ii] = ballsMSPoints[1 + ii] + ballsCTPoints[1];
                ballsMSPoints[2 + ii] = ballsMSPoints[2 + ii] + ballsCTPoints[2];

                skullMSPoints[0 + ii] = skullMSPoints[0 + ii] + ballsCTPoints[0];
                skullMSPoints[1 + ii] = skullMSPoints[1 + ii] + ballsCTPoints[1];
                skullMSPoints[2 + ii] = skullMSPoints[2 + ii] + ballsCTPoints[2];
            }

            Log.e("ProMarker MS", ballsMSPoints[0] + " " + ballsMSPoints[1] + " " + ballsMSPoints[2] + "\n"
                    + ballsMSPoints[3] + " " + ballsMSPoints[4] + " " + ballsMSPoints[5] + "\n"
                    + ballsMSPoints[6] + " " + ballsMSPoints[7] + " " + ballsMSPoints[8]);

            Log.e("ProMarker CT", ballsCTPoints[0] + " " + ballsCTPoints[1] + " " + ballsCTPoints[2] + "\n"
                    + ballsCTPoints[3] + " " + ballsCTPoints[4] + " " + ballsCTPoints[5] + "\n"
                    + ballsCTPoints[6] + " " + ballsCTPoints[7] + " " + ballsCTPoints[8]);

            vertexBuffer = RenderUtils.buildFloatBuffer(squareCoords);
            vertexBuffer1 = RenderUtils.buildFloatBuffer(ballsMSPoints);
            vertexBuffer2 = RenderUtils.buildFloatBuffer(skullMSPoints);
            colorBuffer = RenderUtils.buildFloatBuffer(color);
            colorBuffer1 = RenderUtils.buildFloatBuffer(color1);
            colorBuffer2 = RenderUtils.buildFloatBuffer(color2);

        } else {
            Log.v("ProMarkerLoaded E*: ", "UnLoaded");
        }

    }

    public float[] get_skullMSPoints() {
        return skullMSPoints;
    }

    public int isLoaded(){
        return loaded;
    }

    public void draw(GL10 unused) {

        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glDisable(GLES10.GL_CULL_FACE);
        GLES10.glPointSize(10);
        GLES10.glDrawArrays(GLES10.GL_POINTS, 0, squareCoords.length / 3);
        GLES10.glEnable(GLES10.GL_CULL_FACE);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);


        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer1);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer1);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glDisable(GLES10.GL_CULL_FACE);
        GLES10.glPointSize(10);
        GLES10.glDrawArrays(GLES10.GL_POINTS, 0, ballsMSPoints.length / 3);
        GLES10.glEnable(GLES10.GL_CULL_FACE);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);


        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer2);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer2);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glDisable(GLES10.GL_CULL_FACE);
        GLES10.glPointSize(10);
        GLES10.glDrawArrays(GLES10.GL_POINTS, 0, skullMSPoints.length / 3);
        GLES10.glEnable(GLES10.GL_CULL_FACE);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
    }
}
