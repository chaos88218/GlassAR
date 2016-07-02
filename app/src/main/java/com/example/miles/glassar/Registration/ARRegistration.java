package com.example.miles.glassar.Registration;

import android.opengl.GLES10;
import android.opengl.Matrix;
import android.util.Log;

import com.example.miles.glassar.MyARRenderer;

import org.artoolkit.ar.base.rendering.RenderUtils;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import static com.example.miles.glassar.LoaderNCalculater.VectorCal.*;

/**
 * Created by miles on 2015/10/16.
 */
public class ARRegistration {
    //TODO: rework transformation from angle and axis to matrix
    private FloatBuffer vertexBuffer;
    private FloatBuffer colorBuffer;

    private FloatBuffer vertexBuffer1;
    private FloatBuffer colorBuffer1;

    public static float[] aRTransVec = new float[3];
    static float[] aRRotationMatrix = new float[16];
    public static float[] aRTransBackVec = new float[3];

    public static float ReAngle1;
    public static float ReAngle2;
    public static float[] ReAxis1;
    public static float[] ReAxis2;

    float[] vnCT = new float[3];
    float[] vnAR = new float[3];

    float color[];
    float color1[];

    public ARRegistration(){

        float[] testpoint = ProMarker.skullMSPoints.clone();
        float[] temp = new float[]{ ProMarker.skullMSPoints[0],  ProMarker.skullMSPoints[1],  ProMarker.skullMSPoints[2]};
        Matrix.setIdentityM(aRRotationMatrix, 0);

        color = new float[ProMarker.skullMSPoints.length/3*4];
        for(int i = 0; i < color.length ; i = i+4)
        {
            color[i+0] =1.0f;
            color[i+1] =0.0f;
            color[i+2] =0.0f;
            color[i+3] =1.0f;
        }

        color1 = new float[Arpoints.skullMSARPoints.length/3*4];
        for(int i = 0; i < color1.length ; i = i+4)
        {
            color1[i+0] =0.0f;
            color1[i+1] =1.0f;
            color1[i+2] =0.0f;
            color1[i+3] =1.0f;
        }

        aRTransVec = new float[]{-temp[0], -temp[1], -temp[2]};
        for(int ii = 0; ii <9; ii+=3)
        {
            ProMarker.skullMSPoints[0+ii] = ProMarker.skullMSPoints[0+ii]-temp[0];
            ProMarker.skullMSPoints[1+ii] = ProMarker.skullMSPoints[1+ii]-temp[1];
            ProMarker.skullMSPoints[2+ii] = ProMarker.skullMSPoints[2+ii]-temp[2];

            testpoint[0+ii] = testpoint[ii+0]-temp[0];
            testpoint[1+ii] = testpoint[ii+1]-temp[1];
            testpoint[2+ii] = testpoint[ii+2]-temp[2];
        }

        vnCT = getNormByPtArray(ProMarker.skullMSPoints);
        vnAR = getNormByPtArray(Arpoints.skullMSARPoints);

        float[] Vec1;
        float[] Vec2;
        Vec1 = getVecByPoint(new float[]{ProMarker.skullMSPoints[0], ProMarker.skullMSPoints[1], ProMarker.skullMSPoints[2]},
                new float[]{ProMarker.skullMSPoints[3], ProMarker.skullMSPoints[4], ProMarker.skullMSPoints[5]});
        normalize(Vec1);
        Vec2 = getVecByPoint(new float[]{Arpoints.skullMSARPoints[0], Arpoints.skullMSARPoints[1], Arpoints.skullMSARPoints[2]},
                new float[]{Arpoints.skullMSARPoints[3], Arpoints.skullMSARPoints[4], Arpoints.skullMSARPoints[5]});
        normalize(Vec2);

        ReAxis1 = cross(Vec1, Vec2);
        ReAngle1 = getAngleDeg(Vec1, Vec2);

        ProMarker.skullMSPoints = rotAngMatrixMultiVec(ProMarker.skullMSPoints, ReAngle1, ReAxis1);
        Matrix.rotateM(aRRotationMatrix, 0, ReAngle1, ReAxis1[0], ReAxis1[1], ReAxis1[2]);//*****************************

        float[] Vec3;
        float[] Vec4;
        vnCT = getNormByPtArray(ProMarker.skullMSPoints);
        Vec1 = getVecByPoint(new float[]{ProMarker.skullMSPoints[0], ProMarker.skullMSPoints[1], ProMarker.skullMSPoints[2]},
                new float[]{ProMarker.skullMSPoints[3], ProMarker.skullMSPoints[4], ProMarker.skullMSPoints[5]});
        normalize(Vec1);
        Vec2 = getVecByPoint(new float[]{Arpoints.skullMSARPoints[0], Arpoints.skullMSARPoints[1], Arpoints.skullMSARPoints[2]},
                new float[]{Arpoints.skullMSARPoints[3], Arpoints.skullMSARPoints[4], Arpoints.skullMSARPoints[5]});
        normalize(Vec2);

        Vec3 = cross(vnCT, Vec1);
        Vec4 = cross(vnAR, Vec2);

        ReAxis2 = cross(Vec3, Vec4);
        ReAngle2 = getAngleDeg(Vec3, Vec4);

        Log.e("ARRegiatration VN1", ReAngle2 + "\n" + ReAxis2[0] + " " + ReAxis2[1] + " " + ReAxis2[2]);
        ProMarker.skullMSPoints = rotAngMatrixMultiVec(ProMarker.skullMSPoints, ReAngle2, ReAxis2);
        Matrix.rotateM(aRRotationMatrix, 0, ReAngle2, ReAxis2[0], ReAxis2[1], ReAxis2[2]);//*****************************

        testpoint = rotMatrixMultiVec(testpoint, aRRotationMatrix);

        for(int ii = 0; ii <9; ii+=3)
        {
            ProMarker.skullMSPoints[0+ii] = ProMarker.skullMSPoints[0+ii]+ Arpoints.skullMSARPoints[0];
            ProMarker.skullMSPoints[1+ii] = ProMarker.skullMSPoints[1+ii]+ Arpoints.skullMSARPoints[1];
            ProMarker.skullMSPoints[2+ii] = ProMarker.skullMSPoints[2+ii]+ Arpoints.skullMSARPoints[2];

            testpoint[0+ii] = testpoint[0+ii]+ Arpoints.skullMSARPoints[0];
            testpoint[1+ii] = testpoint[1+ii]+ Arpoints.skullMSARPoints[1];
            testpoint[2+ii] = testpoint[2+ii]+ Arpoints.skullMSARPoints[2];
        }
        aRTransBackVec = new float[]{Arpoints.skullMSARPoints[0], Arpoints.skullMSARPoints[1], Arpoints.skullMSARPoints[2]};//**************************

        Log.e("ARRegiatration CT", ProMarker.skullMSPoints[0] + " " + ProMarker.skullMSPoints[1] + " " + ProMarker.skullMSPoints[2] + "\n"
                + ProMarker.skullMSPoints[3] + " " + ProMarker.skullMSPoints[4] + " " + ProMarker.skullMSPoints[5] + "\n"
                + ProMarker.skullMSPoints[6] + " " + ProMarker.skullMSPoints[7] + " " + ProMarker.skullMSPoints[8]);

        Log.e("ARRegiatration AR", Arpoints.skullMSARPoints[0] + " " + Arpoints.skullMSARPoints[1] + " " + Arpoints.skullMSARPoints[2] + "\n"
                + Arpoints.skullMSARPoints[3] + " " + Arpoints.skullMSARPoints[4] + " " + Arpoints.skullMSARPoints[5] + "\n"
                + Arpoints.skullMSARPoints[6] + " " + Arpoints.skullMSARPoints[7] + " " + Arpoints.skullMSARPoints[8]);

        MyARRenderer.AllSTLLoadingCheck[7] = 1;

        vertexBuffer = RenderUtils.buildFloatBuffer(ProMarker.skullMSPoints);
        vertexBuffer1 = RenderUtils.buildFloatBuffer(Arpoints.skullMSARPoints);
        colorBuffer = RenderUtils.buildFloatBuffer(color);
        colorBuffer1 = RenderUtils.buildFloatBuffer(color1);
        Log.e("Trans", ARRegistration.aRTransVec[0]+" "+ARRegistration.aRTransVec[1]+" "+ARRegistration.aRTransVec[2]+"\n"
                +" "+ARRegistration.aRTransBackVec[0]+" "+ARRegistration.aRTransBackVec[1]+" "+ARRegistration.aRTransBackVec[2]);
    }

    public void draw(GL10 unused) {

        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glDisable(GLES10.GL_CULL_FACE);
        GLES10.glPointSize(10);
        GLES10.glDrawArrays(GLES10.GL_POINTS, 0, ProMarker.skullMSPoints.length / 3);
        GLES10.glEnable(GLES10.GL_CULL_FACE);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);




        GLES10.glColorPointer(4, GLES10.GL_FLOAT, 0, colorBuffer1);
        GLES10.glVertexPointer(3, GLES10.GL_FLOAT, 0, vertexBuffer1);

        GLES10.glEnableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glEnableClientState(GLES10.GL_VERTEX_ARRAY);

        GLES10.glDisable(GLES10.GL_CULL_FACE);
        GLES10.glPointSize(10);
        GLES10.glDrawArrays(GLES10.GL_POINTS, 0, Arpoints.skullMSARPoints.length / 3);
        GLES10.glEnable(GLES10.GL_CULL_FACE);

        GLES10.glDisableClientState(GLES10.GL_COLOR_ARRAY);
        GLES10.glDisableClientState(GLES10.GL_VERTEX_ARRAY);
    }
}
